#!/bin/bash

# AWS EC2 Deployment Script for Invoice App
set -e

# Variables - UPDATE THESE WITH YOUR AWS DETAILS
EC2_IP="YOUR_EC2_PUBLIC_IP"  # Replace with your EC2 public IP
SSH_KEY="your-key.pem"       # Replace with your SSH key file name
DB_PASSWORD="SecurePass123!" # Replace with a secure password

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_status() { echo -e "${GREEN}[INFO]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

echo "=== AWS EC2 Invoice App Deployment ==="

# Step 1: Build application
print_status "Step 1: Building application..."
mvn clean package -DskipTests

# Step 2: Upload files to EC2
print_status "Step 2: Uploading files to EC2..."
scp -i $SSH_KEY target/invoice-app-0.0.1-SNAPSHOT.jar ubuntu@$EC2_IP:/home/ubuntu/
scp -i $SSH_KEY deployment/complete-deployment.sh ubuntu@$EC2_IP:/home/ubuntu/

# Step 3: Create customized deployment script on EC2
print_status "Step 3: Creating deployment script on EC2..."
ssh -i $SSH_KEY ubuntu@$EC2_IP << 'ENDSSH'
#!/bin/bash
set -e

# Variables
DOMAIN="$EC2_IP"
DB_PASSWORD="SecurePass123!"
APP_JAR_PATH="./invoice-app-0.0.1-SNAPSHOT.jar"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_status() { echo -e "${GREEN}[INFO]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

echo "=== Invoice App EC2 Deployment ==="

# System Setup
print_status "Setting up system..."
sudo apt update && sudo apt upgrade -y
sudo apt install -y openjdk-17-jdk postgresql postgresql-contrib nginx

# Create application user and directories
print_status "Creating application user and directories..."
sudo useradd -r -m -U -d /opt/invoice-app -s /bin/bash invoice || true
sudo mkdir -p /opt/invoice-app /var/log/invoice-app
sudo chown invoice:invoice /opt/invoice-app /var/log/invoice-app

# Database setup
print_status "Setting up PostgreSQL database..."
sudo -u postgres psql << EOF
CREATE DATABASE invoice_app_prod;
CREATE USER invoice_user WITH ENCRYPTED PASSWORD '$DB_PASSWORD';
GRANT ALL PRIVILEGES ON DATABASE invoice_app_prod TO invoice_user;
\c invoice_app_prod;
GRANT ALL ON SCHEMA public TO invoice_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO invoice_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO invoice_user;
EOF

# Deploy application
print_status "Deploying application..."
sudo cp "$APP_JAR_PATH" /opt/invoice-app/invoice-app.jar
sudo chown invoice:invoice /opt/invoice-app/invoice-app.jar

# Create systemd service
print_status "Creating systemd service..."
sudo tee /etc/systemd/system/invoice-app.service > /dev/null << EOF
[Unit]
Description=Invoice App Spring Boot Application
After=network.target postgresql.service
Requires=postgresql.service

[Service]
Type=simple
User=invoice
Group=invoice
WorkingDirectory=/opt/invoice-app
ExecStart=/usr/bin/java -jar /opt/invoice-app/invoice-app.jar --spring.profiles.active=prod
ExecStop=/bin/kill -15 \$MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=invoice-app

Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
Environment=DB_PASSWORD=$DB_PASSWORD
Environment=SPRING_PROFILES_ACTIVE=prod

NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ProtectHome=true
ReadWritePaths=/var/log/invoice-app
ReadWritePaths=/opt/invoice-app

[Install]
WantedBy=multi-user.target
EOF

# SSL Certificate
print_status "Creating SSL certificate..."
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/ssl/private/invoice-app.key \
    -out /etc/ssl/certs/invoice-app.crt \
    -subj "/C=US/ST=State/L=City/O=Invoice App/CN=$DOMAIN"

sudo chmod 600 /etc/ssl/private/invoice-app.key
sudo chmod 644 /etc/ssl/certs/invoice-app.crt

# Nginx configuration
print_status "Configuring Nginx..."
sudo tee /etc/nginx/sites-available/invoice-app > /dev/null << EOF
server {
    listen 80;
    server_name $DOMAIN;
    return 301 https://\$server_name\$request_uri;
}

server {
    listen 443 ssl http2;
    server_name $DOMAIN;

    ssl_certificate /etc/ssl/certs/invoice-app.crt;
    ssl_certificate_key /etc/ssl/private/invoice-app.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_prefer_server_ciphers on;

    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 30s;
        proxy_send_timeout 30s;
        proxy_read_timeout 30s;
    }

    location /api/invoices {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
EOF

# Enable site
sudo ln -sf /etc/nginx/sites-available/invoice-app /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# Start services
print_status "Starting services..."
sudo systemctl daemon-reload
sudo systemctl enable invoice-app
sudo systemctl start invoice-app

sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx

# Firewall
print_status "Configuring firewall..."
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw --force enable

print_status "Deployment completed successfully!"
echo ""
print_status "Your application is now available at:"
echo "  HTTPS: https://$DOMAIN"
echo "  HTTP:  http://$DOMAIN (redirects to HTTPS)"
echo ""
print_status "API Endpoints:"
echo "  Dealers:  https://$DOMAIN/api/dealers"
echo "  Vehicles: https://$DOMAIN/api/vehicles"
echo "  Invoices: https://$DOMAIN/api/invoices"
echo "  Health:   https://$DOMAIN/actuator/health"

ENDSSH

print_status "Deployment script created on EC2!"
print_warning "Please provide your EC2 details to continue:"
echo "1. EC2 Public IP address"
echo "2. SSH key file name"
echo "3. (Optional) Domain name"

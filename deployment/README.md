# Invoice App Deployment Guide

This guide will help you deploy your Spring Boot Invoice Application to Ubuntu with Nginx reverse proxy, SSL, and systemd service.

## Prerequisites

- Ubuntu 20.04+ server (AWS Free Tier, Oracle Cloud, or your own server)
- Domain name or public IP address
- SSH access to the server

## Quick Deployment

### Option 1: Automated Deployment (Recommended)

1. **Build your application:**
   ```bash
   mvn clean package
   ```

2. **Upload files to server:**
   ```bash
   scp target/invoice-app-0.0.1-SNAPSHOT.jar user@your-server:/home/user/
   scp deployment/complete-deployment.sh user@your-server:/home/user/
   ```

3. **Run deployment script:**
   ```bash
   ssh user@your-server
   chmod +x complete-deployment.sh
   # Edit the script to set your domain and password
   nano complete-deployment.sh
   ./complete-deployment.sh
   ```

### Option 2: Manual Step-by-Step Deployment

#### Step 1: Prepare Your Application

1. **Build the JAR file:**
   ```bash
   mvn clean package -Pprod
   ```

2. **Upload to server:**
   ```bash
   scp target/invoice-app-0.0.1-SNAPSHOT.jar user@your-server:/home/user/
   ```

#### Step 2: Server Setup

1. **Connect to your server:**
   ```bash
   ssh user@your-server
   ```

2. **Run basic setup:**
   ```bash
   chmod +x deployment/deploy.sh
   ./deployment/deploy.sh
   ```

#### Step 3: Database Setup

1. **Set up PostgreSQL:**
   ```bash
   sudo -u postgres psql < deployment/database-setup.sql
   ```

2. **Update password in the SQL file before running**

#### Step 4: Application Deployment

1. **Copy JAR file:**
   ```bash
   sudo cp invoice-app-0.0.1-SNAPSHOT.jar /opt/invoice-app/invoice-app.jar
   sudo chown invoice:invoice /opt/invoice-app/invoice-app.jar
   ```

2. **Create systemd service:**
   ```bash
   sudo cp deployment/invoice-app.service /etc/systemd/system/
   # Edit the service file to set your database password
   sudo nano /etc/systemd/system/invoice-app.service
   ```

3. **Start the service:**
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable invoice-app
   sudo systemctl start invoice-app
   ```

#### Step 5: SSL Certificate

1. **Create SSL certificate:**
   ```bash
   chmod +x deployment/ssl-setup.sh
   sudo ./deployment/ssl-setup.sh
   ```

#### Step 6: Nginx Configuration

1. **Configure Nginx:**
   ```bash
   sudo cp deployment/nginx-config /etc/nginx/sites-available/invoice-app
   # Edit the config to set your domain
   sudo nano /etc/nginx/sites-available/invoice-app
   ```

2. **Enable site:**
   ```bash
   sudo ln -s /etc/nginx/sites-available/invoice-app /etc/nginx/sites-enabled/
   sudo rm /etc/nginx/sites-enabled/default
   sudo nginx -t
   sudo systemctl restart nginx
   ```

#### Step 7: Firewall

```bash
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

## Configuration Files

### Production Application Properties

The `application-prod.properties` file contains:
- Production database configuration
- Logging configuration
- Security settings
- Actuator endpoints

### Environment Variables

Set these in your systemd service:
- `DB_PASSWORD`: Your PostgreSQL password
- `SPRING_PROFILES_ACTIVE=prod`

## URLs After Deployment

- **Main Application**: `https://your-domain.com`
- **API Endpoints**:
  - Dealers: `https://your-domain.com/api/dealers`
  - Vehicles: `https://your-domain.com/api/vehicles`
  - Invoices: `https://your-domain.com/api/invoices`
- **Health Check**: `https://your-domain.com/actuator/health`

## Service Management

```bash
# Check status
sudo systemctl status invoice-app

# View logs
sudo journalctl -u invoice-app -f

# Restart service
sudo systemctl restart invoice-app

# Stop service
sudo systemctl stop invoice-app
```

## Nginx Management

```bash
# Test configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx

# View Nginx logs
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

## Troubleshooting

### Application Won't Start

1. Check logs: `sudo journalctl -u invoice-app -f`
2. Verify database connection
3. Check Java installation: `java -version`

### Nginx Issues

1. Test config: `sudo nginx -t`
2. Check ports: `sudo netstat -tlnp | grep :80`
3. Verify SSL certificate paths

### Database Connection Issues

1. Check PostgreSQL status: `sudo systemctl status postgresql`
2. Test connection: `sudo -u postgres psql -c "\l"`
3. Verify user permissions

## Security Considerations

- Change default passwords
- Use strong SSL certificates (Let's Encrypt recommended for production)
- Regular security updates
- Monitor application logs
- Set up log rotation

## Monitoring

The application includes:
- Health check endpoint at `/actuator/health`
- Metrics at `/actuator/metrics`
- Application logs in `/var/log/invoice-app/`

## Backup Strategy

1. Database backups: `pg_dump invoice_app_prod > backup.sql`
2. Application logs rotation
3. SSL certificate renewal reminders

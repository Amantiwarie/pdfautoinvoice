#!/bin/bash

# SSL Certificate Setup Script
set -e

echo "=== SSL Certificate Setup ==="

DOMAIN="your-domain.com"  # Replace with your domain
CERT_DIR="/etc/ssl/certs"
KEY_DIR="/etc/ssl/private"

# Create self-signed certificate
print_status() {
    echo -e "\033[0;32m[INFO]\033[0m $1"
}

print_status "Creating self-signed SSL certificate..."

# Create certificate
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout $KEY_DIR/invoice-app.key \
    -out $CERT_DIR/invoice-app.crt \
    -subj "/C=US/ST=State/L=City/O=Organization/OU=OrgUnit/CN=$DOMAIN"

# Set proper permissions
sudo chmod 600 $KEY_DIR/invoice-app.key
sudo chmod 644 $CERT_DIR/invoice-app.crt

print_status "SSL certificate created successfully!"
print_status "Certificate: $CERT_DIR/invoice-app.crt"
print_status "Private Key: $KEY_DIR/invoice-app.key"

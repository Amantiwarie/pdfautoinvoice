#!/bin/bash

# Invoice App Deployment Script for Ubuntu
set -e

echo "=== Invoice App Deployment Script ==="

# Variables
APP_NAME="invoice-app"
APP_USER="invoice"
APP_DIR="/opt/invoice-app"
SERVICE_NAME="invoice-app"
NGINX_SITE="invoice-app"
DOMAIN="your-domain.com"  # Replace with your domain or IP

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if running as root
if [[ $EUID -eq 0 ]]; then
   print_error "This script should not be run as root for security reasons"
   exit 1
fi

# Update system
print_status "Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install Java 17
print_status "Installing Java 17..."
sudo apt install -y openjdk-17-jdk

# Install PostgreSQL
print_status "Installing PostgreSQL..."
sudo apt install -y postgresql postgresql-contrib

# Install Nginx
print_status "Installing Nginx..."
sudo apt install -y nginx

# Install Maven (for building)
print_status "Installing Maven..."
sudo apt install -y maven

# Create application user
print_status "Creating application user..."
sudo useradd -r -m -U -d /opt/invoice-app -s /bin/bash $APP_USER || true

# Create application directory
print_status "Creating application directory..."
sudo mkdir -p $APP_DIR
sudo chown $APP_USER:$APP_USER $APP_DIR

# Create log directory
print_status "Creating log directory..."
sudo mkdir -p /var/log/invoice-app
sudo chown $APP_USER:$APP_USER /var/log/invoice-app

print_status "Basic system setup completed!"
print_warning "Next steps:"
echo "1. Copy your application JAR to $APP_DIR"
echo "2. Set up PostgreSQL database"
echo "3. Configure systemd service"
echo "4. Configure Nginx with SSL"

-- PostgreSQL Database Setup for Production
-- Run this as postgres user: sudo -u postgres psql

-- Create database
CREATE DATABASE invoice_app_prod;

-- Create user
CREATE USER invoice_user WITH ENCRYPTED PASSWORD 'InvoiceApp2025!';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE invoice_app_prod TO invoice_user;

-- Connect to the database
\c invoice_app_prod;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO invoice_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO invoice_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO invoice_user;

-- Set default privileges for future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO invoice_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO invoice_user;

CREATE TABLE customer.customers (
    id UUID PRIMARY KEY,
    customer_number VARCHAR(50) UNIQUE NOT NULL,
    legal_name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    tax_identifier VARCHAR(100) NULL,
    industry VARCHAR(100) NULL,
    notes TEXT NULL
);
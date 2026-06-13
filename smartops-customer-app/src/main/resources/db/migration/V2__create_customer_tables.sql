CREATE TABLE IF NOT EXISTS customer.customers (
    id UUID PRIMARY KEY,
    customer_number VARCHAR(50) UNIQUE NOT NULL,
    legal_name VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    tax_identifier VARCHAR(100) NOT NULL,
    industry VARCHAR(100) NOT NULL,
    notes TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS customer.customer_sites (
    id UUID PRIMARY KEY,
    site_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id UUID NOT NULL,
    site_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state_region VARCHAR(100),
    postal_code VARCHAR(30) NOT NULL,
    country_code VARCHAR(10) NOT NULL,
    timezone VARCHAR(100),
    access_instructions TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE customer.customer_sites
ADD CONSTRAINT fk_customer_sites_customer
FOREIGN KEY (customer_id) REFERENCES customer.customers(id);

CREATE TABLE IF NOT EXISTS customer.customer_contacts (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    contact_role VARCHAR(100),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);


ALTER TABLE customer.customer_contacts
    ADD CONSTRAINT fk_customer_contacts_customer
    FOREIGN KEY (customer_id) REFERENCES customer.customers(id);

CREATE INDEX IF NOT EXISTS idx_customer_contacts_customer_id
    ON customer.customer_contacts(customer_id);

CREATE UNIQUE INDEX IF NOT EXISTS uk_customer_contacts_customer_email
    ON customer.customer_contacts(customer_id, email);

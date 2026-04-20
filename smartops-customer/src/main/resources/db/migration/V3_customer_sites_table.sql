CREATE TABLE customer.customer_sites (
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
    version VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE customer.customer_sites
ADD CONSTRAINT fk_customer_sites_customer
FOREIGN KEY (customer_id) REFERENCES customer.customers(id);s
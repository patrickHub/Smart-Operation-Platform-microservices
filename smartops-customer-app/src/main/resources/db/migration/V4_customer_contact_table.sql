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
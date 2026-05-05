CREATE SCHEMA IF NOT EXISTS billing;

CREATE TABLE IF NOT EXISTS billing.pricing_policies (
    id UUID PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(150) NOT NULL,
    work_type VARCHAR(50) NOT NULL,
    labor_rate NUMERIC(12,2) NOT NULL,
    default_currency VARCHAR(10) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    valid_from DATE NOT NULL,
    valid_to DATE NULL,
    rules_json JSONB NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS billing.invoices (
    id UUID PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    work_order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    pricing_policy_id UUID NULL,
    status VARCHAR(50) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    subtotal_amount NUMERIC(12,2) NOT NULL,
    tax_amount NUMERIC(12,2) NOT NULL,
    total_amount NUMERIC(12,2) NOT NULL,
    generated_at TIMESTAMPTZ NOT NULL,
    due_date DATE NULL,
    version BIGINT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT fk_invoices_pricing_policy
        FOREIGN KEY (pricing_policy_id) REFERENCES billing.pricing_policies(id)
);

CREATE TABLE IF NOT EXISTS billing.invoice_lines (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL,
    line_number INT NOT NULL,
    line_type VARCHAR(50) NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity NUMERIC(10,2) NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    line_total NUMERIC(12,2) NOT NULL,
    CONSTRAINT fk_invoice_lines_invoice
        FOREIGN KEY (invoice_id) REFERENCES billing.invoices(id)
);

CREATE TABLE IF NOT EXISTS billing.outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_payload JSONB NOT NULL,
    event_key VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ NULL,
    retry_count INT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_invoices_work_order_id
    ON billing.invoices(work_order_id);

CREATE INDEX IF NOT EXISTS idx_invoices_customer_id
    ON billing.invoices(customer_id);

CREATE INDEX IF NOT EXISTS idx_invoices_status
    ON billing.invoices(status);

CREATE INDEX IF NOT EXISTS idx_invoice_lines_invoice_id
    ON billing.invoice_lines(invoice_id);

CREATE INDEX IF NOT EXISTS idx_billing_outbox_status
    ON billing.outbox_events(status);
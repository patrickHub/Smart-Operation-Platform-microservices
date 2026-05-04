CREATE SCHEMA IF NOT EXISTS workorder;

CREATE TABLE IF NOT EXISTS workorder.work_orders (
    id UUID PRIMARY KEY,
    work_order_number VARCHAR(50) UNIQUE NOT NULL,
    asset_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    site_id UUID NOT NULL,
    created_by_user_id UUID NOT NULL,
    assigned_technician_user_id UUID NULL,
    type VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    requested_date TIMESTAMPTZ NULL,
    scheduled_start TIMESTAMPTZ NULL,
    scheduled_end TIMESTAMPTZ NULL,
    completed_at TIMESTAMPTZ NULL,
    cancellation_reason TEXT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS workorder.work_order_tasks (
    id UUID PRIMARY KEY,
    work_order_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    task_order INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT fk_work_order_tasks_work_order
        FOREIGN KEY (work_order_id) REFERENCES workorder.work_orders(id)
);

CREATE TABLE IF NOT EXISTS workorder.work_order_assignments (
    id UUID PRIMARY KEY,
    work_order_id UUID NOT NULL,
    technician_user_id UUID NOT NULL,
    assigned_by_user_id UUID NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL,
    assignment_status VARCHAR(50) NOT NULL,
    reason TEXT NULL,
    CONSTRAINT fk_work_order_assignments_work_order
        FOREIGN KEY (work_order_id) REFERENCES workorder.work_orders(id)
);

CREATE TABLE IF NOT EXISTS workorder.intervention_reports (
    id UUID PRIMARY KEY,
    work_order_id UUID NOT NULL UNIQUE,
    technician_user_id UUID NOT NULL,
    started_at TIMESTAMPTZ NULL,
    completed_at TIMESTAMPTZ NULL,
    labor_duration_minutes INT NULL,
    summary TEXT NOT NULL,
    actions_performed TEXT NULL,
    result_status VARCHAR(50) NOT NULL,
    follow_up_required BOOLEAN NOT NULL DEFAULT FALSE,
    follow_up_notes TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT fk_intervention_reports_work_order
        FOREIGN KEY (work_order_id) REFERENCES workorder.work_orders(id)
);

CREATE TABLE IF NOT EXISTS workorder.used_parts (
    id UUID PRIMARY KEY,
    intervention_report_id UUID NOT NULL,
    part_number VARCHAR(100) NOT NULL,
    part_name VARCHAR(255) NOT NULL,
    quantity NUMERIC(10,2) NOT NULL,
    unit_price NUMERIC(12,2) NULL,
    currency VARCHAR(10) NULL,
    CONSTRAINT fk_used_parts_intervention_report
        FOREIGN KEY (intervention_report_id) REFERENCES workorder.intervention_reports(id)
);

CREATE TABLE IF NOT EXISTS workorder.outbox_events (
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

CREATE INDEX IF NOT EXISTS idx_work_orders_asset_id ON workorder.work_orders(asset_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_customer_id ON workorder.work_orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_site_id ON workorder.work_orders(site_id);
CREATE INDEX IF NOT EXISTS idx_work_orders_status ON workorder.work_orders(status);
CREATE INDEX IF NOT EXISTS idx_work_orders_priority ON workorder.work_orders(priority);
CREATE INDEX IF NOT EXISTS idx_work_orders_assigned_technician ON workorder.work_orders(assigned_technician_user_id);

CREATE INDEX IF NOT EXISTS idx_work_order_tasks_work_order_id ON workorder.work_order_tasks(work_order_id);
CREATE INDEX IF NOT EXISTS idx_work_order_assignments_work_order_id ON workorder.work_order_assignments(work_order_id);
CREATE INDEX IF NOT EXISTS idx_outbox_events_status ON workorder.outbox_events(status);
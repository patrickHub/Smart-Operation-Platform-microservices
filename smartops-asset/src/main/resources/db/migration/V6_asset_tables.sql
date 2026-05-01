CREATE SCHEMA IF NOT EXISTS asset;

CREATE TABLE asset.assets (
    id UUID PRIMARY KEY,
    asset_number VARCHAR(50) UNIQUE NOT NULL,
    serial_number VARCHAR(100) UNIQUE NOT NULL,
    asset_type_id UUID NOT NULL,
    site_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    installation_date DATE,
    warranty_end_date DATE,
    status VARCHAR(50) NOT NULL,
    criticality VARCHAR(50),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    updated_by VARCHAR(100) NOT NULL
);

CREATE TABLE asset.asset_history (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_timestamp TIMESTAMPTZ NOT NULL,
    actor_id UUID,
    summary TEXT NOT NULL,
    payload_json JSONB,
    created_at TIMESTAMPTZ NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES asset.assets(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_asset_number
    ON asset.assets(asset_number);

CREATE UNIQUE INDEX IF NOT EXISTS uk_serial_number
    ON asset.assets(serial_number);

FOREIGN KEY (asset_type_id) REFERENCES asset.asset_types(id);
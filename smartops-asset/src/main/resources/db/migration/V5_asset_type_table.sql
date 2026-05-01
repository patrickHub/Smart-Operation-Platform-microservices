
CREATE TABLE IF NOT EXISTS asset.asset_types (
    id UUID PRIMARY KEY
    code VARCHAR(50) UNIQUE NOT NULL
    name VARCHAR(150) NOT NULL
    manufacturer VARCHAR(150) NULL
    model VARCHAR(150) NULL
    description TEXT NULL
    created_at TIMESTAMPTZ NOT NULL
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_asset_type_code
    ON asset.asset_types(code);
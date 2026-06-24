CREATE TABLE identity.users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE identity.user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(100) NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES identity.users(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_identity_users_username
    ON identity.users(username);

CREATE INDEX idx_identity_users_email
    ON identity.users(email);

CREATE INDEX idx_identity_user_roles_user_id
    ON identity.user_roles(user_id);
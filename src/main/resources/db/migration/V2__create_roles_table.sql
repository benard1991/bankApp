CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255) NOT NULL UNIQUE,
    user_type VARCHAR(255),
    can_register_accounts BOOLEAN DEFAULT FALSE,
    needs_next_of_kin BOOLEAN DEFAULT FALSE
);

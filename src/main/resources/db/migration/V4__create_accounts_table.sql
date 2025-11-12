CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    account_type VARCHAR(255) NOT NULL,
    balance DOUBLE DEFAULT 0.00,
    user_id BIGINT,
    CONSTRAINT fk_accounts_user_id FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

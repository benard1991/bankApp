CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE,
    status VARCHAR(255),
    transaction_type VARCHAR(255),
    transfer_channel VARCHAR(255),
    destination_bank VARCHAR(255),
    account_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    transaction_date DATETIME,
    reference_number VARCHAR(255) NOT NULL UNIQUE,
    source_account VARCHAR(255),
    destination_account VARCHAR(255),
    created_at DATETIME,
    CONSTRAINT fk_transactions_account_id FOREIGN KEY (account_id)
        REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

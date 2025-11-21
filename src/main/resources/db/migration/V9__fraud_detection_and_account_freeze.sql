-- Add account status to users
ALTER TABLE users
ADD COLUMN account_status ENUM('ACTIVE','FROZEN') DEFAULT 'ACTIVE';

-- Add fraud detection columns to transactions
ALTER TABLE transactions
ADD COLUMN flagged BOOLEAN DEFAULT FALSE,
ADD COLUMN flag_reason VARCHAR(255);

CREATE TABLE fraud_alerts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    transaction_id BIGINT,
    alert_type VARCHAR(50),
    alert_details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

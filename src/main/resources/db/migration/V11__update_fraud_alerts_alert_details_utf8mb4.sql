
ALTER TABLE accounts
MODIFY COLUMN balance DECIMAL(19,2) NOT NULL DEFAULT 0.00;

-- Update alert_details column to support utf8mb4 for proper special character storage
ALTER TABLE fraud_alerts
MODIFY alert_details TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

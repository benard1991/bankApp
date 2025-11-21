
ALTER TABLE transactions
ADD COLUMN ip VARCHAR(45) NULL AFTER destination_account;

CREATE INDEX idx_transactions_ip ON transactions(ip);

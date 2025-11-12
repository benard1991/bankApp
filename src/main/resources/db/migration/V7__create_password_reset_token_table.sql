CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    otp VARCHAR(255) NOT NULL,
    expiry_time DATETIME NOT NULL,
    user_id BIGINT UNIQUE,
    CONSTRAINT fk_password_reset_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    access_token VARCHAR(2000) NOT NULL,
    refresh_token VARCHAR(2000) NOT NULL,
    access_token_expiration DATETIME,
    refresh_token_expiration DATETIME,
    token_type VARCHAR(50),
    status VARCHAR(50),
    issued_at DATETIME,
    user_id BIGINT,
    CONSTRAINT fk_token_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_trail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(255),
    action VARCHAR(255),
    entity_type VARCHAR(255),
    entity_id VARCHAR(255),
    old_value VARCHAR(2000),
    new_value VARCHAR(2000),
    action_description VARCHAR(2000),
    ip_address VARCHAR(100),
    status VARCHAR(50),
    performed_at DATETIME,
    CONSTRAINT fk_audit_trail_user_id FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE SET NULL
);

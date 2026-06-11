CREATE TABLE conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    style VARCHAR(50) NOT NULL,
    design_type VARCHAR(50) NOT NULL,
    description TEXT,
    current_image_url LONGTEXT,
    current_description TEXT,
    created_at DATETIME NOT NULL
);

CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    image_urls LONGTEXT,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_message_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversation(id)
        ON DELETE CASCADE
);

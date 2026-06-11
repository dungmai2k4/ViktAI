package com.viktai.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String style;

    @Column(name = "design_type", length = 50, nullable = false)
    private String designType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "current_image_url", columnDefinition = "LONGTEXT")
    private String currentImageUrl;

    @Column(name = "current_description", columnDefinition = "TEXT")
    private String currentDescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Message> messages = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
    }

    public Long getId() { return id; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public String getDesignType() { return designType; }
    public void setDesignType(String designType) { this.designType = designType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCurrentImageUrl() { return currentImageUrl; }
    public void setCurrentImageUrl(String currentImageUrl) { this.currentImageUrl = currentImageUrl; }
    public String getCurrentDescription() { return currentDescription; }
    public void setCurrentDescription(String currentDescription) { this.currentDescription = currentDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Message> getMessages() { return messages; }
}

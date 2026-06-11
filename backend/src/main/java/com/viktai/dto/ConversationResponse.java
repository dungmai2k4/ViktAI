package com.viktai.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ConversationResponse(
        Long id,
        String style,
        String designType,
        String description,
        String currentImageUrl,
        String currentDescription,
        LocalDateTime createdAt,
        List<MessageResponse> messages
) {
}

package com.viktai.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MessageResponse(Long id, String role, String content, List<String> imageUrls, LocalDateTime createdAt) {
}

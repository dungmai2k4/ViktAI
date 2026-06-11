package com.viktai.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(@NotBlank String message) {
}

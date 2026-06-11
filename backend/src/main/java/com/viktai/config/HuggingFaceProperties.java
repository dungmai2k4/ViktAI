package com.viktai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.hugging-face")
public record HuggingFaceProperties(String token, String modelUrl, boolean mockEnabled) {
}

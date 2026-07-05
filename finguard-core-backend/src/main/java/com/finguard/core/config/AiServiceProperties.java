package com.finguard.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "finguard.ai-service")
public record AiServiceProperties(String baseUrl) {
}

package com.finguard.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(AiServiceProperties.class)
public class WebClientConfig {

    @Bean
    public WebClient aiWebClient(WebClient.Builder builder, AiServiceProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .build();
    }
}

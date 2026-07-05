package com.finguard.core.service;

import com.finguard.core.dto.AiRiskRequest;
import com.finguard.core.dto.AiRiskResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AiRiskClient implements RiskClient {

    private final WebClient aiWebClient;

    public AiRiskClient(WebClient aiWebClient) {
        this.aiWebClient = aiWebClient;
    }

    @Override
    public AiRiskResponse predictRisk(AiRiskRequest request) {
        return aiWebClient.post()
                .uri("/predict")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiRiskResponse.class)
                .block();
    }
}

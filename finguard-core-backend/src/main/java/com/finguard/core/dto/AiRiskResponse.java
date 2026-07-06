package com.finguard.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiRiskResponse(
        @JsonProperty("transaction_id")
        Long transactionId,

        @JsonProperty("risk_score")
        double riskScore,

        @JsonProperty("is_anomaly")
        boolean anomaly,

        @JsonProperty("risk_level")
        String riskLevel,

        @JsonProperty("what_changed")
        String whatChanged,

        @JsonProperty("safest_next_step")
        String safestNextStep
) {
}

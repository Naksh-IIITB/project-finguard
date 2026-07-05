package com.finguard.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiRiskRequest(
        @JsonProperty("transaction_id")
        Long transactionId,

        double amount,

        @JsonProperty("hour_of_day")
        int hourOfDay,

        @JsonProperty("category_encoded")
        int categoryEncoded,

        @JsonProperty("amount_vs_avg")
        double amountVsAverage
) {
}

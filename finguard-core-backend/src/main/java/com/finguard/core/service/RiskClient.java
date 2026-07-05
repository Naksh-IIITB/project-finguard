package com.finguard.core.service;

import com.finguard.core.dto.AiRiskRequest;
import com.finguard.core.dto.AiRiskResponse;

public interface RiskClient {

    AiRiskResponse predictRisk(AiRiskRequest request);
}

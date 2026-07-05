from app.models.schemas import PredictionRequest, PredictionResponse


def score_transaction(request: PredictionRequest) -> PredictionResponse:
    risk_score = calculate_mock_risk_score(request)

    return PredictionResponse(
        transaction_id=request.transaction_id,
        risk_score=risk_score,
        is_anomaly=risk_score >= 0.70,
    )


def calculate_mock_risk_score(request: PredictionRequest) -> float:
    score = 0.10

    if request.amount_vs_avg >= 5:
        score += 0.55
    elif request.amount_vs_avg >= 3:
        score += 0.35
    elif request.amount_vs_avg >= 2:
        score += 0.20

    if request.amount >= 5000:
        score += 0.20
    elif request.amount >= 2500:
        score += 0.10

    if request.hour_of_day < 5:
        score += 0.10

    return round(min(score, 1.0), 4)

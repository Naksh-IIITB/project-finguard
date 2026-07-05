from app.models.schemas import PredictionRequest
from app.services.predictor import score_transaction


def test_low_risk_transaction_is_not_anomaly():
    request = PredictionRequest(
        transaction_id=1,
        amount=200.0,
        hour_of_day=14,
        category_encoded=2,
        amount_vs_avg=1.0,
    )

    response = score_transaction(request)

    assert response.transaction_id == 1
    assert 0 <= response.risk_score <= 1
    assert response.risk_score < 0.60
    assert response.is_anomaly is False


def test_high_ratio_transaction_is_anomaly():
    request = PredictionRequest(
        transaction_id=2,
        amount=6000.0,
        hour_of_day=2,
        category_encoded=4,
        amount_vs_avg=6.0,
    )

    response = score_transaction(request)

    assert response.risk_score > 0.50
    assert response.is_anomaly is True

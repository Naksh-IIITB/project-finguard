from fastapi.testclient import TestClient
from main import app

client = TestClient(app)


def test_health_endpoint():
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {"status": "UP", "service": "finguard-ai-engine"}


def test_predict_endpoint():
    response = client.post(
        "/predict",
        json={
            "transaction_id": 1024,
            "amount": 2500.0,
            "hour_of_day": 10,
            "category_encoded": 3,
            "amount_vs_avg": 3.4,
        },
    )

    assert response.status_code == 200
    assert response.json() == {
        "transaction_id": 1024,
        "risk_score": 0.55,
        "is_anomaly": False,
    }

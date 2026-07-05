from app.models.schemas import PredictionRequest, PredictionResponse
from app.config import MODEL_PATH
from app.services.feature_builder import build_feature_vector
import joblib

_model = None


def load_model():
    global _model
    if _model is None:
        _model = joblib.load(MODEL_PATH)
    return _model


def score_transaction(request: PredictionRequest) -> PredictionResponse:
    model = load_model()
    features = [build_feature_vector(request)]
    prediction = model.predict(features)[0]
    anomaly_score = model.decision_function(features)[0]
    risk_score = convert_anomaly_score_to_risk(anomaly_score)

    return PredictionResponse(
        transaction_id=request.transaction_id,
        risk_score=risk_score,
        is_anomaly=bool(prediction == -1),
    )


def convert_anomaly_score_to_risk(anomaly_score: float) -> float:
    risk = 0.5 - anomaly_score
    return round(max(0.0, min(1.0, risk)), 4)

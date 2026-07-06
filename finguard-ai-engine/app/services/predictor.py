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
    risk_level = classify_risk_level(risk_score, bool(prediction == -1))

    return PredictionResponse(
        transaction_id=request.transaction_id,
        risk_score=risk_score,
        is_anomaly=bool(prediction == -1),
        risk_level=risk_level,
        what_changed=build_change_explanation(request, risk_level),
        safest_next_step=build_next_step(request, risk_level),
    )


def convert_anomaly_score_to_risk(anomaly_score: float) -> float:
    risk = 0.5 - anomaly_score
    return round(max(0.0, min(1.0, risk)), 4)


def classify_risk_level(risk_score: float, is_anomaly: bool) -> str:
    if risk_score >= 0.75 or is_anomaly:
        return "Urgent"
    if risk_score >= 0.55:
        return "Attention"
    if risk_score >= 0.35:
        return "Watch"
    return "Stable"


def build_change_explanation(request: PredictionRequest, risk_level: str) -> str:
    signals = []
    if request.amount_vs_avg >= 4:
        signals.append(f"amount is {request.amount_vs_avg:.1f}x your usual category average")
    elif request.amount_vs_avg >= 2:
        signals.append(f"amount is {request.amount_vs_avg:.1f}x above the normal range")

    if request.hour_of_day < 5 or request.hour_of_day > 22:
        signals.append("time is outside your typical daytime activity")

    if not signals:
        signals.append("pattern is close to your normal transaction history")

    return f"{risk_level} signal: " + "; ".join(signals) + "."


def build_next_step(request: PredictionRequest, risk_level: str) -> str:
    if risk_level == "Urgent":
        return "Verify the merchant now, freeze the card if this was not you, and contact support before more charges post."
    if risk_level == "Attention":
        return "Review the receipt and merchant, then mark it expected or follow up with your bank."
    if risk_level == "Watch":
        return "Keep it on your watchlist and compare it with upcoming bills before spending again."
    return "No action needed; keep monitoring for changes."

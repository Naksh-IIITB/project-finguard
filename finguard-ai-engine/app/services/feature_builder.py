from app.models.schemas import PredictionRequest


def build_feature_vector(request: PredictionRequest) -> list[float]:
    return [
        request.amount,
        float(request.hour_of_day),
        float(request.category_encoded),
        request.amount_vs_avg,
    ]

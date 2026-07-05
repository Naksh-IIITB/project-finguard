from app.models.schemas import PredictionRequest
from app.services.feature_builder import build_feature_vector


def test_build_feature_vector_uses_expected_order():
    request = PredictionRequest(
        transaction_id=7,
        amount=1250.5,
        hour_of_day=22,
        category_encoded=3,
        amount_vs_avg=2.75,
    )

    assert build_feature_vector(request) == [1250.5, 22.0, 3.0, 2.75]

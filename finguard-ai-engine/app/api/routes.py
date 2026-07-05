from app.models.schemas import PredictionRequest, PredictionResponse
from app.services.predictor import score_transaction
from fastapi import APIRouter

router = APIRouter()


@router.get("/health")
def health() -> dict[str, str]:
    return {"status": "UP", "service": "finguard-ai-engine"}


@router.post("/predict", response_model=PredictionResponse)
def predict(request: PredictionRequest) -> PredictionResponse:
    return score_transaction(request)

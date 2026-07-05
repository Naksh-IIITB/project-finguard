from pydantic import BaseModel, Field


class PredictionRequest(BaseModel):
    transaction_id: int = Field(ge=1)
    amount: float = Field(gt=0)
    hour_of_day: int = Field(ge=0, le=23)
    category_encoded: int = Field(ge=0)
    amount_vs_avg: float = Field(ge=0)


class PredictionResponse(BaseModel):
    transaction_id: int
    risk_score: float = Field(ge=0, le=1)
    is_anomaly: bool

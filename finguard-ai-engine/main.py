from app.api.routes import router
from fastapi import FastAPI

app = FastAPI(
    title="FinGuard AI Engine",
    description="Transaction risk scoring service for FinGuard.",
    version="0.1.0",
)

app.include_router(router)

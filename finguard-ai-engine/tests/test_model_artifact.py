from app.config import MODEL_PATH


def test_model_artifact_exists():
    assert MODEL_PATH.exists()

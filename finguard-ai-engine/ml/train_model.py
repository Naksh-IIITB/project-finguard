from pathlib import Path

import joblib
import numpy as np
from sklearn.ensemble import IsolationForest
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler

MODEL_PATH = Path(__file__).resolve().parent / "model.pkl"
RANDOM_SEED = 42


def build_training_data() -> np.ndarray:
    rng = np.random.default_rng(RANDOM_SEED)

    normal_amounts = rng.lognormal(mean=6.0, sigma=0.55, size=900)
    normal_hours = rng.integers(7, 23, size=900)
    normal_categories = rng.integers(1, 9, size=900)
    normal_amount_vs_avg = rng.normal(loc=1.0, scale=0.35, size=900).clip(0.05, 2.2)

    unusual_amounts = rng.lognormal(mean=8.4, sigma=0.55, size=100)
    unusual_hours = rng.choice([0, 1, 2, 3, 4, 23], size=100)
    unusual_categories = rng.integers(0, 9, size=100)
    unusual_amount_vs_avg = rng.normal(loc=5.5, scale=1.4, size=100).clip(2.5, 12.0)

    normal = np.column_stack(
        [normal_amounts, normal_hours, normal_categories, normal_amount_vs_avg]
    )
    unusual = np.column_stack(
        [unusual_amounts, unusual_hours, unusual_categories, unusual_amount_vs_avg]
    )

    return np.vstack([normal, unusual])


def train_model() -> Pipeline:
    training_data = build_training_data()

    model = Pipeline(
        steps=[
            ("scaler", StandardScaler()),
            (
                "isolation_forest",
                IsolationForest(
                    contamination=0.10,
                    n_estimators=200,
                    random_state=RANDOM_SEED,
                ),
            ),
        ]
    )
    model.fit(training_data)
    return model


if __name__ == "__main__":
    trained_model = train_model()
    joblib.dump(trained_model, MODEL_PATH)
    print(f"Saved model to {MODEL_PATH}")

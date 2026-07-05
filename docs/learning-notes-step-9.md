# Learning Notes - Step 9: ML Model Pipeline

## What We Built

Step 9 replaces the hand-written mock scoring logic with a saved machine
learning model.

The FastAPI service now loads:

```text
ml/model.pkl
```

and uses it inside:

```http
POST /predict
```

## Why Isolation Forest?

Isolation Forest is an unsupervised anomaly detection algorithm.

Unsupervised means we do not need labeled examples like:

```text
fraud
not fraud
```

Instead, the model learns the general shape of normal data. Points that look far
away from normal behavior are treated as anomalies.

That fits this project because suspicious transactions are rare and hard to
label perfectly.

## Features Used by the Model

The model expects this feature order:

```text
amount
hour_of_day
category_encoded
amount_vs_avg
```

This order matters. The training script and prediction code must use the same
order.

## Training Script

The training script is:

```text
finguard-ai-engine/ml/train_model.py
```

It creates synthetic training data for now:

```text
mostly normal transactions
some unusual high-ratio transactions
some late-night transactions
```

Then it trains:

```text
StandardScaler -> IsolationForest
```

`StandardScaler` normalizes feature ranges so large values like amount do not
overpower smaller values like hour of day.

## Saved Model

The trained model is saved with `joblib`:

```text
model.pkl
```

FastAPI loads that file when prediction is needed.

## Prediction Result

Isolation Forest returns:

```text
1 for normal
-1 for anomaly
```

The service converts the model score into:

```json
{
  "risk_score": 0.82,
  "is_anomaly": true
}
```

## What Still Needs Improvement Later

This is a real ML pipeline, but the data is synthetic.

A stronger future version would train on:

```text
real user transaction data
public fraud/anomaly datasets
better category encodings
merchant behavior features
location/device/time-window features
```

Step 9 gives the project the correct shape: train, save, load, predict.

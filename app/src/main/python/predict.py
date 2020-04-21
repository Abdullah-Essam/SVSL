import numpy as np
from sklearn import linear_model
from sklearn.linear_model import LinearRegression


def get(totals):
    # x and y must be in specific shape
    x = np.random.uniform(-1, 1, 101)
    y = x + 1 + np.random.normal(0, 1, len(x))

    # Passing in x and y in column shape
    x = x.reshape(-1, 1)
    y = y.reshape(-1, 1)

    LM = linear_model.LinearRegression().fit(x, y)

    # Passing the totals into the array
    total_array = np.array(totals)

    # Prediction
    predicted_total = total_array.reshape(-1, 1)
    score = LM.score(x, y)

    predicted_values = LM.predict(predicted_total)

    predictions = {'intercept': LM.intercept_,
                   'coefficient': LM.coef_,
                   'predicted_value': predicted_values,
                   'accuracy': score}

    return str(predicted_values[0])

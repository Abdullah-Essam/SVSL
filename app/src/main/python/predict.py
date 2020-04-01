import numpy as np
from sklearn import linear_model
from sklearn.linear_model import LinearRegression


def get(month1_total, month2_total, month3_total):
    # x and y must be in specific shape
    x = np.random.uniform(-1, 1, 101)
    y = x + 1 + np.random.normal(0, 1, len(x))

    # Passing in x and y in column shape
    x = x.reshape(-1, 1)
    y = y.reshape(-1, 1)

    LM = linear_model.LinearRegression().fit(x, y)

    # Passing the totals into the array
    total_array = np.array([month1_total, month2_total, month3_total])

    # Prediction
    predicted_total = total_array.reshape(-1, 1)
    score = LM.score(x, y)

    predicted_values = LM.predict(predicted_total)

    predictions = {'intercept': LM.intercept_,
                   'coefficient': LM.coef_,
                   'predicted_value': predicted_values,
                   'accuracy': score}
    s = 'First Month = ' + str(month1_total) + "\nSecond Month = " + str(
        month2_total) + "\nThird Month = " + str(month3_total) + "\nPrediction = " + str(
        predicted_values[0])
    return str(predicted_values[0])

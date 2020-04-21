package com.example.svslsavemoneysavelife.activities.ui.predict;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.svslsavemoneysavelife.R;
import com.example.svslsavemoneysavelife.models.Month;
import com.example.svslsavemoneysavelife.utils.SharedData;
import com.google.android.gms.vision.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PredictFragment extends Fragment {

    private TextView title, comment;
    private Button predict;

    public PredictFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predict, container, false);
        title = view.findViewById(R.id.title);
        comment = view.findViewById(R.id.comment);
        predict = view.findViewById(R.id.predict);

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int noOfMonths = SharedData.currentUser.getMonths().size();
                if (noOfMonths >= 3) {
                    Double[] totals = new Double[SharedData.currentUser.getMonths().size()];
                    for(int i = 0; i < SharedData.currentUser.getMonths().size(); i++) {
                        totals[i] = SharedData.currentUser.getMonths().get(i).getTotalExpanse();
                    }
                    runPythonCode(totals);
                } else {
                    comment.setText(R.string.three_month_error);
                }
            }
        });
        return view;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void runPythonCode(Double[] totals) {
        if(!Python.isStarted()) {
            Python.start(new AndroidPlatform(Objects.requireNonNull(getActivity())));
        }

        Python py = Python.getInstance();
        PyObject pyf = py.getModule("predict");
        PyObject obj = pyf.callAttr("get", (Object) totals);
        String strValue = obj.toString().trim().replaceAll("[^\\d.]", "");
        double value = Double.parseDouble(strValue);
        comment.setText(getResources().getString(R.string.predict_one) + String.format(" %.2f", value) + getResources().getString(R.string.predict_two));
    }
}

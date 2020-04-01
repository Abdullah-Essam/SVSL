package com.example.svslsavemoneysavelife.activities.ui.predict;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.svslsavemoneysavelife.R;

import java.util.Objects;

public class PredictFragment extends Fragment {

    private TextView textView;

    public PredictFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predict, container, false);
        textView = view.findViewById(R.id.text);

        runPythonCode();

        return view;
    }

    private void runPythonCode() {
        if(!Python.isStarted()) {
            Python.start(new AndroidPlatform(Objects.requireNonNull(getActivity())));
        }

        Python py = Python.getInstance();

        PyObject pyf = py.getModule("predict");

        PyObject obj = pyf.callAttr("get");

        textView.setText(obj.toString());
    }
}

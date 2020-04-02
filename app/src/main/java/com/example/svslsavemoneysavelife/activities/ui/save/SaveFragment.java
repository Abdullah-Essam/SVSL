package com.example.svslsavemoneysavelife.activities.ui.save;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svslsavemoneysavelife.R;
import com.example.svslsavemoneysavelife.controller.UserController;
import com.example.svslsavemoneysavelife.models.Month;
import com.example.svslsavemoneysavelife.utils.SharedData;

public class SaveFragment extends Fragment {
    private TextView title, comment;
    private EditText goalAmount;
    private Button saveButton;
    private UserController userController = new UserController();

    private double avg_points = 0.0;
    private double duration = 0;

    public SaveFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);
        title = view.findViewById(R.id.title);
        comment = view.findViewById(R.id.comment);
        goalAmount = view.findViewById(R.id.goal_amount);
        saveButton = view.findViewById(R.id.save);

        init();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (SharedData.currentUser.getMonths().size() > 2) {
                        double goalCost;
                        try {
                            goalCost = Double.parseDouble(goalAmount.getText().toString());

                            if (avg_points >= 0.1 && avg_points <= 0.2) {
                                duration = 2;
                            } else if (avg_points > 0.2 && avg_points <= 0.3) {
                                duration = 1.5;
                            } else if (avg_points > 0.3 && avg_points <= 0.4) {
                                duration = 1;
                            } else if (avg_points > 0.4 && avg_points <= 0.6) {
                                duration = 0.5;
                            } else if (avg_points > 0.6) {
                                duration = 0.25;
                            }

                            double monthly_saving_amount = goalCost / duration * 12;
                            comment.setText(String.format("To achieve this goal, you must save %.2f SR per month for %.0f months", monthly_saving_amount, (duration * 12)));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Enter valid amount", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        comment.setText(R.string.three_month_error);
                    }
                }
            }
        });
        return view;
    }

    private void init() {
        if (SharedData.currentUser.getMonths().size() > 2) {
            double totalAvgPoint = 0.0;
            for (Month month : SharedData.currentUser.getMonths()) {
                totalAvgPoint += month.getAvgPoint();
            }
            avg_points = totalAvgPoint / SharedData.currentUser.getMonths().size();
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(goalAmount.getText().toString())) {
            goalAmount.setError("required field!");
            return false;
        }
        return true;
    }
}

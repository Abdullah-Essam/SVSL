package com.example.svslsavemoneysavelife.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svslsavemoneysavelife.R;
import com.example.svslsavemoneysavelife.callback.UserCallback;
import com.example.svslsavemoneysavelife.controller.MonthController;
import com.example.svslsavemoneysavelife.controller.UserController;
import com.example.svslsavemoneysavelife.models.Month;
import com.example.svslsavemoneysavelife.models.User;
import com.example.svslsavemoneysavelife.utils.SharedData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SetLimitActivity extends AppCompatActivity {

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat df = new SimpleDateFormat("MMMM");
    private TextView title, subTitle;
    private EditText limitAmount;
    private Button save;
    private UserController userController = new UserController();
    private ArrayList<Month> userMonths = new ArrayList<>();
    private double amount = 0.0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_limit);

        title = findViewById(R.id.title);
        subTitle = findViewById(R.id.sub_title);
        limitAmount = findViewById(R.id.limit);
        save = findViewById(R.id.save);

        userMonths = SharedData.currentUser.getMonths();
        String monthName = df.format(userMonths.get(SharedData.currentMonthIndex).getMonthStart());
        title.setText(getString(R.string.dear) + SharedData.currentUser.getName() + getString(R.string.limit_title));
        limitAmount.setHint("Enter " + monthName + " limit");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    try {
                        amount = Double.parseDouble(limitAmount.getText().toString());
                    } catch (Exception e) {
                        Toast.makeText(SetLimitActivity.this, "Enter valid amount!", Toast.LENGTH_LONG).show();
                    }
                    if (amount > 0) {
                        final ProgressDialog progressDialog = new ProgressDialog(SetLimitActivity.this);
                        progressDialog.setTitle(getString(R.string.saving));
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        SharedData.currentUser.getMonths().get(SharedData.currentMonthIndex).setMonthLimit(amount);
                        userController.save(SharedData.currentUser, new UserCallback() {
                            @Override
                            public void onSuccess(ArrayList<User> users) {
                                SharedData.currentUser = users.get(0);
                                progressDialog.dismiss();
                                Intent intent = new Intent(SetLimitActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFail(String error) {
                                progressDialog.dismiss();
                                Toast.makeText(SetLimitActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(SetLimitActivity.this, R.string.valid_amount, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private boolean validate() {
        if (TextUtils.isEmpty(limitAmount.getText().toString())) {
            limitAmount.setError(getString(R.string.required));
            return false;
        }
        return true;
    }
}

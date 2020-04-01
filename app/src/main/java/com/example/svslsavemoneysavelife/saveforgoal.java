package com.example.svslsavemoneysavelife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.time.Month;
import java.util.Calendar;

public class saveforgoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveforgoal);
        double avg_points = 0.3 ;
        double duration = 0 ;
        TextView gaol = (TextView) findViewById(R.id.textgoal);
        EditText price = (EditText) findViewById(R.id.etcost);
        int p = Integer.parseInt(price.getText().toString());

        if (avg_points >= 0.1 && avg_points <= 0.2) {
            duration = 2;
        } else if (avg_points > 0.2 && avg_points <= 0.3 ) {
            duration = 1.5;
        } else if (avg_points > 0.3 && avg_points <= 0.4) {
            duration = 1;
        }else if (avg_points > 0.4 && avg_points <= 0.6) {
            duration = 0.5;
        }else if (avg_points > 0.6) {
            duration = 0.25;
        }
        double monthly_saving_amount = p/duration*12;
        gaol.setText( "To achieve this goal, you must save " + monthly_saving_amount + " per month for " + duration + " months");
    }
}

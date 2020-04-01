package com.example.svslsavemoneysavelife.callback;


import com.example.svslsavemoneysavelife.models.Month;

import java.util.ArrayList;

public interface MonthCallback {
    void onSuccess(ArrayList<Month> months);
    void onFail(String error);
}

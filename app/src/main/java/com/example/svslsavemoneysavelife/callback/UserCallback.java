package com.example.svslsavemoneysavelife.callback;

import com.example.svslsavemoneysavelife.models.User;

import java.util.ArrayList;

public interface UserCallback {
    void onSuccess(ArrayList<User> users);
    void onFail(String error);
}

package com.example.svslsavemoneysavelife.utils;

import com.example.svslsavemoneysavelife.models.User;

public class SharedData {
    public static User currentUser;
    public static int currentMonthIndex;

    public static final String PREF_KEY = "login";
    public static final String IS_USER_SAVED = "SAVED_USER";
    public static final String PHONE = "PHONE";
    public static final String PASS = "PASS";
}

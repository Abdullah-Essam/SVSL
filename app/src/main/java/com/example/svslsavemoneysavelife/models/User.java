package com.example.svslsavemoneysavelife.models;


import android.text.TextUtils;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class User {
    private String key;
    private String name;
    private String phone;
    private String pass;
    private ArrayList<Month> months;

    public User() {
    }

    public User(String key, String name, String phone, String pass, ArrayList<Month> months) {
        this.key = key;
        this.name = name;
        this.phone = phone;
        this.pass = pass;
        this.months = months;
    }

    public boolean validate() {
        return  (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pass));
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public ArrayList<Month> getMonths() {
        return months;
    }

    public void setMonths(ArrayList<Month> months) {
        this.months = months;
    }
}

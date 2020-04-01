package com.example.svslsavemoneysavelife.models;

import java.util.Date;

public class Invoice {
    private String key;
    private double amount;
    private Date date;

    public Invoice() {
    }

    public Invoice(String key, double amount, Date date) {
        this.key = key;
        this.amount = amount;
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

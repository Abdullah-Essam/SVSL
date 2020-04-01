package com.example.svslsavemoneysavelife.models;

import java.util.ArrayList;
import java.util.Date;

public class Month {
    private String key;
    private Double totalExpanse;
    private Double monthLimit;
    private Date monthStart;
    private Date monthEnd;
    private ArrayList<Invoice> invoices = new ArrayList<>();

    public Month() {
    }

    public Month(String key, Double totalExpanse, Double monthLimit, Date monthStart, Date monthEnd, ArrayList<Invoice> invoices) {
        this.key = key;
        this.totalExpanse = totalExpanse;
        this.monthLimit = monthLimit;
        this.monthStart = monthStart;
        this.monthEnd = monthEnd;
        this.invoices = invoices;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getTotalExpanse() {
        return totalExpanse;
    }

    public void setTotalExpanse(Double totalExpanse) {
        this.totalExpanse = totalExpanse;
    }

    public Double getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(Double monthLimit) {
        this.monthLimit = monthLimit;
    }

    public Date getMonthStart() {
        return monthStart;
    }

    public void setMonthStart(Date monthStart) {
        this.monthStart = monthStart;
    }

    public Date getMonthEnd() {
        return monthEnd;
    }

    public void setMonthEnd(Date monthEnd) {
        this.monthEnd = monthEnd;
    }

    public ArrayList<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(ArrayList<Invoice> invoices) {
        this.invoices = invoices;
    }
}

package com.branch.lending.pojo;

import java.util.Date;

public class Repayment {

    private Date date;
    private double amount;

    //Generating Setters and Getters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}

package com.branch.lending.pojo;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class LoanObj {


    private long application_id;
    private long customer_id;
    private double principal;
    private double fee;
    private Date disbursement_date;
    private List<RepaymentObj> repayments;
    private double totaledAmount;
    private boolean isLoanIncomplete;


    //Generating Setters and Getters
    public long getApplication_id() {
        return application_id;
    }

    public void setApplication_id(long application_id) {
        this.application_id = application_id;
    }

    public long getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(long customer_id) {
        this.customer_id = customer_id;
    }

    public double getPrincipal() {
        return principal;
    }

    public void setPrincipal(double principal) {
        this.principal = principal;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public Date getDisbursement_date() {
        return disbursement_date;
    }

    public void setDisbursement_date(Date disbursement_date) {
        this.disbursement_date = disbursement_date;
    }

    public List<RepaymentObj> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<RepaymentObj> repaymentObjs) {
        this.repayments = repaymentObjs;
    }

    public double getAmountRepaid() {
        List<RepaymentObj> repaymentObjs = getRepayments();
        double amount = 0 ;
        for (RepaymentObj repaymentObj : repaymentObjs) {
            amount += repaymentObj.getAmount() ;
        }
        return amount;
    }

    public Date getFinalRepaymentDate() {
        List<RepaymentObj> repaymentObjs = getRepayments();
        TreeMap<Date, Double> loanDateMap = new TreeMap<>(Comparator.naturalOrder());
        for (RepaymentObj repaymentObj : repaymentObjs) {
            loanDateMap.put(repaymentObj.getDate(), repaymentObj.getAmount());
        }
        return loanDateMap.lastKey();
    }

}

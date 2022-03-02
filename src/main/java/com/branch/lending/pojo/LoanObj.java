package com.branch.lending.pojo;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class LoanObj {


    private long applicationId;
    private long customerId;
    private double principal;
    private double fee;
    private Date disbursementDate;
    private List<Repayment> repayments;
    private double totaledAmount;
    private boolean isLoanIncomplete;


    //Generating Setters and Getters
    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
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

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public List<Repayment> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<Repayment> repayments) {
        this.repayments = repayments;
    }

    public double getTotaledAmount() {
        double netAmount = 0.0;
        for (Repayment repayment: repayments) {
            netAmount += repayment.getAmount();
        }
        return netAmount - principal;
    }

    public double getAmountRepaid() {
        List<Repayment> repayments = getRepayments();
        double amount = 0 ;
        for (Repayment repayment : repayments) {
            amount += repayment.getAmount() ;
        }
        return amount;
    }

    public Date getFinalRepaymentDate() {
        List<Repayment> repayments = getRepayments();
        TreeMap<Date, Double> loanDateMap = new TreeMap<>(Comparator.naturalOrder());
        for (Repayment repayment : repayments) {
            loanDateMap.put(repayment.getDate(), repayment.getAmount());
        }
        return loanDateMap.lastKey();
    }

}

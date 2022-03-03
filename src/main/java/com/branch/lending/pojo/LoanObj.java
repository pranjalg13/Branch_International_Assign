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
    private List<RepaymentObj> repaymentObjs;
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

    public List<RepaymentObj> getRepayments() {
        return repaymentObjs;
    }

    public void setRepayments(List<RepaymentObj> repaymentObjs) {
        this.repaymentObjs = repaymentObjs;
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

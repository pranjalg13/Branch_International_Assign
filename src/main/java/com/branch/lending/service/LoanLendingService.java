package com.branch.lending.service;

import com.branch.lending.pojo.LoanObj;
import com.branch.lending.pojo.RepaymentObj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;



public class LoanLendingService {

    private Set<Long> activeLoanCustomerId = new HashSet<>();
    private Set<LoanObj> activeLoanObjs = new HashSet<>();
    private Set<LoanObj> tempActiveLoanObjs = new HashSet<>();
    private double cashAtHand;
    private TreeSet<Long> loanApplicationIds = new TreeSet<>();
    private long maxNumberOfActiveLoans;
    private String inputFile;
    private String outputFile;

    //Constructor
    public LoanLendingService(String inputFile, String outputFile, double initialCapital, long maxNumberOfActiveLoans) {
        this.cashAtHand = initialCapital;
        this.maxNumberOfActiveLoans = maxNumberOfActiveLoans;
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        loansProcessing();
    }

    public void processFilteredLoans(Set<LoanObj> filteredLoanObjs) {

        Map<Date, Set<LoanObj>> orderedLoans = loanOrderByDate(filteredLoanObjs);

        for (Map.Entry<Date, Set<LoanObj>> orderedLoan : orderedLoans.entrySet()) {
            processDailyLoans(orderedLoan.getKey(), orderedLoan.getValue());
        }

        removeRepaidLoans(lastDayOfYear());
    }

    //picking closest repayment date
    public void processDailyLoans(Date date, Set<LoanObj> loanObjs) {
        removeRepaidLoans(date);

        if (activeLoanObjs.size() < maxNumberOfActiveLoans) {

            TreeSet<LoanObj> loanObjTreeSet = loansorderByRepaymentDateFeeAndPrincipal(loanObjs);
            for (LoanObj loanObj : loanObjTreeSet) {
                if (!activeLoanCustomerId.contains(loanObj.getCustomer_id()) && isHandCashSufficient(loanObj.getPrincipal())) {
                    activeLoanObjs.add(loanObj);
                    activeLoanCustomerId.add(loanObj.getCustomer_id());
                    cashAtHand -= loanObj.getPrincipal();
                    loanApplicationIds.add(loanObj.getApplication_id());
                }
                //Checking for concurrent loans < k
                if (activeLoanObjs.size() > maxNumberOfActiveLoans) {
                    break;
                }
            }
        }

    }

    public void loansProcessing() {

        WritingToFileService writingToFileService = new WritingToFileService();
        LoanObj[] loanObjs = writingToFileService.getLoansFromInputPath(inputFile);
        Set<LoanObj> filteredLoanObjs = filterLoans(loanObjs);
        processFilteredLoans(filteredLoanObjs);
        writingToFileService.writeToOutputFile(loanApplicationIds, outputFile);
        //Printing LoanIds
        System.out.println(loanApplicationIds.toString());

    }


    public Map<Date, Set<LoanObj>> loanOrderByDate(Set<LoanObj> filteredLoanObjs) {
        Map<Date, Set<LoanObj>> loanDateMap = new TreeMap<>(Comparator.naturalOrder());

        filteredLoanObjs.stream().forEach(filteredLoanObj -> {
            if (!loanDateMap.containsKey(filteredLoanObj.getDisbursement_date())) {
                loanDateMap.put(filteredLoanObj.getDisbursement_date(), new HashSet<>());
            }
            loanDateMap.get(filteredLoanObj.getDisbursement_date()).add(filteredLoanObj);

        });
        return loanDateMap;
    }

    public TreeSet<LoanObj> loansorderByRepaymentDateFeeAndPrincipal(Set<LoanObj> loanObjs) {
        TreeSet<LoanObj> loanObjSet = new TreeSet<LoanObj>((o1, o2) -> {
            if (o1.getFinalRepaymentDate().equals(o2.getFinalRepaymentDate())) {
                if (o1.getFee() == o2.getFee()) {
                    return (int) (o1.getPrincipal() - o2.getPrincipal());
                }
                return (int) (o2.getFee() - o1.getFee());
            }
            return o1.getFinalRepaymentDate().compareTo(o2.getFinalRepaymentDate());
        });

        loanObjSet.addAll(loanObjs);
        return loanObjSet;
    }

    public void removeRepaidLoans(Date date) {
        for (LoanObj loanObj : activeLoanObjs) {
            if (loanObj.getFinalRepaymentDate().compareTo(date) < 0) {
               tempActiveLoanObjs.add(loanObj);
               activeLoanCustomerId.remove(loanObj.getCustomer_id());
               cashAtHand += loanObj.getAmountRepaid();
            }
        }
        activeLoanObjs.removeAll(tempActiveLoanObjs);
        tempActiveLoanObjs = new HashSet<>();
    }

    public boolean isHandCashSufficient(double principal) {
        return cashAtHand > principal;
    }


    public long getDurationBetweenDays(Date date1, Date date2) {
        return Duration.between(date1.toInstant(), date2.toInstant())
                .abs().toDays();
    }

    public Date lastDayOfYear() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-31");
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    public Set<LoanObj> filterLoans(LoanObj[] loanObjs) {
        Set<LoanObj> loanObjSet = new HashSet<>();
        for (int index = 0; index < loanObjs.length; index++) {
            LoanObj loanObj = loanObjs[index];
            double principal = loanObj.getPrincipal();
            double amount = 0;
            for (RepaymentObj repaymentObj : loanObj.getRepayments()) {
                if (repaymentObj.getDate().before(lastDayOfYear())) {
                    amount += repaymentObj.getAmount();
                }
            }
            double netAmount = amount - principal;
            if (netAmount < 0 || getDurationBetweenDays(loanObj.getDisbursement_date(), loanObj.getFinalRepaymentDate()) > 90) {
                continue;
            }
            loanObjSet.add(loanObj);
        }
        return loanObjSet;
    }


}

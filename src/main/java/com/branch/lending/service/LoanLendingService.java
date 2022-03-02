package com.branch.lending.service;

import com.branch.lending.pojo.LoanObj;
import com.branch.lending.pojo.Repayment;

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
    public LoanLendingService(String inputFile, String outputFile, double intialCapital, long maxNumberOfActiveLoans) {
        this.cashAtHand = intialCapital;
        this.maxNumberOfActiveLoans = maxNumberOfActiveLoans;
        this.inputFile = inputFile;
        this.outputFile = outputFile;

        loansProcessing();
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

    public void processFilteredLoans(Set<LoanObj> filteredLoanObjs) {

        Map<Date, Set<LoanObj>> orderedLoans = orderLoansByDate(filteredLoanObjs);

        for (Map.Entry<Date, Set<LoanObj>> orderedLoan : orderedLoans.entrySet()) {
            processDailyLoans(orderedLoan.getKey(), orderedLoan.getValue());
        }

        removeRepaidLoans(lastDayOfYear());
    }

    //picking closest repayment date
    public void processDailyLoans(Date date, Set<LoanObj> loanObjs) {
        removeRepaidLoans(date);

        if (activeLoanObjs.size() < maxNumberOfActiveLoans) {

            TreeSet<LoanObj> loanObjTreeSet = orderLoansByRepaymentDateFeeAndPrincipal(loanObjs);
            for (LoanObj loanObj : loanObjTreeSet) {
                if (!activeLoanCustomerId.contains(loanObj.getCustomerId()) && isCashAtHandSufficient(loanObj.getPrincipal())) {
                    activeLoanObjs.add(loanObj);
                    activeLoanCustomerId.add(loanObj.getCustomerId());
                    cashAtHand -= loanObj.getPrincipal();
                    loanApplicationIds.add(loanObj.getApplicationId());
                }
                //Checking for concurrent loans < k
                if (activeLoanObjs.size() > maxNumberOfActiveLoans) {
                    break;
                }
            }
        }

    }

    public Map<Date, Set<LoanObj>> orderLoansByDate(Set<LoanObj> filteredLoanObjs) {
        Map<Date, Set<LoanObj>> loanDateMap = new TreeMap<>(Comparator.naturalOrder());

        filteredLoanObjs.stream().forEach(filteredLoanObj -> {
            if (!loanDateMap.containsKey(filteredLoanObj.getDisbursementDate())) {
                loanDateMap.put(filteredLoanObj.getDisbursementDate(), new HashSet<>());
            }
            loanDateMap.get(filteredLoanObj.getDisbursementDate()).add(filteredLoanObj);

        });
        return loanDateMap;
    }

    public TreeSet<LoanObj> orderLoansByRepaymentDateFeeAndPrincipal(Set<LoanObj> loanObjs) {
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
               activeLoanCustomerId.remove(loanObj.getCustomerId());
               cashAtHand += loanObj.getAmountRepaid();
            }
        }
        activeLoanObjs.removeAll(tempActiveLoanObjs);
        tempActiveLoanObjs = new HashSet<>();
    }

    public boolean isCashAtHandSufficient(double principal) {
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
            for (Repayment repayment : loanObj.getRepayments()) {
                if (repayment.getDate().before(lastDayOfYear())) {
                    amount += repayment.getAmount();
                }
            }
            double netAmount = amount - principal;
            if (netAmount < 0 || getDurationBetweenDays(loanObj.getDisbursementDate(), loanObj.getFinalRepaymentDate()) > 90) {
                continue;
            }
            loanObjSet.add(loanObj);
        }
        return loanObjSet;
    }


}
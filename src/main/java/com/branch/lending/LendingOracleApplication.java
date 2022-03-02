package com.branch.lending;

import com.branch.lending.service.LoanLendingService;


public class LendingOracleApplication {

    public static void main(String[] args) {
        try {
            if (args.length < 4) {
                // Logging (Log4j not working :( )
                System.out.println("Less than 4 arguments are provided");
                return;
            }else if(args.length > 4){
                System.out.println("More than 4 arguments are provided");
                return;
            }

            String inputFilePath = args[0];
            String outputFilePath = args[1];
            double initialCapital = Double.valueOf(args[2]);
            long concurrentActiveLoans = Long.valueOf(args[3]);

            LoanLendingService loanLendingService = new LoanLendingService(inputFilePath, outputFilePath, initialCapital, concurrentActiveLoans);
        } catch (Exception e) {
            System.out.println("Exception " + e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

}

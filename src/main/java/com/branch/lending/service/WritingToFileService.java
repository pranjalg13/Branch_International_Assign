package com.branch.lending.service;

import com.branch.lending.pojo.LoanObj;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;

public class WritingToFileService {

    public void writeToOutputFile(TreeSet<Long> loanApplicationIds, String outputFilePath) {

        Path filePath = Paths.get(outputFilePath);
        try {
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);

            try (FileWriter writer = new FileWriter(outputFilePath, true)) {
                for (Long value : loanApplicationIds) {
                    writer.write(value + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error Output in file");
            e.printStackTrace();
        }
    }

    public LoanObj[] getLoansFromInputPath(String inputFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            LoanObj[] loanObjs = new Gson().fromJson(br, LoanObj[].class);
            return loanObjs;
        } catch (IOException e) {
            System.out.println("Input File not Found");
            e.printStackTrace();
        }
        return null;
    }

}

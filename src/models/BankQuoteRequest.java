/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.io.Console;
import static java.lang.Math.random;

/**
 *
 * @author user
 */
public class BankQuoteRequest {
   public int SSN;
 public int CreditScore;
 public int HistoryLength;
 public int LoanAmount;
 public int LoanTerm; 

    public BankQuoteRequest(int SSN, int CreditScore, int HistoryLength, int LoanAmount, int LoanTerm) {
        this.SSN = SSN;
        this.CreditScore = CreditScore;
        this.HistoryLength = HistoryLength;
        this.LoanAmount = LoanAmount;
        this.LoanTerm = LoanTerm;
    }

    public int getSSN() {
        return SSN;
    }

    public void setSSN(int SSN) {
        this.SSN = SSN;
    }

    public int getCreditScore() {
        return CreditScore;
    }

    public void setCreditScore(int CreditScore) {
        this.CreditScore = CreditScore;
    }

    public int getHistoryLength() {
        return HistoryLength;
    }

    public void setHistoryLength(int HistoryLength) {
        this.HistoryLength = HistoryLength;
    }

    public int getLoanAmount() {
        return LoanAmount;
    }

    public void setLoanAmount(int LoanAmount) {
        this.LoanAmount = LoanAmount;
    }

    public int getLoanTerm() {
        return LoanTerm;
    }

    public void setLoanTerm(int LoanTerm) {
        this.LoanTerm = LoanTerm;
    }
    
}

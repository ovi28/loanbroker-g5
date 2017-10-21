/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bank.credit.web.service;

/**
 *
 * @author user
 */
public class Loan {
    private String SSN;
    private int creditScore;
    private double loanAmount;
    private int loanDuration;
    private double interestRate;
    private String[] rules;

    

   
     public String getSSN() {
        return SSN;
    }

    public void setSSN(String SSN) {
        this.SSN = SSN;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public int getLoanDuration() {
        return loanDuration;
    }

    public void setLoanDuration(int loanDuration) { this.loanDuration = loanDuration; }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public String[] getRules() {
        return rules;
    }

    public void setRules(String[] rules) {
        this.rules = rules;
    }
    
}

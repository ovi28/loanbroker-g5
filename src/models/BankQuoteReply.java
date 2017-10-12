/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author user
 */
public class BankQuoteReply {
    public double InterestRate;
 public String QuoteID;
 public int ErrorCode; 

    public BankQuoteReply(double InterestRate, String QuoteID, int ErrorCode) {
        this.InterestRate = InterestRate;
        this.QuoteID = QuoteID;
        this.ErrorCode = ErrorCode;
    }

    public double getInterestRate() {
        return InterestRate;
    }

    public void setInterestRate(double InterestRate) {
        this.InterestRate = InterestRate;
    }

    public String getQuoteID() {
        return QuoteID;
    }

    public void setQuoteID(String QuoteID) {
        this.QuoteID = QuoteID;
    }

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int ErrorCode) {
        this.ErrorCode = ErrorCode;
    }
 
 
}

package models;

/**
 *
 * @author Group 6
 */
public class LoanRequest {
    
    private int creditScore;
    private int loanAmount;

    public LoanRequest(int creditScore, int loanAmount) {
        this.creditScore = creditScore;
        this.loanAmount = loanAmount;
    }
    
    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public int getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(int loanAmount) {
        this.loanAmount = loanAmount;
    }
}

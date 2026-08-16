package com.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String loanNumber;
    private Long userId;
    private String loanType; // PERSONAL, HOME, CAR, EDUCATION
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer durationMonths;
    private BigDecimal monthlyEmi;
    private String status; // PENDING, APPROVED, REJECTED, CLOSED
    private BigDecimal remainingAmount;
    private LocalDateTime appliedAt;

    public Loan() {}

    public Loan(Long id, String loanNumber, Long userId, String loanType, BigDecimal principalAmount, BigDecimal interestRate, Integer durationMonths, BigDecimal monthlyEmi, String status, BigDecimal remainingAmount) {
        this.id = id;
        this.loanNumber = loanNumber;
        this.userId = userId;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
        this.monthlyEmi = monthlyEmi;
        this.status = status;
        this.remainingAmount = remainingAmount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoanNumber() { return loanNumber; }
    public void setLoanNumber(String loanNumber) { this.loanNumber = loanNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }

    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public void setPrincipalAmount(BigDecimal principalAmount) { this.principalAmount = principalAmount; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public Integer getDurationMonths() { return durationMonths; }
    public void setDurationMonths(Integer durationMonths) { this.durationMonths = durationMonths; }

    public BigDecimal getMonthlyEmi() { return monthlyEmi; }
    public void setMonthlyEmi(BigDecimal monthlyEmi) { this.monthlyEmi = monthlyEmi; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", loanNumber='" + loanNumber + '\'' +
                ", userId=" + userId +
                ", loanType='" + loanType + '\'' +
                ", principalAmount=" + principalAmount +
                ", monthlyEmi=" + monthlyEmi +
                ", status='" + status + '\'' +
                '}';
    }
}

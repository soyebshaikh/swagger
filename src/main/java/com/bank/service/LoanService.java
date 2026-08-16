package com.bank.service;

import com.bank.model.Loan;
import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    Loan applyLoan(Long userId, String loanType, BigDecimal principalAmount, BigDecimal interestRate, Integer durationMonths);
    List<Loan> getLoansByUserId(Long userId);
    Loan getLoanByNumber(String loanNumber);
    Loan approveLoan(Long loanId);
    Loan evaluateLoanEligibility(Loan loan);
    BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualInterestRate, int durationMonths);
}

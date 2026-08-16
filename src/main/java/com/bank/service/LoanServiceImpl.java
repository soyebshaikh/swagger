package com.bank.service;

import com.bank.mapper.LoanMapper;
import com.bank.model.Loan;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class LoanServiceImpl implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    private final LoanMapper loanMapper;
    private ProducerTemplate producerTemplate;

    public LoanServiceImpl(LoanMapper loanMapper) {
        this.loanMapper = loanMapper;
    }

    public void setProducerTemplate(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Loan applyLoan(Long userId, String loanType, BigDecimal principalAmount, BigDecimal interestRate, Integer durationMonths) {
        if (principalAmount == null || principalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Loan principal amount must be positive");
        }
        if (durationMonths == null || durationMonths <= 0) {
            throw new IllegalArgumentException("Loan duration months must be positive");
        }

        BigDecimal emi = calculateEmi(principalAmount, interestRate, durationMonths);
        String loanNumber = "LN-" + loanType.substring(0, 3).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Loan loan = new Loan(
                null, loanNumber, userId, loanType, principalAmount,
                interestRate, durationMonths, emi, "PENDING", principalAmount
        );

        loanMapper.insert(loan);
        logger.info("Submitted loan application: {} for user ID: {}, EMI: {}", loanNumber, userId, emi);

        // Send to Camel Loan Review Route
        if (producerTemplate != null) {
            producerTemplate.sendBody("direct:reviewLoan", loan);
        }

        return loan;
    }

    @Override
    public List<Loan> getLoansByUserId(Long userId) {
        return loanMapper.findByUserId(userId);
    }

    @Override
    public Loan getLoanByNumber(String loanNumber) {
        Loan loan = loanMapper.findByLoanNumber(loanNumber);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found: " + loanNumber);
        }
        return loan;
    }

    @Override
    public Loan approveLoan(Long loanId) {
        Loan loan = loanMapper.findById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found with ID: " + loanId);
        }
        loanMapper.updateStatus(loanId, "APPROVED");
        loan.setStatus("APPROVED");
        logger.info("Loan ID {} ({}) approved.", loanId, loan.getLoanNumber());
        return loan;
    }

    @Override
    public Loan evaluateLoanEligibility(Loan loan) {
        logger.info("Camel evaluating eligibility for Loan Number: {}", loan.getLoanNumber());
        // Eligibility Rule: Loans <= 500,000 auto-approved; > 500,000 remain pending manual review
        if (loan.getPrincipalAmount().compareTo(new BigDecimal("500000.00")) <= 0) {
            loanMapper.updateStatus(loan.getId(), "APPROVED");
            loan.setStatus("APPROVED");
            logger.info("Loan {} auto-approved by Camel rule engine!", loan.getLoanNumber());
        } else {
            logger.info("Loan {} queued for manual underwriting review.", loan.getLoanNumber());
        }
        return loan;
    }

    @Override
    public BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualInterestRate, int durationMonths) {
        BigDecimal monthlyRate = annualInterestRate.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(durationMonths), 2, RoundingMode.HALF_UP);
        }

        double p = principal.doubleValue();
        double r = monthlyRate.doubleValue();
        double emi = (p * r * Math.pow(1 + r, durationMonths)) / (Math.pow(1 + r, durationMonths) - 1);

        return new BigDecimal(emi).setScale(2, RoundingMode.HALF_UP);
    }
}

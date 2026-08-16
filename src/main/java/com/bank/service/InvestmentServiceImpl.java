package com.bank.service;

import com.bank.mapper.InvestmentMapper;
import com.bank.model.Investment;
import com.bank.model.MutualFund;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class InvestmentServiceImpl implements InvestmentService {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentServiceImpl.class);

    private final InvestmentMapper investmentMapper;

    public InvestmentServiceImpl(InvestmentMapper investmentMapper) {
        this.investmentMapper = investmentMapper;
    }

    @Override
    public List<MutualFund> getAllMutualFunds() {
        return investmentMapper.findAllFunds();
    }

    @Override
    public MutualFund getMutualFundByCode(String fundCode) {
        MutualFund fund = investmentMapper.findFundByCode(fundCode);
        if (fund == null) {
            throw new IllegalArgumentException("Mutual Fund not found: " + fundCode);
        }
        return fund;
    }

    @Override
    public List<Investment> getUserInvestments(Long userId) {
        return investmentMapper.findInvestmentsByUserId(userId);
    }

    @Override
    public Investment buyMutualFund(Long userId, String fundCode, BigDecimal amount, String investmentType) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Investment amount must be greater than zero");
        }
        MutualFund fund = getMutualFundByCode(fundCode);

        // Calculate Units = Amount / NAV
        BigDecimal units = amount.divide(fund.getNav(), 4, RoundingMode.HALF_UP);
        BigDecimal currentValue = units.multiply(fund.getNav()).setScale(2, RoundingMode.HALF_UP);

        Investment investment = new Investment(
                null, userId, fund.getId(), units, amount, currentValue,
                investmentType != null ? investmentType : "LUMPSUM", "ACTIVE"
        );

        investmentMapper.insertInvestment(investment);
        logger.info("Purchased {} units of {} for User ID {}. Amount: {}", units, fundCode, userId, amount);
        return investmentMapper.findInvestmentById(investment.getId());
    }

    @Override
    public Investment redeemInvestment(Long investmentId) {
        Investment investment = investmentMapper.findInvestmentById(investmentId);
        if (investment == null) {
            throw new IllegalArgumentException("Investment not found with ID: " + investmentId);
        }
        if ("REDEEMED".equalsIgnoreCase(investment.getStatus())) {
            throw new IllegalStateException("Investment already redeemed");
        }

        BigDecimal currentNav = investment.getCurrentNav() != null ? investment.getCurrentNav() : BigDecimal.ONE;
        BigDecimal redemptionValue = investment.getUnits().multiply(currentNav).setScale(2, RoundingMode.HALF_UP);

        investment.setCurrentValue(redemptionValue);
        investment.setStatus("REDEEMED");
        investmentMapper.updateInvestment(investment);

        logger.info("Redeemed investment ID {}. Total Payout: {}", investmentId, redemptionValue);
        return investment;
    }

    @Override
    public void updateFundNav(String fundCode, BigDecimal newNav) {
        MutualFund fund = getMutualFundByCode(fundCode);
        investmentMapper.updateFundNav(fund.getId(), newNav);
        logger.info("Updated NAV for fund {} to {}", fundCode, newNav);
    }
}

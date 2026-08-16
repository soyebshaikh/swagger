package com.bank.service;

import com.bank.model.Investment;
import com.bank.model.MutualFund;
import java.math.BigDecimal;
import java.util.List;

public interface InvestmentService {
    List<MutualFund> getAllMutualFunds();
    MutualFund getMutualFundByCode(String fundCode);
    List<Investment> getUserInvestments(Long userId);
    Investment buyMutualFund(Long userId, String fundCode, BigDecimal amount, String investmentType);
    Investment redeemInvestment(Long investmentId);
    void updateFundNav(String fundCode, BigDecimal newNav);
}

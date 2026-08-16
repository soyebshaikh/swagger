package com.bank.mapper;

import com.bank.model.Investment;
import com.bank.model.MutualFund;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

public interface InvestmentMapper {
    // Mutual Funds
    MutualFund findFundById(@Param("id") Long id);
    MutualFund findFundByCode(@Param("fundCode") String fundCode);
    List<MutualFund> findAllFunds();
    int updateFundNav(@Param("id") Long id, @Param("nav") BigDecimal nav);

    // User Investments
    Investment findInvestmentById(@Param("id") Long id);
    List<Investment> findInvestmentsByUserId(@Param("userId") Long userId);
    int insertInvestment(Investment investment);
    int updateInvestment(Investment investment);
}

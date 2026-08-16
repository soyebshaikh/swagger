package com.bank.mapper;

import com.bank.model.Loan;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

public interface LoanMapper {
    Loan findById(@Param("id") Long id);
    Loan findByLoanNumber(@Param("loanNumber") String loanNumber);
    List<Loan> findByUserId(@Param("userId") Long userId);
    List<Loan> findByStatus(@Param("status") String status);
    int insert(Loan loan);
    int updateStatus(@Param("loanId") Long loanId, @Param("status") String status);
    int updateRemainingAmount(@Param("loanId") Long loanId, @Param("remainingAmount") BigDecimal remainingAmount);
}

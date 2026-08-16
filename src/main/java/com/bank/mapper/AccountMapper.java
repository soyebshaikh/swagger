package com.bank.mapper;

import com.bank.model.Account;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

public interface AccountMapper {
    Account findById(@Param("id") Long id);
    Account findByAccountNumber(@Param("accountNumber") String accountNumber);
    List<Account> findByUserId(@Param("userId") Long userId);
    int insert(Account account);
    int updateBalance(@Param("accountId") Long accountId, @Param("balance") BigDecimal balance);
    int updateStatus(@Param("accountId") Long accountId, @Param("status") String status);
}

package com.bank.mapper;

import com.bank.model.Transaction;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TransactionMapper {
    Transaction findById(@Param("id") Long id);
    Transaction findByReference(@Param("reference") String reference);
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
    int insert(Transaction transaction);
}

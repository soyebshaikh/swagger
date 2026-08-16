package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account getAccountByNumber(String accountNumber);
    List<Account> getAccountsByUserId(Long userId);
    BigDecimal getBalance(String accountNumber);
    Transaction deposit(String accountNumber, BigDecimal amount, String description);
    Transaction withdraw(String accountNumber, BigDecimal amount, String description);
    List<Transaction> getTransactions(String accountNumber);
    Account createAccount(Account account);
}

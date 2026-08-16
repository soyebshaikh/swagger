package com.bank.service;

import com.bank.mapper.AccountMapper;
import com.bank.mapper.TransactionMapper;
import com.bank.model.Account;
import com.bank.model.Transaction;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    private ProducerTemplate producerTemplate;

    public AccountServiceImpl(AccountMapper accountMapper, TransactionMapper transactionMapper) {
        this.accountMapper = accountMapper;
        this.transactionMapper = transactionMapper;
    }

    public void setProducerTemplate(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Account getAccountByNumber(String accountNumber) {
        Account account = accountMapper.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account;
    }

    @Override
    public List<Account> getAccountsByUserId(Long userId) {
        return accountMapper.findByUserId(userId);
    }

    @Override
    public BigDecimal getBalance(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return account.getBalance();
    }

    @Override
    public Transaction deposit(String accountNumber, BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Account account = getAccountByNumber(accountNumber);
        BigDecimal newBalance = account.getBalance().add(amount);
        accountMapper.updateBalance(account.getId(), newBalance);

        String ref = "TXN-DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Transaction transaction = new Transaction(
                ref, account.getId(), "DEPOSIT", amount, newBalance,
                description != null ? description : "Cash/Online Deposit"
        );
        transactionMapper.insert(transaction);

        logger.info("Deposited {} to account {}. New Balance: {}", amount, accountNumber, newBalance);

        // Audit via Camel Route if available
        if (producerTemplate != null) {
            producerTemplate.asyncSendBody("direct:auditTransaction", transaction);
        }

        return transaction;
    }

    @Override
    public Transaction withdraw(String accountNumber, BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        Account account = getAccountByNumber(accountNumber);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds in account: " + accountNumber);
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountMapper.updateBalance(account.getId(), newBalance);

        String ref = "TXN-WTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Transaction transaction = new Transaction(
                ref, account.getId(), "WITHDRAWAL", amount, newBalance,
                description != null ? description : "Atm/Online Withdrawal"
        );
        transactionMapper.insert(transaction);

        logger.info("Withdrew {} from account {}. New Balance: {}", amount, accountNumber, newBalance);

        // Audit via Camel Route if available
        if (producerTemplate != null) {
            producerTemplate.asyncSendBody("direct:auditTransaction", transaction);
        }

        return transaction;
    }

    @Override
    public List<Transaction> getTransactions(String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        return transactionMapper.findByAccountId(account.getId());
    }

    @Override
    public Account createAccount(Account account) {
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        if (account.getStatus() == null) {
            account.setStatus("ACTIVE");
        }
        accountMapper.insert(account);
        return account;
    }
}

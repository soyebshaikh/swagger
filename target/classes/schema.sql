-- Database Schema for Banking Application

DROP TABLE IF EXISTS investments;
DROP TABLE IF EXISTS mutual_funds;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL, -- CHECKING, SAVINGS, FIXED_DEPOSIT
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_reference VARCHAR(50) NOT NULL UNIQUE,
    account_id BIGINT NOT NULL,
    transaction_type VARCHAR(20) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER, INTEREST_CREDIT
    amount DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    description VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE loans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    loan_number VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    loan_type VARCHAR(30) NOT NULL, -- PERSONAL, HOME, CAR, EDUCATION
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,
    duration_months INT NOT NULL,
    monthly_emi DECIMAL(15, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, CLOSED
    remaining_amount DECIMAL(15, 2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE mutual_funds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fund_code VARCHAR(30) NOT NULL UNIQUE,
    fund_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL, -- EQUITY, DEBT, HYBRID, INDEX
    nav DECIMAL(10, 4) NOT NULL,
    risk_level VARCHAR(20) NOT NULL -- LOW, MODERATE, HIGH
);

CREATE TABLE investments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    fund_id BIGINT NOT NULL,
    units DECIMAL(15, 4) NOT NULL,
    invested_amount DECIMAL(15, 2) NOT NULL,
    current_value DECIMAL(15, 2) NOT NULL,
    investment_type VARCHAR(20) NOT NULL, -- SIP, LUMPSUM
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, REDEEMED
    invested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (fund_id) REFERENCES mutual_funds(id)
);

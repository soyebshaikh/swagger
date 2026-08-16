-- Initial Seed Data for Banking Application

-- Users (IDs auto-assigned: 1, 2, 3)
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
('john_doe', 'password123', 'John Doe', 'john.doe@example.com', '+1-555-0199', 'CUSTOMER'),
('jane_smith', 'securepass', 'Jane Smith', 'jane.smith@example.com', '+1-555-0188', 'CUSTOMER'),
('admin_bank', 'admin123', 'Bank Admin', 'admin@apexbank.com', '+1-555-0100', 'ADMIN');

-- Accounts (IDs auto-assigned: 1, 2, 3)
INSERT INTO accounts (account_number, user_id, account_type, balance, status) VALUES
('ACC-CHK-1001', 1, 'CHECKING', 15500.00, 'ACTIVE'),
('ACC-SAV-1002', 1, 'SAVINGS', 45000.50, 'ACTIVE'),
('ACC-CHK-2001', 2, 'CHECKING', 8200.75, 'ACTIVE');

-- Initial Transactions (IDs auto-assigned: 1, 2, 3)
INSERT INTO transactions (transaction_reference, account_id, transaction_type, amount, balance_after, description) VALUES
('TXN-10001', 1, 'DEPOSIT', 20000.00, 20000.00, 'Initial Salary Credit'),
('TXN-10002', 1, 'WITHDRAWAL', 4500.00, 15500.00, 'ATM Withdrawal'),
('TXN-10003', 2, 'DEPOSIT', 45000.50, 45000.50, 'Savings Transfer');

-- Initial Loans (IDs auto-assigned: 1, 2)
INSERT INTO loans (loan_number, user_id, loan_type, principal_amount, interest_rate, duration_months, monthly_emi, status, remaining_amount) VALUES
('LN-HOM-5001', 1, 'HOME', 250000.00, 7.50, 240, 2014.28, 'APPROVED', 245000.00),
('LN-AUT-5002', 2, 'CAR', 35000.00, 8.20, 60, 713.12, 'PENDING', 35000.00);

-- Mutual Funds Catalog (IDs auto-assigned: 1, 2, 3, 4)
INSERT INTO mutual_funds (fund_code, fund_name, category, nav, risk_level) VALUES
('MF-EQ-001', 'Apex Bluechip Equity Growth Fund', 'EQUITY', 142.50, 'HIGH'),
('MF-DB-002', 'Apex Corporate Bond Debt Fund', 'DEBT', 45.80, 'LOW'),
('MF-HB-003', 'Apex Balanced Hybrid Growth Fund', 'HYBRID', 88.25, 'MODERATE'),
('MF-IX-004', 'Apex Nifty 50 Index Fund', 'INDEX', 210.10, 'MODERATE');

-- Initial Investments (IDs auto-assigned: 1, 2)
INSERT INTO investments (user_id, fund_id, units, invested_amount, current_value, investment_type, status) VALUES
(1, 1, 100.0000, 14000.00, 14250.00, 'SIP', 'ACTIVE'),
(1, 3, 200.0000, 17000.00, 17650.00, 'LUMPSUM', 'ACTIVE');

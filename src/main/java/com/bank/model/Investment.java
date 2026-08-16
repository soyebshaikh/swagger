package com.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Investment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long fundId;
    private BigDecimal units;
    private BigDecimal investedAmount;
    private BigDecimal currentValue;
    private String investmentType; // SIP, LUMPSUM
    private String status; // ACTIVE, REDEEMED
    private LocalDateTime investedAt;

    // Joined fields
    private String fundName;
    private String fundCode;
    private BigDecimal currentNav;

    public Investment() {}

    public Investment(Long id, Long userId, Long fundId, BigDecimal units, BigDecimal investedAmount, BigDecimal currentValue, String investmentType, String status) {
        this.id = id;
        this.userId = userId;
        this.fundId = fundId;
        this.units = units;
        this.investedAmount = investedAmount;
        this.currentValue = currentValue;
        this.investmentType = investmentType;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFundId() { return fundId; }
    public void setFundId(Long fundId) { this.fundId = fundId; }

    public BigDecimal getUnits() { return units; }
    public void setUnits(BigDecimal units) { this.units = units; }

    public BigDecimal getInvestedAmount() { return investedAmount; }
    public void setInvestedAmount(BigDecimal investedAmount) { this.investedAmount = investedAmount; }

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public String getInvestmentType() { return investmentType; }
    public void setInvestmentType(String investmentType) { this.investmentType = investmentType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getInvestedAt() { return investedAt; }
    public void setInvestedAt(LocalDateTime investedAt) { this.investedAt = investedAt; }

    public String getFundName() { return fundName; }
    public void setFundName(String fundName) { this.fundName = fundName; }

    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }

    public BigDecimal getCurrentNav() { return currentNav; }
    public void setCurrentNav(BigDecimal currentNav) { this.currentNav = currentNav; }

    @Override
    public String toString() {
        return "Investment{" +
                "id=" + id +
                ", userId=" + userId +
                ", fundId=" + fundId +
                ", units=" + units +
                ", investedAmount=" + investedAmount +
                ", currentValue=" + currentValue +
                ", status='" + status + '\'' +
                '}';
    }
}

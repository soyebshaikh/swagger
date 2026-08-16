package com.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class MutualFund implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String fundCode;
    private String fundName;
    private String category; // EQUITY, DEBT, HYBRID, INDEX
    private BigDecimal nav;
    private String riskLevel; // LOW, MODERATE, HIGH

    public MutualFund() {}

    public MutualFund(Long id, String fundCode, String fundName, String category, BigDecimal nav, String riskLevel) {
        this.id = id;
        this.fundCode = fundCode;
        this.fundName = fundName;
        this.category = category;
        this.nav = nav;
        this.riskLevel = riskLevel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }

    public String getFundName() { return fundName; }
    public void setFundName(String fundName) { this.fundName = fundName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getNav() { return nav; }
    public void setNav(BigDecimal nav) { this.nav = nav; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    @Override
    public String toString() {
        return "MutualFund{" +
                "id=" + id +
                ", fundCode='" + fundCode + '\'' +
                ", fundName='" + fundName + '\'' +
                ", category='" + category + '\'' +
                ", nav=" + nav +
                '}';
    }
}

package com.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private String status;
    private BigDecimal refundAmount;
    private LocalDateTime processedAt;

    public PaymentResponse() {}

    public PaymentResponse(Long paymentId, String status, BigDecimal refundAmount) {
        this.paymentId = paymentId;
        this.status = status;
        this.refundAmount = refundAmount;
        this.processedAt = LocalDateTime.now();
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}

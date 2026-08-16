package com.bank.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CancelPaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long paymentId;
    private String reason;
    private LocalDateTime requestedAt;

    public CancelPaymentRequest() {}

    public CancelPaymentRequest(Long paymentId, String reason) {
        this.paymentId = paymentId;
        this.reason = reason;
        this.requestedAt = LocalDateTime.now();
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}

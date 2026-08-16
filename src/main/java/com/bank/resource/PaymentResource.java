package com.bank.resource;

import com.bank.model.CancelPaymentRequest;
import com.bank.model.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/payments")
@Produces("application/json")
@Consumes("application/json")
public class PaymentResource {

    private static final Logger logger = LoggerFactory.getLogger(PaymentResource.class);

    @POST
    @Path("/cancel")
    public Response cancelPayment(CancelPaymentRequest request) {
        logger.info("Received payment cancellation request for payment ID: {}", request != null ? request.getPaymentId() : null);

        PaymentResponse response = new PaymentResponse(
                request != null ? request.getPaymentId() : 9999L,
                "CANCELLED",
                new BigDecimal("250.00")
        );

        return Response.ok(response).build();
    }
}

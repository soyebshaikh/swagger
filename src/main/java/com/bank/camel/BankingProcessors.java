package com.bank.camel;

import com.bank.model.Transaction;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BankingProcessors {

    private static final Logger logger = LoggerFactory.getLogger(BankingProcessors.class);

    public static class TransactionEventTransformer implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            Transaction txn = exchange.getIn().getBody(Transaction.class);
            if (txn != null) {
                Map<String, Object> eventMap = new HashMap<>();
                eventMap.put("reference", txn.getTransactionReference());
                eventMap.put("account", txn.getAccountId());
                eventMap.put("amount", txn.getAmount());
                eventMap.put("type", txn.getTransactionType());
                eventMap.put("balanceAfter", txn.getBalanceAfter());
                eventMap.put("status", "PROCESSED_BY_CAMEL");

                exchange.getIn().setBody(eventMap);
                logger.info("Camel Processed Transaction Event: {}", eventMap);
            }
        }
    }
}

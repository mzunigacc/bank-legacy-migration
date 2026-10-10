package com.example.paymentservice.messaging;

import com.example.paymentservice.event.PaymentProcessedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {

    private static final String TOPIC = "bank.payments";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentProcessedEvent event) {

        String key = event.sourceAccountId() != null
                ? event.sourceAccountId().toString()
                : event.targetAccountId().toString();

        kafkaTemplate.send(
                TOPIC,
                key,
                event
        );

        System.out.printf(
                "[PAYMENT-KAFKA] operationId=%d type=%s amount=%s%n",
                event.operationId(),
                event.operationType(),
                event.amount()
        );
    }
}

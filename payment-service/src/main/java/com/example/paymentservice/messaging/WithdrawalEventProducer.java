package com.example.paymentservice.messaging;

import com.example.paymentservice.event.WithdrawalCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalEventProducer {

    private static final String TOPIC = "bank.withdrawals";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public WithdrawalEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(WithdrawalCreatedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.cuentaId().toString(),
                event
        );

        System.out.printf(
                "[PAYMENT-KAFKA] withdrawalId=%d cuentaId=%d monto=%s%n",
                event.withdrawalId(),
                event.cuentaId(),
                event.monto()
        );
    }
}

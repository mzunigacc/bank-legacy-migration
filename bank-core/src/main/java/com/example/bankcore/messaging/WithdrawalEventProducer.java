package com.example.bankcore.messaging;

import com.example.bankcore.event.WithdrawalCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalEventProducer {

    private static final String TOPIC = "bank.withdrawals";

    private final KafkaTemplate<String, WithdrawalCreatedEvent> kafkaTemplate;

    public WithdrawalEventProducer(
            KafkaTemplate<String, WithdrawalCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(WithdrawalCreatedEvent event) {
        kafkaTemplate.send(
                TOPIC,
                event.accountId().toString(),
                event
        );

        System.out.printf(
                "[KAFKA] WithdrawalCreatedEvent enviado: withdrawalId=%d, accountId=%d, amount=%s%n",
                event.withdrawalId(),
                event.accountId(),
                event.amount()
        );
    }
}
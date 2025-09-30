package com.vnsky.bcss.projectbase.infrastructure.secondary.kafka.adapter;

import com.vnsky.bcss.projectbase.domain.dto.DebitMessageDTO;
import com.vnsky.bcss.projectbase.domain.dto.RegisterSimDTO;
import com.vnsky.bcss.projectbase.domain.port.secondary.KafkaProducerPort;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerAdapter implements KafkaProducerPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String REGISTER_SIM_TOPIC = "register-sim";
    private static final String DEBIT_TOPIC = "debit";

    @Override
    public void publishRegisterSimMessage(RegisterSimDTO registerSimDTO) {
        log.info("Publishing register SIM message to topic '{}' for IMSI: {}", REGISTER_SIM_TOPIC, registerSimDTO.getIsdn());

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(REGISTER_SIM_TOPIC, registerSimDTO.getIsdn().toString(), registerSimDTO);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent message to topic '{}' for IMSI: {} at partition {} with offset {}",
                        REGISTER_SIM_TOPIC, registerSimDTO.getIsdn(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send message to topic '{}' for IMSI: {}", REGISTER_SIM_TOPIC, registerSimDTO.getIsdn(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error sending message to Kafka topic '{}' for IMSI: {}", REGISTER_SIM_TOPIC, registerSimDTO.getIsdn(), e);
            throw BaseException.internalServerError(ErrorCode.KAFKA_PUBLISH_FAILED)
                .message("Failed to publish register SIM message to Kafka: " + e.getMessage())
                .build();
        }
    }

    @Override
    public void publishDebitMessage(DebitMessageDTO debitMessageDTO) {
        log.info("Publishing debit message to topic '{}' for client_id: {} amount: {}", DEBIT_TOPIC, debitMessageDTO.getClientId(), debitMessageDTO.getDebitAmount());

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(DEBIT_TOPIC, debitMessageDTO.getClientId(), debitMessageDTO);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent debit message to topic '{}' for client_id: {} at partition {} with offset {}",
                        DEBIT_TOPIC, debitMessageDTO.getClientId(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send debit message to topic '{}' for client_id: {}", DEBIT_TOPIC, debitMessageDTO.getClientId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error sending debit message to Kafka topic '{}' for client_id: {}", DEBIT_TOPIC, debitMessageDTO.getClientId(), e);
            throw BaseException.internalServerError(ErrorCode.KAFKA_DEBIT_PUBLISH_FAILED)
                .message("Failed to publish debit message to Kafka: " + e.getMessage())
                .build();
        }
    }
}

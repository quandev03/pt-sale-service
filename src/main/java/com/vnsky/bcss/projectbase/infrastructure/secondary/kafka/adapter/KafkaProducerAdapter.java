package com.vnsky.bcss.projectbase.infrastructure.secondary.kafka.adapter;

import com.vnsky.bcss.projectbase.domain.dto.RegisterSimDTO;
import com.vnsky.bcss.projectbase.domain.port.secondary.KafkaProducerPort;
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

    @Override
    public void publishRegisterSimMessage(RegisterSimDTO registerSimDTO) {
        log.info("Publishing register SIM message to topic '{}' for IMSI: {}", REGISTER_SIM_TOPIC, registerSimDTO.getSerial());

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(REGISTER_SIM_TOPIC, registerSimDTO.getSerial(), registerSimDTO);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent message to topic '{}' for IMSI: {} at partition {} with offset {}",
                        REGISTER_SIM_TOPIC, registerSimDTO.getSerial(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send message to topic '{}' for IMSI: {}", REGISTER_SIM_TOPIC, registerSimDTO.getSerial(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error sending message to Kafka topic '{}' for IMSI: {}", REGISTER_SIM_TOPIC, registerSimDTO.getSerial(), e);
            throw new RuntimeException("Failed to publish message to Kafka", e);
        }
    }
}

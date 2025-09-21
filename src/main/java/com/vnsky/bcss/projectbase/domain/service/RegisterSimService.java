package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.RegisterSimDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.RegisterSimServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.KafkaProducerPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.hook.RegisterSimRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterSimService implements RegisterSimServicePort {

    private final KafkaProducerPort kafkaProducerPort;
    private final SubscriberRepoPort subscriberRepoPort;

    private static final int CALLED_900 = 1;

    @Override
    @Transactional
    public void registerSim(RegisterSimRequest request) {
        log.info("Processing register SIM request for IMSI: {}", request.getImsi());
        long imsiLong;
        try {
            imsiLong = Long.parseLong(request.getImsi());
        } catch (NumberFormatException e) {
            log.error("Invalid IMSI format: {}", request.getImsi());
            throw new IllegalArgumentException("Invalid IMSI format");
        }
        Optional<SubscriberDTO> optionalSubscriber = subscriberRepoPort.findByImsi(imsiLong);
        if (optionalSubscriber.isEmpty()) {
            log.error("Subscriber not found for IMSI: {}", request.getImsi());
            throw new IllegalArgumentException("Subscriber not found for IMSI: " + request.getImsi());
        }
        SubscriberDTO subscriber = optionalSubscriber.get();
        subscriber.setStatusCall900(CALLED_900);
        subscriber.setMsgId(request.getMsgId());
        SubscriberDTO updatedSubscriber = subscriberRepoPort.saveAndFlush(subscriber);
        RegisterSimDTO registerSimDTO = RegisterSimDTO.builder()
            .serial(updatedSubscriber.getSerial())
            .build();
        kafkaProducerPort.publishRegisterSimMessage(registerSimDTO);
        log.info("Successfully published register SIM message to Kafka for serial: {}", updatedSubscriber.getSerial());
    }
}

package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.dto.RegisterSimDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.RegisterSimServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.KafkaProducerPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.hook.RegisterSimRequest;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterSimService implements RegisterSimServicePort {

    private final KafkaProducerPort kafkaProducerPort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;
    private final OrganizationUserRepoPort organizationUserRepoPort;

    @Override
    public void registerSim(RegisterSimRequest request) {
        log.info("Processing register SIM request for IMSI: {}", request.getImsi());

        // Create message for Kafka
        RegisterSimDTO registerSimDTO = RegisterSimDTO.builder()
            .isdn(request.getIsdn())
            .msgId(request.getMsgId())
            .imsi(request.getImsi())
            .profile(request.getProfile())
            .createTime(request.getCreateTime())
            .build();

        // Publish to Kafka
        kafkaProducerPort.publishRegisterSimMessage(registerSimDTO);
        log.info("Successfully published register SIM message to Kafka for ISDN: {}", request.getIsdn());
    }
}

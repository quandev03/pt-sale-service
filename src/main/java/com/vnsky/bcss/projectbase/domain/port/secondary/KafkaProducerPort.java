package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.RegisterSimDTO;

public interface KafkaProducerPort {
    void publishRegisterSimMessage(RegisterSimDTO registerSimDTO);
} 
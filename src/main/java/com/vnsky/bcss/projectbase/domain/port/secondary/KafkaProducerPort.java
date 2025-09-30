package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.RegisterSimDTO;
import com.vnsky.bcss.projectbase.domain.dto.DebitMessageDTO;

public interface KafkaProducerPort {
    void publishRegisterSimMessage(RegisterSimDTO registerSimDTO);
    void publishDebitMessage(DebitMessageDTO debitMessageDTO);
}
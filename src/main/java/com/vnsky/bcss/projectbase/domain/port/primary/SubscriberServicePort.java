package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import org.springframework.core.io.Resource;

public interface SubscriberServicePort {
    SubscriberDTO saveAndFlushNewTransaction(SubscriberDTO dto);

    SubscriberDTO findByIsdn(Long isdn);

    Resource downloadFile(String url);
}

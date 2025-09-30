package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.ActiveSubscriberDataDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;

public interface GenContractServicePort {
    void genCustomerCode(ActiveSubscriberDataDTO activeData, SubscriberDTO subscriber);
}

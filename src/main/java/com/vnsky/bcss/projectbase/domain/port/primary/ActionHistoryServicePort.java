package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;

public interface ActionHistoryServicePort {
    ActionHistoryDTO save(String actionCode, String subId);
}

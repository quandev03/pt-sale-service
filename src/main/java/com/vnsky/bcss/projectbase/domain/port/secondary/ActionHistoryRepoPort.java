package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;

import java.util.List;

public interface ActionHistoryRepoPort {
    List<ActionHistoryDTO> getListActionHistoryBySubId(String subId);

    ActionHistoryDTO save(ActionHistoryDTO actionHistoryDTO);

    ActionHistoryDTO findByActionCodeAndSubId(String actionCode, String subId);

}

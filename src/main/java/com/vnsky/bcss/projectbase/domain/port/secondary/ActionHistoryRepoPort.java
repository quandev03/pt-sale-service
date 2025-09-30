package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ActionHistoryRepoPort {
    List<ActionHistoryDTO> getListActionHistoryBySubId(String subId, Sort sort);

    ActionHistoryDTO save(ActionHistoryDTO actionHistoryDTO);

    ActionHistoryDTO saveAndFlush(ActionHistoryDTO actionHistoryDTO);

    List<ActionHistoryDTO> saveAllAndFlush(List<ActionHistoryDTO> actionHistoryDTOs);
}

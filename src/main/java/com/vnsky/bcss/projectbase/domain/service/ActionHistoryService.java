package com.vnsky.bcss.projectbase.domain.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.ActionHistoryServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.ActionHistoryRepoPort;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActionHistoryService implements ActionHistoryServicePort {
    private final ActionHistoryRepoPort actionHistoryRepoPort;
    private static final String SYSTEM = "SYSTEM";

    @Transactional
    @Override
    public ActionHistoryDTO save(String actionCode, String subId) {
        String createdBy = SecurityUtil.getCurrentUserId() == null ? SYSTEM : SecurityUtil.getCurrentUserId();

        ActionHistoryDTO history = ActionHistoryDTO.builder()
            .id(UlidCreator.getUlid().toString())
            .actionCode(actionCode)
            .subId(subId)
            .createdBy(createdBy)
            .actionDate(LocalDateTime.now())
            .reasonCode("VIEW")
            .build();

        return actionHistoryRepoPort.save(history);
    }
}

package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.ParamDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.AppPickListServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.AppPickListRepoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppPickListService implements AppPickListServicePort {
    private final AppPickListRepoPort appPickListRepoPort;

    @Override
    public Map<String, List<ParamDTO>> getAppPickList() {
        return this.appPickListRepoPort.findAllActive()
            .stream()
            .collect(Collectors.groupingBy(
                appPick -> String.format("%s_%s", appPick.getTableName(), appPick.getColumnName()),
                Collectors.mapping(
                    appPick -> ParamDTO.builder()
                        .code(appPick.getCode())
                        .value(appPick.getValue())
                        .build(),
                    Collectors.toList()
                )
            ));
    }
}

package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.ParamDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.AppPickListServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.AppPickListOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AppPickListRest implements AppPickListOperation {

    private final AppPickListServicePort appPickListServicePort;

    @Override
    public ResponseEntity<Map<String, List<ParamDTO>>> getAppPickList() {
        Map<String, List<ParamDTO>> response = this.appPickListServicePort.getAppPickList();
        return ResponseEntity.ok(response);
    }
}

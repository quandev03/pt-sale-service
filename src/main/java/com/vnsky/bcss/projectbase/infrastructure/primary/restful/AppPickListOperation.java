package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.domain.dto.ParamDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Tag(name = "App Pick List", description = "api param")
@RequestMapping({"${application.path.base.private}/params", "${application.path.base.public}/params"})
public interface AppPickListOperation {

    @GetMapping
    ResponseEntity<Map<String, List<ParamDTO>>> getAppPickList();
}

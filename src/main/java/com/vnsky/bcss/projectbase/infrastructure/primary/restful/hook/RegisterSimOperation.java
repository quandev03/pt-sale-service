package com.vnsky.bcss.projectbase.infrastructure.primary.restful.hook;

import com.vnsky.bcss.projectbase.infrastructure.data.request.hook.RegisterSimRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "hook", description = "Hook APIs for external integrations")
@RequestMapping("${application.path.base.public}/hook")
public interface RegisterSimOperation {

    @PostMapping("/register-sim")
    ResponseEntity<Void> registerSim(@Validated @RequestBody RegisterSimRequest request);
} 
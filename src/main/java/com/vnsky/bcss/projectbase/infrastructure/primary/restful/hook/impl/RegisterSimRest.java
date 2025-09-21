package com.vnsky.bcss.projectbase.infrastructure.primary.restful.hook.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.RegisterSimServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.hook.RegisterSimRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.hook.RegisterSimOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RegisterSimRest implements RegisterSimOperation {

    private final RegisterSimServicePort registerSimServicePort;

    @Override
    public ResponseEntity<Void> registerSim(RegisterSimRequest request) {
        registerSimServicePort.registerSim(request);
        return ResponseEntity.ok().build();
    }
} 
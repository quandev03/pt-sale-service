package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.infrastructure.data.request.hook.RegisterSimRequest;

public interface RegisterSimServicePort {
    void registerSim(RegisterSimRequest request);
} 
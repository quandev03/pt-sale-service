package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationLineDTO;

import java.util.List;

public interface EsimRegistrationLineRepoPort {
    EsimRegistrationLineDTO saveAndFlush(EsimRegistrationLineDTO esimRegistrationLineDTO);
    List<EsimRegistrationLineDTO> saveAllAndFlush(List<EsimRegistrationLineDTO> esimRegistrationLineDTOs);
} 
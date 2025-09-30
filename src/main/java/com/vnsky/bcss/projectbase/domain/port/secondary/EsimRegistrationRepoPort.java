package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationDTO;
import com.vnsky.bcss.projectbase.domain.dto.IncompleteRegistrationRowDTO;

import java.util.List;
import java.util.Optional;

public interface EsimRegistrationRepoPort {
    EsimRegistrationDTO saveAndFlush(EsimRegistrationDTO esimRegistrationDTO);

    Optional<EsimRegistrationDTO> findById(String id);

    List<IncompleteRegistrationRowDTO> findIncompleteRegistrations();
}

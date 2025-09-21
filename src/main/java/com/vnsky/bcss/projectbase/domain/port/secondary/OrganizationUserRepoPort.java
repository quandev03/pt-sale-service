package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;

import java.util.Optional;

public interface OrganizationUserRepoPort {
    Optional<OrganizationUserDTO> findByUserId(String userId);
} 
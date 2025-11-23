package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUserServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.UpdateOrganizationUserRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.OrganizationUserPrivateOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class OrganizationUserPrivateRest implements OrganizationUserPrivateOperation {
    private final OrganizationUserServicePort organizationUserServicePort;

    @Override
    public ResponseEntity<Object> save(OrganizationUserDTO request) {
        return ResponseEntity.ok(organizationUserServicePort.savePrivate(request));
    }

    @Override
    public ResponseEntity<OrganizationUserDTO> update(
        @PathVariable String id,
        @Valid @RequestBody UpdateOrganizationUserRequest request) {
        
        // Map request to DTO
        OrganizationUserDTO dto = OrganizationUserDTO.builder()
            .id(id)
            .orgId(request.getOrgId())
            .userId(request.getUserId())
            .userName(request.getUserName())
            .userFullname(request.getUserFullname())
            .email(request.getEmail())
            .status(request.getStatus())
            .isCurrent(request.getIsCurrent())
            .build();

        OrganizationUserDTO updated = organizationUserServicePort.update(dto);
        return ResponseEntity.ok(updated);
    }
}

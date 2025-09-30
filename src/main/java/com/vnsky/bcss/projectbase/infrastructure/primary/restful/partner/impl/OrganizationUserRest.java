package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.service.OrganizationUserService;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.OrganizationUserOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrganizationUserRest implements OrganizationUserOperation {

    private final OrganizationUserService organizationUserService;

    @Override
    public ResponseEntity<OrganizationUserDTO> addOrganizationUnit(OrganizationUserDTO request) {
        return ResponseEntity.ok(organizationUserService.save(request));
    }

    @Override
    public ResponseEntity<Object> updateOrganizationUnit(String userId, String orgId) {
        organizationUserService.updateOrgUnit(userId,orgId);
        return ResponseEntity.noContent().build();
    }

}

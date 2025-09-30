package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.impl;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.CheckOrgParentRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.OrganizationUnitOperation;
import com.vnsky.bcss.projectbase.domain.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrganizationUnitInternalRest implements OrganizationUnitOperation {

    private final OrganizationUnitServicePort organizationUnitServicePort;

    public ResponseEntity<Object> checkOrgParent(@RequestBody CheckOrgParentRequest request){
        return ResponseEntity.ok(organizationUnitServicePort.checkOrgParent(request));
    }

    @Override
    public ResponseEntity<List<OrganizationUnitDTO>> getOrganizationUserChild(String parentId) {
        return ResponseEntity.ok(organizationUnitServicePort.getChild(parentId));
    }

    @Override
    public ResponseEntity<List<UserDTO>> getOrgName(String userId, String clientId, String currentClientId, List<UserDTO> users) {
        return ResponseEntity.ok(organizationUnitServicePort.mapUsersWithOrg(userId, clientId, currentClientId, users));
    }

    @Override
    public ResponseEntity<OrganizationUnitDTO> getOrganizationUnit(String ordId) {
        return ResponseEntity.ok(organizationUnitServicePort.getInfoOrgUnit(ordId));
    }

}

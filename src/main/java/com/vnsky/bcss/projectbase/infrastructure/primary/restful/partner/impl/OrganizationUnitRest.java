package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.CheckOrgParentRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.OrganizationUnitOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrganizationUnitRest implements OrganizationUnitOperation {

    private final OrganizationUnitServicePort organizationUnitServicePort;

    @Override
    public List<GetAllOrganizationUnitResponse> getAllStores(Boolean isAll) {
        return organizationUnitServicePort.getAllStores(isAll);
    }

    @Override
    public ResponseEntity<Object> addOrganizationUnit(OrganizationUnitDTO organizationUnitDTO) {
        return ResponseEntity.ok(organizationUnitServicePort.save(organizationUnitDTO, null));
    }

    @Override
    public ResponseEntity<Object> updateOrganizationUnit(
        OrganizationUnitDTO organizationUnitDTO,
         String id) {
        return ResponseEntity.ok(this.organizationUnitServicePort.save(organizationUnitDTO, id));
    }

    @Override
    public ResponseEntity<Object> getAllPartnersWithoutOrganizationLimit() {
        return ResponseEntity.ok(organizationUnitServicePort.getPartnersWithoutOrganizationLimit());
    }

    @Override
    public ResponseEntity<Object> getOrganizationUnit( String id) {
        return ResponseEntity.ok(this.organizationUnitServicePort.get(id));
    }

    @Override
    public ResponseEntity<Object> deleteOrganizationUnit( String id) {
        this.organizationUnitServicePort.deleteOrganizationUnit(id);
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<Object> getAllOrganizationUnit(Integer status,
                                                         String orgType,
                                                         String orgSubType,
                                                         String textSearch) {
        return ResponseEntity.ok(this.organizationUnitServicePort.getAll(status, orgType, orgSubType, textSearch));
    }

    @Override
    public ResponseEntity<Object> getAuthorizedOrganizationUnitByType(@RequestParam(required = false) String orgSubType){
        return ResponseEntity.ok(organizationUnitServicePort.getActivedAuthorizedOrg(orgSubType));
    }

    public ResponseEntity<Object> checkOrgParent(@RequestBody CheckOrgParentRequest request){
        return ResponseEntity.ok(organizationUnitServicePort.checkOrgParent(request));
    }

    @Override
    public ResponseEntity<List<OrganizationUnitDTO>> getOrganizationUserChild(String parentId) {
        return ResponseEntity.ok(organizationUnitServicePort.getChild(parentId));
    }

    @Override
    public ResponseEntity<List<OrganizationUnitDTO>> getOrganizationUserChildCurrent() {
        return ResponseEntity.ok(organizationUnitServicePort.getChild(organizationUnitServicePort.getOrgCurrent().getId()));
    }

    @Override
    public ResponseEntity<Object> getDebitLimit() {
        return ResponseEntity.ok(this.organizationUnitServicePort.getDebtRevenue());
    }
}

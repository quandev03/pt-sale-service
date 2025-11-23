package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitImageServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.CheckOrgParentRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitImageResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.OrganizationUnitOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrganizationUnitRest implements OrganizationUnitOperation {

    private final OrganizationUnitServicePort organizationUnitServicePort;
    private final OrganizationUnitImageServicePort imageServicePort;

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
                                                         String textSearch,
                                                         String rentalStatus) {
        return ResponseEntity.ok(this.organizationUnitServicePort.getAll(status, orgType, orgSubType, textSearch, rentalStatus));
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

    @Override
    public ResponseEntity<Object> uploadImages(String orgUnitId, List<MultipartFile> files) {
        List<String> imageUrls = imageServicePort.uploadImages(orgUnitId, files);
        return ResponseEntity.ok(OrganizationUnitImageResponse.builder()
            .imageUrls(imageUrls)
            .build());
    }

    @Override
    public ResponseEntity<Object> getImageUrls(String orgUnitId) {
        List<String> imageUrls = imageServicePort.getImageUrls(orgUnitId);
        return ResponseEntity.ok(OrganizationUnitImageResponse.builder()
            .imageUrls(imageUrls)
            .build());
    }

    @Override
    public ResponseEntity<Resource> downloadImage(String orgUnitId, String imageId) {
        Resource resource = imageServicePort.downloadImage(orgUnitId, imageId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

    @Override
    public ResponseEntity<Object> updateImages(String orgUnitId, List<MultipartFile> files) {
        List<String> imageUrls = imageServicePort.updateImages(orgUnitId, files);
        return ResponseEntity.ok(OrganizationUnitImageResponse.builder()
            .imageUrls(imageUrls)
            .build());
    }

    @Override
    public ResponseEntity<List<OrganizationUnitResponse>> getAvailableRooms() {
        List<OrganizationUnitDTO> dtos = organizationUnitServicePort.getAvailableRooms();
        List<OrganizationUnitResponse> responses = dtos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private OrganizationUnitResponse mapToResponse(OrganizationUnitDTO dto) {
        return OrganizationUnitResponse.builder()
            .id(dto.getId())
            .orgCode(dto.getOrgCode())
            .orgName(dto.getOrgName())
            .address(dto.getAddress())
            .phone(dto.getPhone())
            .email(dto.getEmail())
            .priceRoom(dto.getPriceRoom())
            .rentalStatus(dto.getRentalStatus() != null ? dto.getRentalStatus().name() : null)
            .build();
    }
}

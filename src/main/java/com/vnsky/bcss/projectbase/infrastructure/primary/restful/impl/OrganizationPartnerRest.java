package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationPartnerServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PackageClientRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchPartnerResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.OrganizationPartnerOperation;
import com.vnsky.kafka.annotation.AuditAction;
import com.vnsky.kafka.annotation.AuditDetail;
import com.vnsky.kafka.annotation.AuditId;
import com.vnsky.kafka.constant.AuditActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrganizationPartnerRest implements OrganizationPartnerOperation {

    private final OrganizationPartnerServicePort organizationPartnerServicePort;

    @Override
    @AuditAction(targetType = "PARTNER", actionType = AuditActionType.CREATE)
    public ResponseEntity<Object> createPartner(OrganizationUnitDTO organizationUnitDTO, String acceptLanguage) {
        String lang = resolveLang(acceptLanguage);
        OrganizationUnitDTO response = this.organizationPartnerServicePort.createPartner(organizationUnitDTO, lang);
        return ResponseEntity.ok(response);
    }

    private String resolveLang(String acceptLanguageHeader) {
        if (acceptLanguageHeader != null && !acceptLanguageHeader.isBlank()) {
            // lấy token đầu tiên, ví dụ "vi-VN" từ "vi-VN,vi;q=0.9,en-US;q=0.8"
            return acceptLanguageHeader.split(",")[0].trim(); // có thể là "vi", "vi-VN", "en-US", ...
        }
        // fallback theo Locale hiện tại của app (nếu đã cấu hình LocaleResolver) hoặc đặt mặc định "vi"
        LocaleContextHolder.getLocale();
        return LocaleContextHolder.getLocale().toLanguageTag();
    }


    @Override
    @AuditAction(targetType = "PARTNER", actionType = AuditActionType.UPDATE)
    public ResponseEntity<Object> updatePartner(@AuditId String id, OrganizationUnitDTO organizationUnitDTO) {
        OrganizationUnitDTO response = this.organizationPartnerServicePort.updatePartner(id, organizationUnitDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    @AuditDetail(targetType = "PARTNER")
    public ResponseEntity<Object> detailPartner(@AuditId String id) {
        OrganizationUnitDTO response = this.organizationPartnerServicePort.getDetailById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> search(String q, String partnerType, Integer status, Integer approvalStatus, Pageable pageable) {
        Page<SearchPartnerResponse> responses = this.organizationPartnerServicePort.searchPartner(q, partnerType, status, approvalStatus, pageable);
        return ResponseEntity.ok(responses);
    }

    @Override
    @AuditAction(targetType = "PARTNER", actionType = AuditActionType.UPDATE)
    public ResponseEntity<Void> updateStatusPartner(@AuditId String id, Integer status) {
        this.organizationPartnerServicePort.updateStatusPartner(id, status);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<GetAllOrganizationUnitResponse>> getUnitByCode(String code) {
        List<GetAllOrganizationUnitResponse> response = this.organizationPartnerServicePort.getUnitByCode(code);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<GetAllOrganizationUnitResponse>> getAllPartner(String q) {
        List<GetAllOrganizationUnitResponse> response = this.organizationPartnerServicePort.getAllPartner(q);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> getDeliveryInfo(MultipartFile idCardFrontSite, MultipartFile idCardBackSite, MultipartFile portrait) {
        OrganizationDeliveryInfoDTO response = this.organizationPartnerServicePort.getDeliveryInfo(
            new FileAttachments().setIdCardFrontSite(idCardFrontSite).setIdCardBackSite(idCardBackSite).setPortrait(portrait));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> getByCode(String code) {
        OrganizationUnitDTO response = this.organizationPartnerServicePort.getDetailByCode(code);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> createPackageClientForClient(PackageClientRequest packageClientRequest) {
        this.organizationPartnerServicePort.createPackageClientForClient(packageClientRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> getAllPackageByClient(String clientId) {
        return ResponseEntity.ok(organizationPartnerServicePort.getAllPackageByClientId(clientId));
    }

    @Override
    public ResponseEntity<Object> getOrgNBO(String orgPartnerID) {
        return ResponseEntity.ok(organizationPartnerServicePort.getOrgNBOByPartner(orgPartnerID));
    }
}

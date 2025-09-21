package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.impl;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.EsimManagerServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.SendQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.EsimManagerOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class EsimManagerRest implements EsimManagerOperation {

    private final EsimManagerServicePort esimManagerServicePort;
    private final PackageManagerServicePort packageManagerServicePort;

    @Override
    public ResponseEntity<Page<EsimInforDTO>> getListEsimInforDTO(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, Pageable pageable) {
        return ResponseEntity.ok(esimManagerServicePort.getListEsimInforDTO(textSearch, subStatus, activeStatus, pckCode,orgId, pageable));
    }

    @Override
    public ResponseEntity<List<ActionHistoryDTO>> getActionHistoryDTO(String subId) {
        return ResponseEntity.ok(esimManagerServicePort.getActionHistoryDTO(subId));
    }

    @Override
    public ResponseEntity<ESimDetailResponse> getEsimInforDTO(String subId) {
        return ResponseEntity.ok(esimManagerServicePort.detailEsim(subId));
    }

    @Override
    public ResponseEntity<List<PackageProfileDTO>> getPackageProfile() {
        return ResponseEntity.ok(packageManagerServicePort.getAllPackageProfile());
    }

    @Override
    public ResponseEntity<Object> getOrganizationUnit() {
        return ResponseEntity.ok(esimManagerServicePort.getListOrganization());
    }

    @Override
    public ResponseEntity<Resource> export(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("danh_sach_esim.xlsx").build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(esimManagerServicePort.exportListEsimExcelInternal(textSearch, subStatus, activeStatus, pckCode, orgId));
    }

}

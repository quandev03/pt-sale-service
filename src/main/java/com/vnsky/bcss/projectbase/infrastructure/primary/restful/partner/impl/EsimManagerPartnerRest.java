package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.EsimManagerServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.ExportQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.SendQrCodeRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.EsimManagerOperation;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class EsimManagerPartnerRest implements EsimManagerOperation {

    private final EsimManagerServicePort esimManagerServicePort;
    private final PackageManagerServicePort packageManagerServicePort;

    @Override
    public ResponseEntity<Page<EsimInforDTO>> getListEsimInforDTO(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate, Pageable pageable) {
        return ResponseEntity.ok(esimManagerServicePort.getListEsimInforPartnerDTO(textSearch, subStatus, activeStatus, pckCode, orgId, fromDate, toDate, pageable));
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
    public ResponseEntity<Object> sentQrcode(SendQrCodeRequest request) {
        esimManagerServicePort.sendMailEsim(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Resource> sentQrcode(String subId, String size) {
        Resource resource = this.esimManagerServicePort.esimGenerateQR(subId,size);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"esim-qr.png\"")
            .contentType(MediaType.IMAGE_PNG)
            .body(resource);
    }

    @Override
    public ResponseEntity<List<PackageProfileDTO>> getPackageProfile() {
        return ResponseEntity.ok(packageManagerServicePort.getAllPackageProfile());
    }

    @Override
    public ResponseEntity<Resource> esimGenerateQR(String data, String size) {
        Resource resource = esimManagerServicePort.esimGenerateQRCode(data, size);
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"esim-qr.png\"")
            .contentType(MediaType.IMAGE_PNG)
            .body(resource);
    }

    public ResponseEntity<List<OrganizationUnitResponse>> getOrganizationUnit() {
        return ResponseEntity.ok(esimManagerServicePort.getListOrganizationUnit());
    }

    @Override
    public ResponseEntity<Resource> export(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate) {
        String timestamp = DateUtils.localDateTimeToString(LocalDateTime.now(), Constant.DATE_TIME_NO_SYMBOL_PATTERN);
        String filename = "Danh sách eSIM-" + timestamp + ".xlsx";

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(esimManagerServicePort.exportListEsimExcel(textSearch, subStatus, activeStatus, pckCode, orgId, fromDate, toDate));
    }

    @Override
    public ResponseEntity<Resource> exportQrCode(ExportQrCodeRequest request) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("danh_sach_qr_esim.xlsx").build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(esimManagerServicePort.exportListQrCode(request));
    }
}

package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.BatchPackageSaleServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.SaleOrderServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.SubscriberServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.RevenueStatisticOperation;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class RevenueStatisticRest implements RevenueStatisticOperation {
    private final SaleOrderServicePort saleOrderServicePort;
    private final SubscriberServicePort subscriberServicePort;
    private final BatchPackageSaleServicePort batchPackageSaleServicePort;
    private final OrganizationUnitServicePort organizationUnitServicePort;

    @Override
    public ResponseEntity<Object> searchOrderRevenueReport(SearchRevenueReportRequest request, Pageable pageable) {
        return ResponseEntity.ok(saleOrderServicePort.searchOrderRevenueReport(organizationUnitServicePort.getOrgCurrent().getOrgCode(), request, pageable));
    }

    @Override
    public ResponseEntity<byte[]> exportOrderRevenueReport(SearchRevenueReportRequest request) {
        try {
            ByteArrayOutputStream outputStream = saleOrderServicePort.exportOrderRevenueReport(organizationUnitServicePort.getOrgCurrent().getOrgCode(), request);
            byte[] excelBytes = outputStream.toByteArray();
            SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_TIME_NO_SYMBOL_PATTERN);
            String currentDate = sdf.format(new Date());
            String filename = "Bao_cao_don_dat_hang_eSIM-" + currentDate +".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Object> searchSubscriberReport(SearchRevenueReportRequest request, Pageable pageable) {
        return ResponseEntity.ok(subscriberServicePort.searchSubscriberReport(organizationUnitServicePort.getOrgCurrent().getOrgCode(), request, pageable));
    }

    @Override
    public ResponseEntity<byte[]> exportSubscriberReport(SearchRevenueReportRequest request) {
        try {
            ByteArrayOutputStream outputStream = subscriberServicePort.exportSubscriberReport(organizationUnitServicePort.getOrgCurrent().getOrgCode(), request);
            byte[] excelBytes = outputStream.toByteArray();
            SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_TIME_NO_SYMBOL_PATTERN);
            String currentDate = sdf.format(new Date());
            String filename = "Bao_cao_cap_nhat_thong_tin_thue_bao_" + currentDate +".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Object> searchPackageReport(SearchRevenueReportRequest request, Pageable pageable) {
        return ResponseEntity.ok(batchPackageSaleServicePort.searchPackageReport(organizationUnitServicePort.getOrgCurrent().getOrgCode(), request, pageable));
    }

    @Override
    public ResponseEntity<byte[]> exportPackageReport(SearchRevenueReportRequest request) {
        try {
            ByteArrayOutputStream outputStream = batchPackageSaleServicePort.exportPackageReport(organizationUnitServicePort.getOrgCurrent().getOrgCode(), request);
            byte[] excelBytes = outputStream.toByteArray();
            SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_TIME_NO_SYMBOL_PATTERN);
            String currentDate = sdf.format(new Date());
            String filename = "Bao_cao_ban_goi_cho_thue_bao_" + currentDate +".xlsx";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

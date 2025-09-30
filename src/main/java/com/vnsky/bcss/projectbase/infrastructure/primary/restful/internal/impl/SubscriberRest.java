package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.SubscriberServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberStatusResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.SubscriberOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SubscriberRest implements SubscriberOperation {
    private final SubscriberServicePort subscriberServicePort;

    @Override
    public ResponseEntity<Object> findByIsdn(Long isdn) {
        return ResponseEntity.ok(subscriberServicePort.findByIsdn(isdn));
    }

    @Override
    public ResponseEntity<Object> downloadFile(String file) {
        Resource resource = subscriberServicePort.downloadFile(file);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    @Override
    public ResponseEntity<Object> search(String q, Integer status, String orgCode, Pageable pageable) {
        return ResponseEntity.ok(subscriberServicePort.searchSubscriber(q, status, orgCode, pageable));
    }

    @Override
    public ResponseEntity<Object> exportExcel(String q, Integer status, String orgCode) {
        try {
            ByteArrayOutputStream outputStream = subscriberServicePort.exportSubscriber(q, status, orgCode);
            byte[] excelBytes = outputStream.toByteArray();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currentDate = sdf.format(new Date());
            String filename = "Danh_sach_so_" + currentDate +".xlsx";
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
    public ResponseEntity<List<SubscriberStatusResponse>> getAllStatus() {
        return ResponseEntity.ok(subscriberServicePort.getAllStatus());
    }
}

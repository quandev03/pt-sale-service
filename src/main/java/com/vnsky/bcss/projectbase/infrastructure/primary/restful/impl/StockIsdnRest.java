package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.StockIsdnServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.StockIsdnOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class StockIsdnRest implements StockIsdnOperation {
    private final StockIsdnServicePort stockIsdnServicePort;

    @Override
    public ResponseEntity<Object> search(String q, Integer status, String orgCode, Pageable pageable) {
        return ResponseEntity.ok(stockIsdnServicePort.search(q, status, orgCode, pageable));
    }

    @Override
    public ResponseEntity<Object> exportExcel(String q, Integer status, String orgCode) {
        try {
            ByteArrayOutputStream outputStream = stockIsdnServicePort.exportSubscriber(q, status, orgCode);
            byte[] excelBytes = outputStream.toByteArray();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String currentDate = sdf.format(new Date());
            String filename = "Danh_sach_so-" + currentDate +".xlsx";
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

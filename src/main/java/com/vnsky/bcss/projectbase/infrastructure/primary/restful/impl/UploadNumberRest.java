package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionDTO;
import com.vnsky.bcss.projectbase.domain.dto.IsdnUploadDTO;
import com.vnsky.bcss.projectbase.domain.dto.UploadNumberMetadataDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.NumberTransactionDetailServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.UploadNumberServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.UploadNumberOperation;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.NumberTransactionType;
import com.vnsky.common.constant.ExtendedMediaType;
import com.vnsky.excel.dto.ExcelData;
import com.vnsky.excel.service.CsvOperations;
import com.vnsky.excel.service.XlsxOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static com.vnsky.excel.service.CsvOperations.EXT_CSV;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadNumberRest implements UploadNumberOperation {

    private final UploadNumberServicePort uploadNumberResourceService;
    private final NumberTransactionDetailServicePort transactionDetailService;
    private final CsvOperations csvOperations;
    private final XlsxOperations xlsxOperations;

    @Override
    public ResponseEntity<Page<IsdnTransactionDTO>> find(LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable) {
        Page<IsdnTransactionDTO> dtoPage = uploadNumberResourceService.find(fromTime, toTime, pageable);
        return ResponseEntity.ok(dtoPage);
    }

    @Override
    public ResponseEntity<IsdnTransactionDTO> submit(MultipartFile numberFile, UploadNumberMetadataDTO metadata) {
        IsdnTransactionDTO transaction = this.uploadNumberResourceService.submit(numberFile, metadata);
        return ResponseEntity.ok(transaction);
    }

    @Override
    public ResponseEntity<IsdnTransactionDTO> get(String transactionId) {
        return ResponseEntity.ok(transactionDetailService.get(transactionId));
    }

    @Override
    public ResponseEntity<Resource> getSampleCsv() {
        IsdnUploadDTO sampleNumber = IsdnUploadDTO.builder()
            .isdn("")
            .description("")
            .result(Constant.EMPTY_STRING)
            .build();
        List<IsdnUploadDTO> data = List.of(sampleNumber);
        Resource sampleResource = this.csvOperations.writeCsv(new ExcelData<>(new HashMap<>(), data, false), IsdnUploadDTO.class, true);
        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename(Constant.NumberProcessFile.getSampleFileName(NumberTransactionType.UPLOAD, EXT_CSV))
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .header(HttpHeaders.CONTENT_TYPE, ExtendedMediaType.TEXT_CSV_VALUE)
            .body(sampleResource);
    }

    @Override
    public ResponseEntity<Resource> getSampleXlsx() {
        IsdnUploadDTO sampleNumber = IsdnUploadDTO.builder()
            .isdn("")
            .description("")
            .result(Constant.EMPTY_STRING)
            .build();
        List<IsdnUploadDTO> data = List.of(sampleNumber);
        ContentDisposition contentDisposition = ContentDisposition.attachment()
            .filename(Constant.NumberProcessFile.getSampleFileName(NumberTransactionType.UPLOAD))
            .build();
        Resource sampleResource = this.xlsxOperations.writeExcel(new ExcelData<>(new HashMap<>(), data, false), IsdnUploadDTO.class, true);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .header(HttpHeaders.CONTENT_TYPE, ExtendedMediaType.APPLICATION_XLSX_VALUE)
            .body(sampleResource);
    }
}

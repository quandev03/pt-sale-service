package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.ContractServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OcrServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ContractResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.ContractOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ContractRest implements ContractOperation {

    private final OcrServicePort ocrServicePort;
    private final ContractServicePort contractServicePort;

    @Override
    public ResponseEntity<Object> ocr(int typeCard, MultipartFile front, MultipartFile back, MultipartFile portrait) {
        return ResponseEntity.ok(ocrServicePort.callOCRAndFaceCheck(typeCard, front, back, portrait, null));
    }

    @Override
    public ResponseEntity<Object> genContract(
        @RequestPart("request") CreateContractRequest request,
        @RequestPart("frontImage") MultipartFile frontImage,
        @RequestPart("backImage") MultipartFile backImage,
        @RequestPart("portraitImage") MultipartFile portraitImage) throws Exception {
        
        // Set images to request (images không được sử dụng trong genContract, nhưng cần validate)
        request.setFrontImage(frontImage);
        request.setBackImage(backImage);
        request.setPortraitImage(portraitImage);
        
        Resource resource = contractServicePort.genContract(request);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    @Override
    public ResponseEntity<ContractResponse> createContract(
        @RequestPart("request") CreateContractRequest request,
        @RequestPart("frontImage") MultipartFile frontImage,
        @RequestPart("backImage") MultipartFile backImage,
        @RequestPart("portraitImage") MultipartFile portraitImage) {

        // Set images to request
        request.setFrontImage(frontImage);
        request.setBackImage(backImage);
        request.setPortraitImage(portraitImage);

        ContractResponse response = contractServicePort.createContract(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Page<ContractResponse>> listContracts(
        String ownerName,
        String tenantName,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
        Pageable pageable) {

        Page<ContractResponse> contracts = contractServicePort.listContracts(ownerName, tenantName, fromDate, toDate, pageable);
        return ResponseEntity.ok(contracts);
    }
}

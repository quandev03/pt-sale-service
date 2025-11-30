package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.ContractServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OcrServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.GenContractRequest;
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
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import jakarta.validation.Valid;

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
        @RequestPart("request") GenContractRequest request) throws Exception {
        
        Resource resource = contractServicePort.genContract(request);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    @Override
    public ResponseEntity<ContractResponse> createContract(
        @RequestPart(value = "request", required = true) @Valid CreateContractRequest request,
        @RequestPart(value = "frontImage", required = true) MultipartFile frontImage,
        @RequestPart(value = "backImage", required = true) MultipartFile backImage,
        @RequestPart(value = "portraitImage", required = true) MultipartFile portraitImage) {

        // Validate images
        if (frontImage == null || frontImage.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("frontImage must not be null or empty")
                .build();
        }
        if (backImage == null || backImage.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("backImage must not be null or empty")
                .build();
        }
        if (portraitImage == null || portraitImage.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("portraitImage must not be null or empty")
                .build();
        }

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

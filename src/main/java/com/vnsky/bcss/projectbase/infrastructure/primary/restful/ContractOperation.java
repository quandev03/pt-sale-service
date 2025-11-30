package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateContractRequestData;
import com.vnsky.bcss.projectbase.infrastructure.data.request.GenContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ContractResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RequestMapping("${application.path.base.public}/contracts-management")
public interface ContractOperation {
    @PostMapping("/ocr-data")
    ResponseEntity<Object> ocr(
        @RequestParam(name = "typeCard") int typeCard,
        @RequestPart MultipartFile front,
        @RequestPart MultipartFile back,
        @RequestPart MultipartFile portrait);

    @PostMapping(value = "/gen-contract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> genContract(
        @RequestPart("request") @Valid GenContractRequest request) throws Exception;

    @PostMapping(value = "/contracts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ContractResponse> createContract(
        @RequestPart("request") @Valid CreateContractRequestData requestData,
        @RequestPart("frontImage") MultipartFile frontImage,
        @RequestPart("backImage") MultipartFile backImage,
        @RequestPart("portraitImage") MultipartFile portraitImage);

    @GetMapping("/contracts")
    ResponseEntity<Page<ContractResponse>> listContracts(
        @RequestParam(required = false) String ownerName,
        @RequestParam(required = false) String tenantName,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
        @PageableDefault Pageable pageable);
}

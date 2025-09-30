package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.UpdateInformationPartnerServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.GenActiveSubscriberContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.UpdateInformationOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UpdateInformationRest implements UpdateInformationOperation {
    private final UpdateInformationPartnerServicePort updateInformationPartnerServicePort;

    @Override
    public ResponseEntity<Object> checkIsdn(Long isdn) {
        return ResponseEntity.ok(updateInformationPartnerServicePort.checkIsdn(isdn));
    }

    @Override
    public ResponseEntity<Object> ocrPassport(MultipartFile passportFile, String serial) {
        return ResponseEntity.ok(updateInformationPartnerServicePort.ocrPassportStep(passportFile, serial));
    }

    @Override
    public ResponseEntity<Object> faceCheck(MultipartFile portrait, String transactionId) {
        return ResponseEntity.ok(updateInformationPartnerServicePort.faceCheckStep(portrait, transactionId));
    }

    @Override
    public ResponseEntity<Object> genContract(GenActiveSubscriberContractRequest request) {
        updateInformationPartnerServicePort.genContract(request);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> previewDecree13(String transactionId) {
        Resource resource = updateInformationPartnerServicePort.previewDecree13(transactionId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    @Override
    public ResponseEntity<Object> previewConfirmContract(String transactionId) {
        Resource resource = updateInformationPartnerServicePort.previewConfirmContract(transactionId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .contentType(MediaType.APPLICATION_PDF)
            .body(resource);
    }

    @Override
    public ResponseEntity<Object> previewConfirmContractPng(String transactionId) {
        Resource resource = updateInformationPartnerServicePort.previewConfirmContractPng(transactionId);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .contentType(MediaType.IMAGE_PNG)
            .body(resource);
    }

    @Override
    public ResponseEntity<Object> checkSignedContract(String transactionId) {
        return ResponseEntity.ok(updateInformationPartnerServicePort.checkSignedContract(transactionId));
    }

    @Override
    public ResponseEntity<Object> signContract(MultipartFile signature, String transactionId) {
        updateInformationPartnerServicePort.signContract(signature, transactionId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> submitUpdateInformation(String transactionId) {
        updateInformationPartnerServicePort.submit(transactionId);
        return ResponseEntity.noContent().build();
    }
}

package com.vnsky.bcss.projectbase.infrastructure.primary.restful.landingpage.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.ActiveSubscriberServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.GenActiveSubscriberContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.landingpage.ActiveSubscriberOperation;
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
public class ActiveSubscriberRest implements ActiveSubscriberOperation {
    private final ActiveSubscriberServicePort activeSubscriberServicePort;

    @Override
    public ResponseEntity<Object> checkIsdn(Long isdn) {
        return ResponseEntity.ok(activeSubscriberServicePort.checkIsdn(isdn));
    }

    @Override
    public ResponseEntity<Object> getDegree13(HttpHeaders headers) {
        return ResponseEntity.ok(activeSubscriberServicePort.getDegree13(headers));
    }

    @Override
    public ResponseEntity<Object> ocrAndFaceCheck(MultipartFile passport, String serial) {
        return ResponseEntity.ok(activeSubscriberServicePort.ocrPassport(passport, serial));
    }

    @Override
    public ResponseEntity<Object> faceCheck(MultipartFile portrait, String transactionId) {
        return ResponseEntity.ok(activeSubscriberServicePort.faceCheck(portrait, transactionId));
    }

    @Override
    public ResponseEntity<Object> ocrAndFaceCheck(MultipartFile passport, MultipartFile portrait, String serial) {
        return ResponseEntity.ok(activeSubscriberServicePort.ocrAndFaceCheck(passport, portrait, serial));
    }

    @Override
    public ResponseEntity<Object> genContract(GenActiveSubscriberContractRequest request) {
        return ResponseEntity.ok(activeSubscriberServicePort.genActiveSubscriberContract(request));
    }

    @Override
    public ResponseEntity<Object> downloadActiveContract(String id) {
        Resource resource = activeSubscriberServicePort.downloadActiveContract(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .contentType(MediaType.IMAGE_PNG)
            .body(resource);
    }

    @Override
    public ResponseEntity<Object> signContract(GenActiveSubscriberContractRequest request, MultipartFile signature) {
        activeSubscriberServicePort.signContract(request, signature);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> submitActiveSubscriber(String id) {
        activeSubscriberServicePort.submitActiveSubscriber(id);
        return ResponseEntity.noContent().build();
    }
}

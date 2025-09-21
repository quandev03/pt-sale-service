package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.SubscriberServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal.SubscriberOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
}

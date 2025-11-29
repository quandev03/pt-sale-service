package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.OcrServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.ContractOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ContractRest implements ContractOperation {

    private final OcrServicePort ocrServicePort;

    @Override
    public ResponseEntity<Object> ocr(int typeCard, MultipartFile front, MultipartFile back, MultipartFile portrait) {
        return ResponseEntity.ok(ocrServicePort.callOCRAndFaceCheck(typeCard, front, back, portrait, null));
    }

    @Override
    public ResponseEntity<Object> genContract(MultipartFile template) throws Exception {
        return ResponseEntity.ok(ocrServicePort.genContract(template));
    }
}

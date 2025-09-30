package com.vnsky.bcss.projectbase.infrastructure.primary.restful.internal;

import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.GenActiveSubscriberContractRequest;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Update subscriber information partner")
@RequestMapping("${application.path.base.private}/update-subscriber-information")
public interface UpdateInformationOperation {
    @GetMapping("/check-isdn/{isdn}")
    ResponseEntity<Object> checkIsdn(@PathVariable Long isdn);

    @PostMapping(value = "/ocr-passport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> ocrPassport(@RequestPart MultipartFile passportFile,
                                       @RequestPart String serial);


    @PostMapping(value = "/face-check", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> faceCheck(@RequestPart MultipartFile portrait,
                                     @NotEmpty(message = Constant.REQUIRED_FIELD) @RequestPart String transactionId);

    @PostMapping("/gen-contract")
    ResponseEntity<Object> genContract(@Validated @RequestBody GenActiveSubscriberContractRequest request);

    @GetMapping("/preview-decre13/{id}")
    ResponseEntity<Object> previewDecree13(@PathVariable("id") String transactionId);

    @GetMapping("/preview-confirm-contract/{id}")
    ResponseEntity<Object> previewConfirmContract(@PathVariable("id") String transactionId);

    @GetMapping("/preview-confirm-contract-png/{id}")
    ResponseEntity<Object> previewConfirmContractPng(@PathVariable("id") String transactionId);

    @GetMapping("/check-signed-contract/{id}")
    ResponseEntity<Object> checkSignedContract(@PathVariable("id") String transactionId);

    @PostMapping(value = "/sign-contract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> signContract(@RequestPart MultipartFile signature,
                                        @RequestPart String transactionId);

    @PostMapping("/submit/{id}")
    ResponseEntity<Object> submitUpdateInformation(@PathVariable(value = "id") String transactionId);
}

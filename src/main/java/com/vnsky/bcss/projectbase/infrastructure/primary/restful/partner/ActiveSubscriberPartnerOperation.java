package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner;

import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.GenActiveSubscriberContractRequest;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "active-subscriber partner web", description = "Apis for active subscriber")
@RequestMapping("${application.path.base.public}/active-subscriber")
public interface ActiveSubscriberPartnerOperation {
    @GetMapping("/check-isdn/{isdn}")
    ResponseEntity<Object> checkIsdn(@PathVariable Long isdn);

    @GetMapping("/get-degree13")
    ResponseEntity<Object> getDegree13(@RequestHeader HttpHeaders headers);

    @PostMapping(value = "/ocr-passport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> ocrAndFaceCheck(@RequestPart("passport") MultipartFile passport,
                                           @NotEmpty(message = Constant.REQUIRED_FIELD) @RequestPart("serial") String serial);

    @PostMapping(value = "/face-check", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> faceCheck(@RequestPart("portrait") MultipartFile portrait,
                                     @NotEmpty(message = Constant.REQUIRED_FIELD) @RequestPart("transactionId") String transactionId);

    @PostMapping(value = "/ocr-and-face-check", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> ocrAndFaceCheck(@RequestPart("passport") MultipartFile passport,
                                           @RequestPart("portrait") MultipartFile portrait,
                                           @NotEmpty(message = Constant.REQUIRED_FIELD) @RequestPart("serial") String serial);

    @PostMapping(value = "/gen-contract")
    ResponseEntity<Object> genContract(@Validated @RequestBody GenActiveSubscriberContractRequest request);

    @GetMapping("/active-contract/{id}")
    ResponseEntity<Object> downloadActiveContract(@PathVariable String id);

    @PostMapping(value = "/sign-contract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Object> signContract(@Validated @RequestPart GenActiveSubscriberContractRequest request,
                                        @RequestPart("signature") MultipartFile signature);

    @PostMapping("/submit/{id}")
    ResponseEntity<Object> submitActiveSubscriber(@PathVariable String id);
}

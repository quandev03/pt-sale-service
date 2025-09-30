package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.GenActiveSubscriberContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.OcrAndFaceCheckResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface UpdateInformationPartnerServicePort {
    String checkIsdn(Long isdn);

    OcrAndFaceCheckResponse ocrPassportStep(MultipartFile passportFile, String serial);

    OcrAndFaceCheckResponse faceCheckStep(MultipartFile portrait, String transactionId);

    void genContract(GenActiveSubscriberContractRequest request);

    Resource previewConfirmContract(String transactionId);

    Resource previewConfirmContractPng(String transactionId);

    Resource previewDecree13(String transactionId);

    boolean checkSignedContract(String transactionId);

    void signContract(MultipartFile signature, String transactionId);

    void submit(String transactionId);
}

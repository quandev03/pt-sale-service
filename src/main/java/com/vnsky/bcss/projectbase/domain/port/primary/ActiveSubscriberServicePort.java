package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.ActiveSubscriberDataDTO;
import com.vnsky.bcss.projectbase.domain.dto.ApplicationConfigDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.GenActiveSubscriberContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.UpdateSubscriberDataMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.GenContractActiveSubscriberResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.OcrAndFaceCheckResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ActiveSubscriberServicePort {
    String checkIsdn(Long isdn);

    OcrAndFaceCheckResponse ocrPassport(MultipartFile passport, String serial);

    OcrAndFaceCheckResponse faceCheck(MultipartFile portrait, String transactionId);

    OcrAndFaceCheckResponse ocrAndFaceCheck(MultipartFile passport, MultipartFile portrait, String serial);

    GenContractActiveSubscriberResponse genActiveSubscriberContract(GenActiveSubscriberContractRequest request);

    Resource downloadActiveContract(String id);

    void signContract(GenActiveSubscriberContractRequest request, MultipartFile signature);

    void submitActiveSubscriber(String id);

    void processSendActiveDataToMbf(UpdateSubscriberDataMbfRequest updateSubscriberDataMbf, ActiveSubscriberDataDTO activeData);

    List<ApplicationConfigDTO> getDegree13(HttpHeaders headers);
}

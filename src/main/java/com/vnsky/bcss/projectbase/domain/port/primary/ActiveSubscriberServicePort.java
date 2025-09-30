package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.GenActiveSubscriberContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.OcrPassportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.UpdateSubscriberDataMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.GenContractActiveSubscriberResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.OcrAndFaceCheckResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.TypeContract;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ActiveSubscriberServicePort {
    String checkIsdn(Long isdn);

    OcrAndFaceCheckResponse ocrPassport(MultipartFile passport, String serial);

    OcrAndFaceCheckResponse faceCheck(MultipartFile portrait, String transactionId);

    OcrAndFaceCheckResponse ocrAndFaceCheck(MultipartFile passport, MultipartFile portrait, String serial);

    GenContractActiveSubscriberResponse genActiveSubscriberContract(GenActiveSubscriberContractRequest request);

    Resource downloadActiveContract(String id);

    void signContract(GenActiveSubscriberContractRequest request, MultipartFile signature);

    void signContractAndUploadSignature(ActiveSubscriberDataDTO activeData, MultipartFile signature, String transactionId);

    void submitActiveSubscriber(String id);

    void processSendActiveDataToMbf(UpdateSubscriberDataMbfRequest updateSubscriberDataMbf, ActiveSubscriberDataDTO activeData, boolean decree13);

    List<ApplicationConfigDTO> getDegree13(HttpHeaders headers);

    Resource downloadFile(String url);

    String buildBase64Image(MultipartFile file);

    OcrAndFaceCheckResponse initOcrResponse(String serial);

    String processOcrAndGetUrl(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String passportBase64, MultipartFile passportFile);

    boolean sendOcrRequest(OcrPassportRequest ocrRequst, OcrAndFaceCheckResponse ocrAndFaceCheckResponse);

    ActiveSubscriberDataDTO buildActiveDataAtOcrStep(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String passportUrl, Long isdn);

    String uploadFileToMinio(byte[] bytes, String folderPath, String fileName);

    String uploadFileToMinio(MultipartFile file, String folderPath, String transactionId, String fileName);

    String uploadFileToMinio(InputStream inputStream, String folderPath, String fileName);

    String processFaceCheckAndGetUrl(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String portraitBase64, MultipartFile portraitFile);

    String buildTempContractUrl(String transactionId);

    Long formatIsdn(Long isdn);

    ContractFileUrlDTO signActiveContractAndGetUrl(ActiveSubscriberDataDTO activeData, byte[] bytes, TypeContract typeContract, boolean isFinal);

    List<List<String>> buildArrImagesForUpdateMbf(ActiveSubscriberDataDTO activeData);

    ModifyInforParamsDTO getModifyInfoParamFromCache();

    SubscriberDTO checkSubscriberIsNotVerified(String serial);

    SubscriberDTO checkSubscriberIsNotVerified(Long isdn);

    SubscriberDTO checkSubscriberIsNotVerified(SubscriberDTO subscriber);

    byte[] getCskhSignature(String userId, String transactionId);

    void genDecree13(ActiveSubscriberDataDTO activeData, byte[] signature);

}

package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.config.ParamContractConfig;
import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.mapper.ActiveSubscriberMapper;
import com.vnsky.bcss.projectbase.domain.mapper.SubscriberMapper;
import com.vnsky.bcss.projectbase.domain.port.primary.*;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.GenActiveSubscriberContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.UpdateSubscriberDataMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.OcrAndFaceCheckResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.*;
import com.vnsky.bcss.projectbase.shared.utils.DataUtils;
import com.vnsky.bcss.projectbase.shared.utils.PdfContractUtils;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import com.vnsky.redis.component.RedisStoreOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateInformationPartnerService implements UpdateInformationPartnerServicePort {
    private final ActiveSubscriberServicePort activeSubscriberServicePort;
    private final RedisStoreOperation redisStoreOperation;
    private final ObjectMapper objectMapper;
    private final MinioOperations minioOperations;
    private final ActiveSubscriberMapper activeSubscriberMapper;
    private final ParamContractConfig paramContractConfig;
    private final SubscriberMapper subscriberMapper;
    private final ActionHistoryServicePort actionHistoryServicePort;
    private final SubscriberServicePort subscriberServicePort;
    private final GenContractServicePort genContractServicePort;

    private static final Integer CARD_TYPE = 2;
    private static final String UPDATE_INFORMATION_REDIS_KEY = "UPDATE_INFORMATION_TRANSACTION::";
    private static final String SIGNATURE_FILE_NAME = "signature.jpg";
    private static final String BBXN_FILE_NAME = "BBXN.";
    private static final String DEFAULT_ACTIVE_CONTRACT_FILE_NAME = "BBXN.png";
    private static final String ACTIVE_CONTRACT_FILE_NAME = "BBXN.";


    @Override
    public String checkIsdn(Long isdn) {
        isdn = activeSubscriberServicePort.formatIsdn(isdn);
        log.info("[UPDATE_INFORMATION_STEP_1]: Checking isdn {}", isdn);
        SubscriberDTO subscriber = activeSubscriberServicePort.checkSubscriberIsNotVerified(isdn);
        return subscriber.getSerial();
    }

    @Override
    public OcrAndFaceCheckResponse ocrPassportStep(MultipartFile passportFile, String serial) {
        log.info("[UPDATE_INFORMATION_STEP_2]: OCR Passport for serial {}" , serial);
        String passportBase64 = activeSubscriberServicePort.buildBase64Image(passportFile);

        //B0: Kiểm tra trạng thái serial sim
        SubscriberDTO eSim = activeSubscriberServicePort.checkSubscriberIsNotVerified(serial);

        //B0: Cài đặt transactionId
        OcrAndFaceCheckResponse ocrAndFaceCheckResponse = activeSubscriberServicePort.initOcrResponse(serial);
        String transactionId = UUID.randomUUID().toString();
        ocrAndFaceCheckResponse.setTransactionId(transactionId);
        ocrAndFaceCheckResponse.setIsdn(eSim.getIsdn());

        //B1: Tiến hành OCR
        String passportUrl = activeSubscriberServicePort.processOcrAndGetUrl(ocrAndFaceCheckResponse, passportBase64, passportFile);

        //Nếu verify Hộ chiếu thành công
        if(Objects.equals(ocrAndFaceCheckResponse.getStatus(), Status.ACTIVE.getValue())){
            ActiveSubscriberDataDTO activeData = activeSubscriberServicePort.buildActiveDataAtOcrStep(ocrAndFaceCheckResponse, passportUrl, eSim.getIsdn());
            activeData.setCustomerCode(eSim.getCustomerCode());
            activeData.setContractCode(eSim.getContractCode());
            cacheUpdateInformationDataToRedis(transactionId, activeData);
        }

        return ocrAndFaceCheckResponse;
    }

    @Override
    public OcrAndFaceCheckResponse faceCheckStep(MultipartFile portrait, String transactionId) {
        log.info("[UPDATE_INFORMATION_STEP_3]: Start face check for transaction {}", transactionId);
        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(transactionId);

        if(!Objects.equals(activeData.getStepActive(), ActiveSubscriberStep.VERIFIED_OCR.getValue())){
            log.error("[UPDATE_INFORMATION_STEP_3]: Giao dịch chưa xác thực OCR thành công {}", transactionId);
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_VERIFIED_OCR)
                    .message("Giao dịch cập nhật thông tin thuê bao chưa được OCR thành công: " + transactionId).build();
        }

        //B1: Khởi tạo giá trị
        OcrAndFaceCheckResponse ocrAndFaceCheckResponse = activeSubscriberServicePort.initOcrResponse(activeData.getSerial());
        ocrAndFaceCheckResponse.setTransactionId(transactionId);
        ocrAndFaceCheckResponse.setIdEkyc(activeData.getIdEkyc());
        ocrAndFaceCheckResponse.setOcrData(activeData.getOcrData());
        ocrAndFaceCheckResponse.setIsdn(activeData.getIsdn());
        String portraitBase64 = activeSubscriberServicePort.buildBase64Image(portrait);

        //B2: Tiến hành face check
        String portraitUrl = activeSubscriberServicePort.processFaceCheckAndGetUrl(ocrAndFaceCheckResponse, portraitBase64, portrait);
        //Face check thất bại thì trả về luôn
        if(Objects.equals(ocrAndFaceCheckResponse.getStatus(), Status.INACTIVE.getValue())){
            return ocrAndFaceCheckResponse;
        }

        //B3: Cache thông tin vào redis
        activeData.setPortraitUrl(portraitUrl);
        activeData.setStepActive(ActiveSubscriberStep.VERIFIED_FACE.getValue());
        cacheUpdateInformationDataToRedis(transactionId, activeData);

        return ocrAndFaceCheckResponse;
    }

    @Override
    public void genContract(GenActiveSubscriberContractRequest request) {
        log.info("[UPDATE_INFORMATION_STEP_4 ]: Received gen contract for transactionId {}", request.getTransactionId());

        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(request.getTransactionId());

        //Nếu trạng thái của giao dịch không phải chờ gen hợp đồng thì báo lỗi
        if(activeData.getStepActive() < ActiveSubscriberStep.VERIFIED_FACE.getValue()){
            log.error("[UPDATE_INFORMATION_STEP_4]: Giao dịch chưa xác thực khuôn mặt thành công {}", request.getTransactionId());
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_WAITING_GEN_CONTRACT).build();
        }

        //B1: Map dữ liệu đồng ý nghị định 13 cho activeData
        activeData.setAgreeDegree13(request.getAgreeDegree13());

        //Lấy thông tin customerCode và contractCode
        SubscriberDTO subscriber = activeSubscriberServicePort.checkSubscriberIsNotVerified(activeData.getSerial());
        genContractServicePort.genCustomerCode(activeData, subscriber);

        //B2: Upload hợp đồng kích hoạt
        ContractFileUrlDTO activeContractUrl = uploadActiveSubscriberContract(activeData, request);
        activeData.setContractPdfUrl(activeContractUrl.getPdfUrl());
        activeData.setContractPngUrl(activeContractUrl.getPngUrl());
        activeData.setStepActive(ActiveSubscriberStep.VERIFIED_FACE.getValue());

        //B3: Gen nghị định 13
        activeSubscriberServicePort.genDecree13(activeData, new byte[0]);

        //B3: Cập nhật trạng thái giao dịch trong cáche
        cacheUpdateInformationDataToRedis(request.getTransactionId(), activeData);
    }

    @Override
    public Resource previewConfirmContract(String transactionId) {
        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(transactionId);

        DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
                .uri(activeData.getContractPdfUrl())
                .isPublic(false)
                .build();
        return minioOperations.download(downloadOptionDTO);
    }

    @Override
    public Resource previewConfirmContractPng(String transactionId) {
        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(transactionId);

        DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
            .uri(activeData.getContractPngUrl())
            .isPublic(false)
            .build();
        return minioOperations.download(downloadOptionDTO);
    }

    @Override
    public Resource previewDecree13(String transactionId) {
        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(transactionId);

        DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
            .uri(activeData.getAgreeDecree13PdfUrl())
            .isPublic(false)
            .build();
        return minioOperations.download(downloadOptionDTO);
    }

    @Override
    public boolean checkSignedContract(String transactionId) {
        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(transactionId);
        return Objects.equals(activeData.getStepActive(), ActiveSubscriberStep.WAITING_SUBMIT.getValue());
    }

    @Transactional
    @Override
    public void signContract(MultipartFile signature, String transactionId) {
        log.info("[UPDATE_INFORMATION_STEP_5]: Sign contract for transactionId {}", transactionId);

        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(transactionId);
        if(activeData.getStepActive() < ActiveSubscriberStep.VERIFIED_FACE.getValue()){
            log.error("[UPDATE_INFORMATION_STEP_5]: Trạng thái của giao dịch {} không phải là chờ ký hợp đồng", transactionId);
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_WAITING_SIGN_CONTRACT).build();
        }

        //b1: Ký hợp đồng kich hoạt
        signContractAndUploadSignature(activeData, signature, transactionId);

        //b2: Ký nghị định 13
        try {
            activeSubscriberServicePort.genDecree13(activeData, signature.getBytes());
        } catch (IOException e) {
            log.error("[UPDATE_INFORMATION_STEP_5]: Không thể ký nghị định 13 cho giao dịch {}", transactionId);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message(e.getMessage())
                .addProblemDetail(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage()))
                .build();
        }
        cacheUpdateInformationDataToRedis(transactionId, activeData);
    }

    private void signContractAndUploadSignature(ActiveSubscriberDataDTO activeData, MultipartFile signature, String transactionId){
        try {
            byte[] bytes = signature.getBytes();

            ContractFileUrlDTO contract = signActiveContractAndGetUrl(activeData, bytes);
            String signatureUrl = activeSubscriberServicePort.uploadFileToMinio(bytes, Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, transactionId + "/" + SIGNATURE_FILE_NAME);

            activeData.setStepActive(ActiveSubscriberStep.WAITING_SUBMIT.getValue());
            activeData.setContractPngUrl(contract.getPngUrl());
            activeData.setContractPdfUrl(contract.getPdfUrl());
            activeData.setSignatureUrl(signatureUrl);
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER_STEP_4]: Không thể ký hợp đồng cho giao dịch {}", transactionId, e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL).message(e.getMessage()).build();
        }
    }

    private ContractFileUrlDTO signActiveContractAndGetUrl(ActiveSubscriberDataDTO activeData, byte[] bytes){
        GenContractDTO genContractData = activeSubscriberMapper.mapGenContractDto(activeData);
        // Lấy thông tin chữ ký người thực hiện
        genContractData.setSignatureCskh(activeSubscriberServicePort.getCskhSignature(activeData.getUserId(), activeData.getTransactionId()));
        genContractData.setEmployeeName(activeData.getEmployeeFullName());

        DownloadOptionDTO downloadOptionDTO = getDownloadActiveContractOption();

        Resource templateFile = getTemplateFile(downloadOptionDTO);
        try {
            genContractData.setSignatureCustomer(bytes);

            byte[] templateData = IOUtils.toByteArray(templateFile.getInputStream());

            ByteArrayOutputStream contractPdf = PdfContractUtils.fillDataToPdf(templateData, genContractData, GenContractDTO.class);
            ByteArrayOutputStream contractPng = PdfContractUtils.convertPdfToOneJpgFile(contractPdf.toByteArray(), false);

            String tempDirUrl = activeSubscriberServicePort.buildTempContractUrl(activeData.getTransactionId());
            String pdfUrl = activeSubscriberServicePort.uploadFileToMinio(new ByteArrayInputStream(contractPdf.toByteArray()), tempDirUrl, BBXN_FILE_NAME + TypeContract.PDF);
            String pngUrl = activeSubscriberServicePort.uploadFileToMinio(new ByteArrayInputStream(contractPng.toByteArray()), tempDirUrl, BBXN_FILE_NAME + TypeContract.PNG);

            return ContractFileUrlDTO.builder()
                .pdfUrl(pdfUrl)
                .pngUrl(pngUrl)
                .build();
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: không thể ký hợp đồng cho giao dịch");
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message(e.getMessage())
                .build();
        }
    }

    @Transactional
    @Override
    public void submit(String transactionId) {
        ActiveSubscriberDataDTO activeSubscriberData = getActiveDataFromRedis(transactionId);
        log.info("[UPDATE_INFORMATION_STEP_6]: Submit active subscriber with transaction id {}", transactionId);

        if(!Objects.equals(activeSubscriberData.getStepActive(), ActiveSubscriberStep.WAITING_SUBMIT.getValue())){
            log.error("[UPDATE_INFORMATION_STEP_6]: Trạng thái của giao dịch {} không phải là chờ nộp đơn", transactionId);
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_WAITING_SUBMIT).build();
        }

        //Gen lại ảnh hợp đồng mờ đi
        try {
            Resource pdfFile = activeSubscriberServicePort.downloadFile(activeSubscriberData.getContractPdfUrl());
            ByteArrayOutputStream finalContractImg = PdfContractUtils.convertPdfToOneJpgFile(pdfFile.getContentAsByteArray(), true);
            String finalContractUrl = activeSubscriberServicePort.uploadFileToMinio(finalContractImg.toByteArray(), Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, transactionId + "/" + ACTIVE_CONTRACT_FILE_NAME + TypeContract.PNG);
            activeSubscriberData.setContractPngUrl(finalContractUrl);
        } catch (Exception e) {
            log.error("Exception when generate final contract png", e);
            throw BaseException.badRequest(ErrorCode.ATTEMPT_GET_ACTIVE_RESULT_EXCEED_MAX_TIMES)
                .addProblemDetail(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage()))
                .build();
        }

        SubscriberDTO esimRegistration = activeSubscriberServicePort.checkSubscriberIsNotVerified(activeSubscriberData.getSerial());

        //B1: Map thông tin từ cache vào Esim Registration
        subscriberMapper.mapFromSubmitData(esimRegistration, activeSubscriberData);

        //B2: Lưu lại thông tin
        subscriberServicePort.saveAndFlushNewTransaction(esimRegistration);

        //B32: Tải file ảnh hộ chiếu, chân dung, các file hợp đồng gửi sang mobifone
        List<List<String>> buildArrImagesForUpdateMbf = activeSubscriberServicePort.buildArrImagesForUpdateMbf(activeSubscriberData);

        //B3: Map thông tin kích hoạt gửi sang MBF
        ModifyInforParamsDTO params = activeSubscriberServicePort.getModifyInfoParamFromCache();
        UpdateSubscriberDataMbfRequest updateSubscriberDataMbf = subscriberMapper.mapUpdateMbfData(esimRegistration, activeSubscriberData, buildArrImagesForUpdateMbf, params);

        //B4: Gọi sang MBF đồng bộ thông tin
        activeSubscriberServicePort.processSendActiveDataToMbf(updateSubscriberDataMbf, activeSubscriberData, true);

        redisStoreOperation.release(buildUpdateInformationTransactionKey(transactionId));

        //Lưu lại thông tin lịch sử tác động
        actionHistoryServicePort.save(ActionHistoryActionCode.UPDATE_INFO.getCode(), esimRegistration.getId());
    }

    private ContractFileUrlDTO uploadActiveSubscriberContract(ActiveSubscriberDataDTO activeData, GenActiveSubscriberContractRequest genContractRequest){
        GenContractDTO genContractData = activeSubscriberMapper.mapGenContractDto(activeData);
        // Lấy thông tin chữ ký người thực hiện
        genContractData.setSignatureCskh(activeSubscriberServicePort.getCskhSignature(activeData.getUserId(), activeData.getTransactionId()));
        genContractData.setEmployeeName(activeData.getEmployeeFullName());

        DownloadOptionDTO downloadOptionDTO = getDownloadActiveContractOption();

        Resource templateFile = getTemplateFile(downloadOptionDTO);
        try {
            byte[] templateData = IOUtils.toByteArray(templateFile.getInputStream());

            ByteArrayOutputStream contractPdFUrl = PdfContractUtils.fillDataToPdf(templateData, genContractData, GenContractDTO.class);
            ByteArrayOutputStream contractPngUrl = PdfContractUtils.convertPdfToOneJpgFile(contractPdFUrl.toByteArray(), false);

            String pdfUrl = activeSubscriberServicePort.uploadFileToMinio(new ByteArrayInputStream(contractPdFUrl.toByteArray()), activeSubscriberServicePort.buildTempContractUrl(activeData.getTransactionId()), BBXN_FILE_NAME + TypeContract.PDF);

            String pngUrl = activeSubscriberServicePort.uploadFileToMinio(new ByteArrayInputStream(contractPngUrl.toByteArray()), activeSubscriberServicePort.buildTempContractUrl(activeData.getTransactionId()), BBXN_FILE_NAME + TypeContract.PNG);

            return ContractFileUrlDTO.builder()
                .pngUrl(pngUrl)
                .pdfUrl(pdfUrl)
                .build();
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: không thể sinh hợp đồng cho giao dịch {}", genContractRequest.getTransactionId(), e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                    .message(e.getMessage())
                    .build();
        }
    }

    private Resource getTemplateFile(DownloadOptionDTO downloadOption){
        return minioOperations.download(downloadOption);
    }

    private void cacheUpdateInformationDataToRedis(String transactionId, ActiveSubscriberDataDTO activeData){
        try {
            String transactionKey = buildUpdateInformationTransactionKey(transactionId);
            String compressedJson = DataUtils.compress(objectMapper.writeValueAsString(activeData));
            redisStoreOperation.putWithExpireTime(transactionKey, compressedJson,  1, TimeUnit.DAYS);
            log.info("[UPDATE_INFORMATION]: cache active subscriber data to redis successfully");
        } catch (JsonProcessingException e) {
            log.error("[UPDATE_INFORMATION]: Cannot write object to json string", e);
            throw BaseException.badRequest(ErrorCode.CAN_NOT_CACHE_ACTIVE_DATA).build();
        }
    }

    private String buildUpdateInformationTransactionKey(String transactionId){
        return UPDATE_INFORMATION_REDIS_KEY + transactionId;
    }

    private DownloadOptionDTO getDownloadActiveContractOption(){
        return DownloadOptionDTO.builder()
                .uri(paramContractConfig.getTemplate().getContractSimActiveFolder(),
                        paramContractConfig.getTemplate().getContractSimActiveFile())
                .isPublic(false)
                .build();
    }

    private ActiveSubscriberDataDTO getActiveDataFromRedis(String transactionId){
        String transactionKey = buildUpdateInformationTransactionKey(transactionId);
        String activeSubscriberData = (String) redisStoreOperation.get(transactionKey);
        if(activeSubscriberData == null){
            log.error("[UPDATE_INFORMATION]: Cannot find active subscriber data with transactionId {} in redis", transactionId);
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_DATA_NOT_FOUND_IN_REDIS).build();
        }

        try {
            String decompressedJson = DataUtils.decompress(activeSubscriberData);
            return objectMapper.readValue(decompressedJson, ActiveSubscriberDataDTO.class);
        } catch (JsonProcessingException e) {
            log.error("[UPDATE_INFORMATION]: Cannot parse active subscriber data from redis with transactionId: {}", transactionId, e);
            throw BaseException.badRequest(ErrorCode.CAN_NOT_PARSE_ACTIVE_SUBSCRIBER_DATA_FROM_REDIS).build();
        }
    }
}

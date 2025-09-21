package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.config.ParamContractConfig;
import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.mapper.SubscriberMapper;
import com.vnsky.bcss.projectbase.domain.port.primary.ActiveSubscriberServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.ContractHandleServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.SubscriberServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.ApplicationConfigRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.*;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.*;
import com.vnsky.bcss.projectbase.domain.mapper.ActiveSubscriberMapper;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.*;
import com.vnsky.bcss.projectbase.shared.pdf.ContractUtils;
import com.vnsky.bcss.projectbase.shared.utils.DataUtils;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.DeleteOptionDTO;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.redis.component.RedisStoreOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.vnsky.minio.operation.MinioOperations;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActiveSubscriberService implements ActiveSubscriberServicePort {
    private final IntegrationPort integrationPort;
    private final MinioOperations minioOperations;
    private final RedisStoreOperation redisStoreOperation;
    private final ObjectMapper objectMapper;
    private final ContractHandleServicePort contractHandleServicePort;
    private final ParamContractConfig paramContractConfig;
    private final ActiveSubscriberMapper activeSubscriberMapper;
    private final SubscriberMapper subscriberMapper;
    private final SubscriberRepoPort subscriberRepoPort;
    private final ApplicationConfigRepoPort applicationConfigRepoPort;
    private final SubscriberServicePort subscriberServicePort;
    private final ApplicationContext applicationContext;

    private static final String ACTIVE_SUBSCRIBER_TRANSACTION_REDIS_KEY = "ACTIVE_SUBSCRIBER_TRANSACTION::";
    private static final String VNSKY_EKYC_PASSPORT = "VNSKY_EKYC_PASSPORT";
    private static final String OCR_TYPE = "OCR";
    private static final String FACE_CHECK_TYPE = "FACE_CHECK";
    private static final Integer CARD_TYPE = 2;
    private static final String SUCCESS_CODE = "00";
    private static final String ARR_IMAGES_IMAGE_TYPE = "0";
    private static final String ARR_IMAGES_PORTRAIT_TYPE = "1";
    private static final String ARR_IMAGES_CONTRACT_TYPE = "2";
    private static final String DEFAULT_PASS_PORT_FILE_NAME = "Hộ chiếu.jpg";
    private static final String DEFAULT_PORTRAIT_FILE_NAME = "Chân dung.jpg";
    private static final String DEFAULT_ACTIVE_CONTRACT_FILE_NAME = "BBXN.pdf";
    private static final Integer MAX_TIME_ATTEMPT_GET_ACTIVE_RESULT = 5;
    private static final String DEGREE_13_CONFIG_TYPE = "SUB_DOCUMENT_ND13";
    private static final String DEFAULT_LANGUAGE = "vi-vn";
    private static final String MBF_CMD = "MBF";
    private static final String UPDATE_SUBSCRIBER_INFO_TYPE = "MODIFY_INFO";
    private static final String DECREE13_TYPE = "DECREE13";
    private static final String MODIFY_INFO_PREPAID_SUCCESS_CODE = "0000";
    private static final String SUBMIT_DECREE13_SUCCESS_CODE = "SUCCESS";
    private static final String MESSAGE = "message";

    //Tham số tuyền sang api cập nhật thông tin MBF
    private static final String MODIFY_INFOR_TABLE_NAME = "MODIFY_INFO_MBF";
    private static final String ARR_SUB_TYPE = "ARR_SUB_TYPE";
    private static final String CUST_TYPE = "CUST_TYPE";
    private static final String STR_REASON_CODE = "STR_REASON_CODE";
    private static final String STR_ACTION_FLAG = "STR_ACTION_FLAG";
    private static final String STR_APP_OBJECT = "STR_APP_OBJECT";
    private static final String MODIFY_INFO_PARAM_CACHE_KEY = "MODIFY_INFO_PARAM_CACHE_KEY";


    @Value("${hi-vn-service.channel-code}")
    private String hiVnChannelCode;

    @Override
    public String checkIsdn(Long isdn) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_1]: Checking isdn {}", isdn);
        SubscriberDTO esimRegistration = checkSubscriberIsVerified(isdn);
        return esimRegistration.getSerial();
    }

    @Override
    public OcrAndFaceCheckResponse ocrPassport(MultipartFile passport, String serial) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_2]: Start ocr for serial {}", serial);
        String passportBase64 = buildBase64Image(passport);

        //B0: Kiểm tra trạng thái serial sim
        SubscriberDTO eSim = checkSubscriberIsVerified(serial);

        //B0: Cài đặt transactionId
        OcrAndFaceCheckResponse ocrAndFaceCheckResponse = initOcrResponse(serial);
        String transactionId = UUID.randomUUID().toString();
        ocrAndFaceCheckResponse.setTransactionId(transactionId);

        //B1: Tiến hành OCR
        String passportUrl = processOcrAndGetUrl(ocrAndFaceCheckResponse, passportBase64, passport);

        //Nếu verify Hộ chiếu thành công
        if(Objects.equals(ocrAndFaceCheckResponse.getStatus(), Status.ACTIVE.getValue())){
            ActiveSubscriberDataDTO activeData = ActiveSubscriberDataDTO.builder()
                .ocrData(ocrAndFaceCheckResponse.getOcrData())
                .passportUrl(passportUrl)
                .serial(ocrAndFaceCheckResponse.getSerial())
                .stepActive(ActiveSubscriberStep.VERIFIED_OCR.getValue())
                .transactionId(ocrAndFaceCheckResponse.getTransactionId())
                .idEkyc(ocrAndFaceCheckResponse.getIdEkyc())
                .isdn(eSim.getIsdn())
                .build();

            String transactionKey = buildActiveSubscriberTransactionKey(activeData.getTransactionId());
            try {
                String compressedJson = DataUtils.compress(objectMapper.writeValueAsString(activeData));
                redisStoreOperation.putWithExpireTime(transactionKey, compressedJson,  1, TimeUnit.DAYS);
                log.info("[ACTIVE_SUBSCRIBER_STEP_1]: cache active subscriber data to redis successfully");
            } catch (JsonProcessingException e) {
                log.error("[ACTIVE_SUBSCRIBER_STEP_1]: Cannot write object to json string", e);
                throw BaseException.badRequest(ErrorCode.CAN_NOT_CACHE_ACTIVE_DATA).build();
            }
        }

        return ocrAndFaceCheckResponse;
    }

    @Override
    public OcrAndFaceCheckResponse faceCheck(MultipartFile portrait, String transactionId) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_2]: Start face check for transaction {}", transactionId);
        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(transactionId);

        if(!Objects.equals(activeData.getStepActive(), ActiveSubscriberStep.VERIFIED_OCR.getValue())){
            log.error("[ACTIVE_SUBSCRIBER]: Giao dịch chưa xác thực OCR thành công {}", transactionId);
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_VERIFIED_OCR)
                .message("Giao dịch kích hoạt chưa được OCR thành công: " + transactionId).build();
        }

        //B1: Khởi tạo giá trị
        OcrAndFaceCheckResponse ocrAndFaceCheckResponse = initOcrResponse(activeData.getSerial());
        ocrAndFaceCheckResponse.setTransactionId(transactionId);
        ocrAndFaceCheckResponse.setIdEkyc(activeData.getIdEkyc());
        ocrAndFaceCheckResponse.setOcrData(activeData.getOcrData());
        String portraitBase64 = buildBase64Image(portrait);

        //B2: Tiến hành face check
        String portraitUrl = processFaceCheckAndGetUrl(ocrAndFaceCheckResponse, portraitBase64, portrait);
        //Face check thất bại thì trả về luôn
        if(Objects.equals(ocrAndFaceCheckResponse.getStatus(), Status.INACTIVE.getValue())){
            return ocrAndFaceCheckResponse;
        }

        //B3: Cache thông tin vào redis
        cacheOcrDataToRedis(ocrAndFaceCheckResponse, activeData.getPassportUrl(), portraitUrl);

        return ocrAndFaceCheckResponse;
    }

    @Override
    public OcrAndFaceCheckResponse ocrAndFaceCheck(MultipartFile passport, MultipartFile portrait, String serial) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_2]: Start ocr and face check for serial {}", serial);
        String passportBase64 = buildBase64Image(passport);
        String portraitBase64 = buildBase64Image(portrait);

        OcrAndFaceCheckResponse ocrAndFaceCheckResponse = initOcrResponse(serial);

        //B-1: Kiểm tra trạng thái serial sim
        checkSubscriberIsVerified(serial);

        //B0: Cài đặt transactionId
        String transactionId = UUID.randomUUID().toString();
        ocrAndFaceCheckResponse.setTransactionId(transactionId);

        //B1: Tiến hành OCR
        String passportUrl = processOcrAndGetUrl(ocrAndFaceCheckResponse, passportBase64, passport);

        //Ocr thất bại thì trả về luôn
        if(Objects.equals(ocrAndFaceCheckResponse.getStatus(), Status.INACTIVE.getValue())){
            return ocrAndFaceCheckResponse;
        }

        //B2: Tiến hành face check
        String portraitUrl = processFaceCheckAndGetUrl(ocrAndFaceCheckResponse, portraitBase64, portrait);
        //Face check thất bại thì trả về luôn
        if(Objects.equals(ocrAndFaceCheckResponse.getStatus(), Status.INACTIVE.getValue())){
            return ocrAndFaceCheckResponse;
        }

        //B3: Cache thông tin vào redis
        cacheOcrDataToRedis(ocrAndFaceCheckResponse, passportUrl, portraitUrl);

        return ocrAndFaceCheckResponse;
    }

    private String buildBase64Image(MultipartFile file){
        try {
            byte[] fileContent = file.getBytes();
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: Cannot convert image to Base64");
            throw BaseException.badRequest(ErrorCode.CONVERT_IMAGE_TO_BASE64_FAIL)
                .message("Không thể mã hỏa ảnh thành Base64")
                .build();
        }
    }

    private String buildBase64Image(Resource file){
        try {
            byte[] fileContent = file.getContentAsByteArray();
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: Cannot convert Resource image to Base64");
            throw BaseException.badRequest(ErrorCode.CONVERT_IMAGE_TO_BASE64_FAIL)
                .message("Không thể mã hỏa ảnh thành Base64")
                .build();
        }
    }

    private OcrAndFaceCheckResponse initOcrResponse(String serial){
        return OcrAndFaceCheckResponse.builder()
            .status(Status.INACTIVE.getValue())
            .failedStep(OcrFailedStep.OCR.getStep())
            .serial(serial)
            .build();
    }

    private String processOcrAndGetUrl(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String passportBase64, MultipartFile passportFile){
        //Build request
        OcrPassportRequest ocrRequst = OcrPassportRequest.builder()
            .cardType(CARD_TYPE)
            .cardFront(passportBase64)
            .build();

        BaseIntegrationRequest ocrRequest = integrationPort.buildIntegrationRequest(VNSKY_EKYC_PASSPORT, OCR_TYPE, null, ocrRequst);
        OcrStepResponse ocrResponse = integrationPort.executeRequest(ocrRequest, OcrStepResponse.class);

        //Nếu Ocr thất bại, trả về luôn
        if(!Objects.equals(ocrResponse.getErrCode(), SUCCESS_CODE) ||
            !Objects.equals(Boolean.TRUE, ocrResponse.getDataOcr().getOcrPassport().getValidateDocument())){
                log.info("[ACTIVE_SUBSCRIBER]: ocr fail {}", ocrResponse);
                ocrAndFaceCheckResponse.setMessage(ocrResponse.getMessage());
                return null;
        }

        log.info("[ACTIVE_SUBSCRIBER]: ocr success fully {}", ocrResponse);
        ocrAndFaceCheckResponse.setIdEkyc(ocrResponse.getIdEkyc());
        ocrAndFaceCheckResponse.setStatus(Status.ACTIVE.getValue());
        ocrAndFaceCheckResponse.setOcrData(ocrResponse.getDataOcr().getOcrPassport().getInfors());

        //Upload ảnh hộ chiếu lên minio
        String url = uploadFileToMinio(passportFile, Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, ocrAndFaceCheckResponse.getTransactionId());
        log.info("[ACTIVE_SUBSCRIBER]: upload temp passport file successfully");
        return url;
    }

    private String processFaceCheckAndGetUrl(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String portraitBase64, MultipartFile portraitFile){
        //Đặt vị trí thất bại là FACE check
        ocrAndFaceCheckResponse.setFailedStep(OcrFailedStep.FACE_CHECK.getStep());

        //Build request
        FaceCheckRequest faceCheckRequest = FaceCheckRequest.builder()
            .idEkyc(ocrAndFaceCheckResponse.getIdEkyc())
            .anhThang(portraitBase64)
            .build();

        BaseIntegrationRequest faceCheckIntegrationRequest = integrationPort.buildIntegrationRequest(VNSKY_EKYC_PASSPORT, FACE_CHECK_TYPE, null, faceCheckRequest);
        FaceCheckResponse faceCheckResponse = integrationPort.executeRequest(faceCheckIntegrationRequest, FaceCheckResponse.class);

        //Nếu Ocr thất bại, trả về luôn
        if(!Objects.equals(faceCheckResponse.getErrCode(), SUCCESS_CODE)){
            log.info("[ACTIVE_SUBSCRIBER]: face check fail {}", faceCheckResponse);
            ocrAndFaceCheckResponse.setMessage(faceCheckResponse.getErrorMessages());
            ocrAndFaceCheckResponse.setStatus(Status.INACTIVE.getValue());
            return null;
        }else{
            log.info("[ACTIVE_SUBSCRIBER]: face check success fully {}", faceCheckResponse);
            ocrAndFaceCheckResponse.setStatus(Status.ACTIVE.getValue());
        }

        String url = uploadFileToMinio(portraitFile, Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, ocrAndFaceCheckResponse.getTransactionId());
        log.info("[ACTIVE_SUBSCRIBER]: upload temp portrait file successfully");
        return url;
    }

    /**
     *
     * @param ocrAndFaceCheckResponse: Thông tin từ có dược sau khi OCR và FACE check
     * @param passportUrl: Link minio của file tạm hộ chiếu
     * @param portraitUrl: Link minio của file tạo chân dung
     */
    private void cacheOcrDataToRedis(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String passportUrl, String portraitUrl){
        ActiveSubscriberDataDTO activeData = ActiveSubscriberDataDTO.builder()
            .ocrData(ocrAndFaceCheckResponse.getOcrData())
            .passportUrl(passportUrl)
            .portraitUrl(portraitUrl)
            .serial(ocrAndFaceCheckResponse.getSerial())
            .stepActive(ActiveSubscriberStep.VERIFIED_FACE.getValue())
            .transactionId(ocrAndFaceCheckResponse.getTransactionId())
            .build();

        cacheActiveSubscriberDataToRedis(activeData.getTransactionId(), activeData);
    }

    private String uploadFileToMinio(MultipartFile file, String folderPath, String serial){
        try {
            LocalDate today = LocalDate.now();
            UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                .uri(folderPath,
                    today.toString(),
                    serial,
                    file.getOriginalFilename())
                .isPublic(false)
                .build();

            minioOperations.upload(file.getInputStream(), uploadOptionDTO);
            log.info("[ACTIVE_SUBSCRIBER]: upload file successfully");
            return uploadOptionDTO.getUri();
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: Cannot upload file to minio", e);
            throw BaseException.badRequest(ErrorCode.UPLOAD_FILE_TO_MINIO_FAIL).build();
        }
    }

    private String uploadFileToMinio(InputStream inputStream, String folderPath, String fileName){
        UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
            .uri(folderPath,
                fileName)
            .isPublic(false)
            .build();

        minioOperations.upload(inputStream, uploadOptionDTO);
        log.info("[ACTIVE_SUBSCRIBER]: upload file successfully");
        return uploadOptionDTO.getUri();
    }

    private String buildActiveSubscriberTransactionKey(String transactionId){
        return ACTIVE_SUBSCRIBER_TRANSACTION_REDIS_KEY + transactionId;
    }

    @Override
    public GenContractActiveSubscriberResponse genActiveSubscriberContract(GenActiveSubscriberContractRequest request) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_3]: Received gen contract for transactionId {}", request.getTransactionId());

        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(request.getTransactionId());

        //Nếu trạng thái của giao dịch không phải chờ gen hợp đồng thì báo lỗi
        if(activeData.getStepActive() < ActiveSubscriberStep.VERIFIED_FACE.getValue()){
            log.error("[ACTIVE_SUBSCRIBER]: Giao dịch chưa xác thực khuôn mặt thành công {}", request.getTransactionId());
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_WAITING_GEN_CONTRACT).build();
        }

        //B2: Upload hợp đồng kích hoạt
        ContractFileUrlDTO activeContractUrl = uploadActiveSubscriberContract(activeData, request);
        activeData.setContractUrl(activeContractUrl);

        //B3: Cập nhật trạng thái giao dịch trong cáche
        cacheActiveSubscriberDataToRedis(request.getTransactionId(), activeData);

        return new GenContractActiveSubscriberResponse();
    }

    private ContractFileUrlDTO uploadActiveSubscriberContract(ActiveSubscriberDataDTO activeData, GenActiveSubscriberContractRequest genContractRequest){
        GenContractDTO genContractData = activeSubscriberMapper.mapGenContractDto(activeData);

        DownloadOptionDTO downloadOptionDTO = getDownloadActiveContractOption();

        Resource templateFile = getTemplateFile(downloadOptionDTO);
        try {
            ByteArrayOutputStream outPutStreamDegree = contractHandleServicePort.genContractFromTemplate(new ByteArrayInputStream(templateFile.getContentAsByteArray()), genContractData, TypeContract.PDF, ContractUtils.TypeHandlerWord.MAIL_MERGE, ContractUtils.TypeHandlerWord.CHECK_BOX);
            String pdf = uploadFileToMinio(new ByteArrayInputStream(outPutStreamDegree.toByteArray()), buildTempContractUrl(activeData.getTransactionId()), "BBXN." + TypeContract.PDF);

            return ContractFileUrlDTO.builder()
                .pdfUrl(pdf)
                .build();
        } catch (Docx4JException | IllegalAccessException | IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: không thể sinh hợp đồng cho giao dịch {}", genContractRequest.getTransactionId(), e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message(e.getMessage())
                .build();
        }
    }

    private Resource getTemplateFile(DownloadOptionDTO downloadOption){
        return minioOperations.download(downloadOption);
    }

    private ActiveSubscriberDataDTO getActiveDataFromRedis(String transactionId){
        String transactionKey = buildActiveSubscriberTransactionKey(transactionId);
        String activeSubscriberData = (String) redisStoreOperation.get(transactionKey);
        if(activeSubscriberData == null){
            log.error("[ACTIVE_SUBSCRIBER]: Cannot find active subscriber data with transactionId {} in redis", transactionId);
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_DATA_NOT_FOUND_IN_REDIS).build();
        }

        try {
            String decompressedJson = DataUtils.decompress(activeSubscriberData);
            return objectMapper.readValue(decompressedJson, ActiveSubscriberDataDTO.class);
        } catch (JsonProcessingException e) {
            log.error("[ACTIVE_SUBSCRIBER]: Cannot parse active subscriber data from redis with transactionId: {}", transactionId);
            throw BaseException.badRequest(ErrorCode.CAN_NOT_PARSE_ACTIVE_SUBSCRIBER_DATA_FROM_REDIS).build();
        }
    }

    @Override
    public Resource downloadActiveContract(String id) {
        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(id);

        DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
            .uri(activeData.getContractUrl().getPdfUrl())
            .isPublic(false)
            .build();
        return minioOperations.download(downloadOptionDTO);
    }

    @Override
    public void signContract(GenActiveSubscriberContractRequest request, MultipartFile signature) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_4]: Sign contract for transactionId {}", request.getTransactionId());

        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(request.getTransactionId());
        if(activeData.getStepActive() < ActiveSubscriberStep.VERIFIED_FACE.getValue()){
            log.error("[ACTIVE_SUBSCRIBER_STEP_4]: Trạng thái của giao dịch {} không phải là chờ ký hợp đồng", request.getTransactionId());
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_WAITING_SIGN_CONTRACT).build();
        }

        //Cài đặt chấp thuận nghị định 13
        activeData.setAgreeDegree13(request.getAgreeDegree13());

        //b1: Ký hợp đồng kich hoạt
        ContractFileUrlDTO signedActiveContractUrl = signActiveContract(activeData, signature);

        //b2: Ký biên bản xác nhận nghị định 13

        activeData.setStepActive(ActiveSubscriberStep.WAITING_SUBMIT.getValue());
        activeData.setContractUrl(signedActiveContractUrl);

        cacheActiveSubscriberDataToRedis(request.getTransactionId(), activeData);
    }

    private ContractFileUrlDTO signActiveContract(ActiveSubscriberDataDTO activeData, MultipartFile signature){
        GenContractDTO genContractData = activeSubscriberMapper.mapGenContractDto(activeData);

        DownloadOptionDTO downloadOptionDTO = getDownloadActiveContractOption();

        Resource templateFile = getTemplateFile(downloadOptionDTO);
        try {
            genContractData.setSignatureCustomer(signature.getBytes());

            byte[] templateData = IOUtils.toByteArray(templateFile.getInputStream());

            ByteArrayOutputStream outPutStreamContractPdf = contractHandleServicePort.genContractFromTemplate(new ByteArrayInputStream(templateData), genContractData, TypeContract.PDF, ContractUtils.TypeHandlerWord.MAIL_MERGE, ContractUtils.TypeHandlerWord.CHECK_BOX);

            ByteArrayOutputStream outPutStreamContractPng = contractHandleServicePort.convertPdfToPng(new ByteArrayInputStream(outPutStreamContractPdf.toByteArray()));


            String pdfUrl = uploadFileToMinio(new ByteArrayInputStream(outPutStreamContractPdf.toByteArray()), buildTempContractUrl(activeData.getTransactionId()), "BBXN." + TypeContract.PDF);
            String pngUrl = uploadFileToMinio(new ByteArrayInputStream(outPutStreamContractPng.toByteArray()), buildTempContractUrl(activeData.getTransactionId()), "BBXH." + TypeContract.PNG);

            return ContractFileUrlDTO.builder()
                .pdfUrl(pdfUrl)
                .pngUrl(pngUrl)
                .build();
        } catch (Docx4JException | IllegalAccessException | IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: không thể ký hợp đồng cho giao dịch");
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message(e.getMessage())
                .build();
        }
    }

    private DownloadOptionDTO getDownloadActiveContractOption(){
        return DownloadOptionDTO.builder()
            .uri(paramContractConfig.getTemplate().getContractSimActiveFolder(),
                paramContractConfig.getTemplate().getContractSimActiveFile())
            .isPublic(false)
            .build();
    }

    /**
     *
     * @param transactionId: Mã của giao dịch
     * @apiNote Hàm submit kích hoạt thuê bao
     */
    @Transactional
    @Override
    public void submitActiveSubscriber(String transactionId) {
        ActiveSubscriberDataDTO activeSubscriberData = getActiveDataFromRedis(transactionId);
        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: Submit active subscriber with transaction id {}", transactionId);

        if(!Objects.equals(activeSubscriberData.getStepActive(), ActiveSubscriberStep.WAITING_SUBMIT.getValue())){
            log.error("[ACTIVE_SUBSCRIBER_STEP_5]: Trạng thái của giao dịch {} không phải là chờ nộp đơn", transactionId);
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_WAITING_SUBMIT).build();
        }

        SubscriberDTO esimRegistration = checkSubscriberIsVerified(activeSubscriberData.getSerial());

        //B1: Map thông tin từ cache vào Esim Registration
        subscriberMapper.mapFromSubmitData(esimRegistration, activeSubscriberData);

        //B2: Lưu lại thông tin
        esimRegistration = subscriberServicePort.saveAndFlushNewTransaction(esimRegistration);

        //B32: Tải file ảnh hộ chiếu, chân dung, các file hợp đồng gửi sang mobifone
        List<List<String>> buildArrImagesForUpdateMbf = buildArrImagesForUpdateMbf(activeSubscriberData);

        //B3: Map thông tin kích hoạt gửi sang MBF
        ModifyInforParamsDTO params = getModifyInfoParamFromCache();
        UpdateSubscriberDataMbfRequest updateSubscriberDataMbf = subscriberMapper.mapUpdateMbfData(esimRegistration, buildArrImagesForUpdateMbf, params);

        //B4: Gọi sang MBF đồng bộ thông tin
        ActiveSubscriberService self = applicationContext.getBean(ActiveSubscriberService.class);
        self.processSendActiveDataToMbf(updateSubscriberDataMbf, activeSubscriberData);
    }



    /**
     *
     * @param updateSubscriberDataMbf: Thông tin đồng bộ sang Mbf
     * @param activeData: Dữ liệu chứa các file ảnh, hơp đồng tạm thời
     * @apiNote Xử lý gửi api cập nhật thông tin sang phía MBF, có retry tối đa 6 lần
     */
    @Override
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 100))
    public void processSendActiveDataToMbf(UpdateSubscriberDataMbfRequest updateSubscriberDataMbf, ActiveSubscriberDataDTO activeData) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: Get active result for serial {}, isdn {}, with {} times", updateSubscriberDataMbf.getStrSerial(), updateSubscriberDataMbf.getStrIsdn(), updateSubscriberDataMbf.getCountNumber());

        //Step 1: Gửi yêu cầu submit nghị định 13
        SubmitDecree13Response submitDecree13Response = submitAgreeDecree13(activeData.getAgreeDegree13(), updateSubscriberDataMbf.getStrIsdn());

        //Step 2: Gửi yêu cầu cập nhật thông tin
        log.info("[ACTIVE_SUBSCRIBER_STEP_5: Data for modify info MBF: {}", updateSubscriberDataMbf);

        BaseIntegrationRequest modifyInfoRequest = integrationPort.buildIntegrationRequest(MBF_CMD, UPDATE_SUBSCRIBER_INFO_TYPE, null, updateSubscriberDataMbf);
        ModifyInfoPrepaidV2Response modifyInfoResponse = integrationPort.executeRequest(modifyInfoRequest, ModifyInfoPrepaidV2Response.class);
        if(Objects.equals(modifyInfoResponse.getCode(), MODIFY_INFO_PREPAID_SUCCESS_CODE)){
            log.info("[ACTIVE_SUBSCRIBER_STEP_5]: Active subscriber successfully for isdn {}, serial {}, count retry {}", updateSubscriberDataMbf.getStrIsdn(), updateSubscriberDataMbf.getStrSerial(), updateSubscriberDataMbf.getCountNumber());

            //Cập nhật trạng thái thành đã đồng bộ thông tin thành công
            SubscriberDTO esimRegistration = subscriberRepoPort.findByLastIsdn(updateSubscriberDataMbf.getStrIsdn()).orElseThrow(() -> BaseException.notFoundError(ErrorCode.ISDN_NOT_FOUND).build());
            esimRegistration.setVerifiedStatus(EsimRegistrationStatus.ACTIVED.getValue());
            esimRegistration.setMbfSubId(submitDecree13Response.getData().get(0).getSubId());
            esimRegistration = subscriberServicePort.saveAndFlushNewTransaction(esimRegistration);

            //Chuyển thông tin hồ sơ từ th mục tạm -> thư mục chính thức
            try {
                uploadContractFilesToFinalFolder(activeData, esimRegistration);
            } catch (IOException e) {
                log.error("Upload file to final folder failed", e);
            }
            subscriberServicePort.saveAndFlushNewTransaction(esimRegistration);

            //Xóa các file trong folder tạm
            deleteFileByUrl(buildTempContractUrl(activeData.getTransactionId()));

            //Xóa dữ liệu cache giao dịch
            redisStoreOperation.release(buildActiveSubscriberTransactionKey(activeData.getTransactionId()));
        }else{
            throw BaseException.badRequest(ErrorCode.ATTEMPT_GET_ACTIVE_RESULT_EXCEED_MAX_TIMES)
                .message(modifyInfoResponse.getDescription())
                .addProblemDetail(ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), modifyInfoResponse.getDescription()))
                .build();
        }
    }

    /**
     * @param agreeDecree13: Lưu thông tin chấp thuận nghị định 13
     * @apiNote : Hàm này gửi yêu cầu chấp thuận nghị định 13 của khách hàng sang MBF
     */
    private SubmitDecree13Response submitAgreeDecree13(AgreeDegree13DTO agreeDecree13, Long isdn){
        // "CT01:1;CT02:1;CT03:1;CT04:1;CT05:1;CT06:1;"
        String acceptedValue = "CT01:" + buildAcceptedValueDecree13(agreeDecree13.isAgreeDk1())
                            + ",CT02:" + buildAcceptedValueDecree13(agreeDecree13.isAgreeDk2())
                            + ",CT03:" + buildAcceptedValueDecree13(agreeDecree13.isAgreeDk3())
                            + ",CT04:" + buildAcceptedValueDecree13(agreeDecree13.isAgreeDk4())
                            + ",CT05:" + buildAcceptedValueDecree13(agreeDecree13.isAgreeDk5())
                            + ",CT06:" + buildAcceptedValueDecree13(agreeDecree13.isAgreeDk6());

        LocalDateTime now = LocalDateTime.now();

        SubmitDecree13Request submitRequest = SubmitDecree13Request.builder()
            .isdn(isdn)
            .channelCode(hiVnChannelCode)
            .acceptedValue(acceptedValue)
            .acceptedDateTime(now.format(Constant.TIME_STAMP_FE_DATE_FORMATTER))
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(MBF_CMD, DECREE13_TYPE, null, submitRequest);
        SubmitDecree13Response response = integrationPort.executeRequest(integrationRequest, SubmitDecree13Response.class);

        if(!Objects.equals(response.getCode(), SUBMIT_DECREE13_SUCCESS_CODE)){
            log.error("[ACTIVE_SUBSCRIBER_STEP_5]: Submit decree 13 fail for isdn {} by {}", isdn, response.getDescription());
            throw BaseException.badRequest(ErrorCode.SUBMIT_DECREE13_FAIL)
                .message("Lỗi khi xác nhận nghị định 13 do "+ response.getDescription())
                .addParameter(MESSAGE, response.getDescription())
                .build();
        }else{
            log.info("[ACTIVE_SUBSCRIBER_STEP_5]: Submit decree 13 successfully for isdn {}", isdn);
        }

        return response;
    }

    private String buildAcceptedValueDecree13(Boolean agree){
        return Objects.equals(Boolean.TRUE, agree) ? "1" : "0";
    }

    /**
     *
     * @param activeData: Dữ liệu ản, hợp đồng tạm thời
     * @throws IOException: Ném ra exception khi đọc dữ liệu từ file
     * @apiNote Đẩy các ảnh, file hợp đồng từ folder temp sang folder chính thức
     */
    private void uploadContractFilesToFinalFolder(ActiveSubscriberDataDTO activeData, SubscriberDTO subscriber) throws IOException {
        String urlFolder = buildFinalContractUrl(activeData.getTransactionId());

        //File ảnh hộ chiếu
        Resource passportImg = downloadFile(activeData.getPassportUrl());
        subscriber.setPassportUrl(uploadFileToMinio(new ByteArrayInputStream(passportImg.getContentAsByteArray()),
            urlFolder,
            passportImg.getFilename()));

        //File ảnh chân dung
        Resource portraitImg = downloadFile(activeData.getPortraitUrl());
        subscriber.setPortraitUrl(uploadFileToMinio(new ByteArrayInputStream(portraitImg.getContentAsByteArray()),
            urlFolder,
            portraitImg.getFilename()));

        //File hợp đồng kích hoạt PDF
        Resource activeContractPdfFile = downloadFile(activeData.getContractUrl().getPdfUrl());
        subscriber.setContractPdfUrl(uploadFileToMinio(new ByteArrayInputStream(activeContractPdfFile.getContentAsByteArray()),
            urlFolder,
            activeContractPdfFile.getFilename()));

        //File hợp dồng kích hoạt PNG
        Resource activeContractPng = downloadFile(activeData.getContractUrl().getPngUrl());
        subscriber.setContractPngUrl(uploadFileToMinio(new ByteArrayInputStream(activeContractPng.getContentAsByteArray()),
            urlFolder,
            activeContractPng.getFilename()));

        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: Upload all files to folder {}", urlFolder);
    }

    @Override
    public List<ApplicationConfigDTO> getDegree13(HttpHeaders headers) {
        String language = !headers.getAcceptLanguage().isEmpty() ? headers.getAcceptLanguage().get(0).toString() : DEFAULT_LANGUAGE;
        return applicationConfigRepoPort.getByTypeAndLanguage(DEGREE_13_CONFIG_TYPE, language);
    }

    private Resource downloadFile(String url) {
        DownloadOptionDTO downloadOption = DownloadOptionDTO.builder()
            .uri(url)
            .isPublic(false)
            .build();

        return minioOperations.download(downloadOption);
    }

    /**
     *
     * @param activeData: Lưu tạm thời ảnh và hợp đồng
     * @return Chuỗi String gồm mã hóa các ảnh, hợp đồng để gửi sang MBF
     */
    private List<List<String>> buildArrImagesForUpdateMbf(ActiveSubscriberDataDTO activeData){
        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: download files for active subscriber with transactionId: {}", activeData.getTransactionId());

        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: download pass");
        Resource passportFile = downloadFile(activeData.getPassportUrl());

        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: download portrait");
        Resource portraitFile = downloadFile(activeData.getPortraitUrl());

        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: download active contract");
        Resource activeContractPdfFile = downloadFile(activeData.getContractUrl().getPdfUrl());

        Resource activeContractPngFile = downloadFile(activeData.getContractUrl().getPngUrl());

        //Build tham số hình ảnh hộ chiếu
        List<String> passportArr = List.of(StringUtils.hasText(passportFile.getFilename()) ? passportFile.getFilename() : DEFAULT_PASS_PORT_FILE_NAME, buildBase64Image(passportFile), ARR_IMAGES_IMAGE_TYPE);

        //Build tham số hình ảnh chân dung
        List<String> portraitArr = List.of(StringUtils.hasText(portraitFile.getFilename()) ? portraitFile.getFilename() : DEFAULT_PORTRAIT_FILE_NAME, buildBase64Image(portraitFile), ARR_IMAGES_PORTRAIT_TYPE);

        //Build tham số file hợp đồng
        List<String> activeContractArr = List.of(StringUtils.hasText(activeContractPdfFile.getFilename()) ? activeContractPdfFile.getFilename() : DEFAULT_ACTIVE_CONTRACT_FILE_NAME, buildBase64Image(activeContractPngFile), ARR_IMAGES_CONTRACT_TYPE);

        return Arrays.asList(passportArr, portraitArr, activeContractArr);
    }

    private void deleteFileByUrl(String url){
        DeleteOptionDTO deleteOption = DeleteOptionDTO.builder()
            .uri(url)
            .isPublic(false)
            .build();
        minioOperations.remove(deleteOption);
    }

    private void cacheActiveSubscriberDataToRedis(String transactionId, ActiveSubscriberDataDTO activeData){
        try {
            String transactionKey = buildActiveSubscriberTransactionKey(transactionId);
            String compressedJson = DataUtils.compress(objectMapper.writeValueAsString(activeData));
            redisStoreOperation.putWithExpireTime(transactionKey, compressedJson,  1, TimeUnit.DAYS);
            log.info("[ACTIVE_SUBSCRIBER]: cache active subscriber data to redis successfully");
        } catch (JsonProcessingException e) {
            log.error("[ACTIVE_SUBSCRIBER]: Cannot write object to json string", e);
            throw BaseException.badRequest(ErrorCode.CAN_NOT_CACHE_ACTIVE_DATA).build();
        }
    }

    private ModifyInforParamsDTO cacheModifyInfoParam(){
        String strSubType = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, ARR_SUB_TYPE).getName();
        String strCustType = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, CUST_TYPE).getName();
        String strReasonCode = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_REASON_CODE).getName();
        String strActionFlag = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_ACTION_FLAG).getName();
        String strAppObject = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_APP_OBJECT).getName();

        ModifyInforParamsDTO params = ModifyInforParamsDTO.builder()
            .strSubType(strSubType)
            .strCustType(strCustType)
            .strReasonCode(strReasonCode)
            .strActionFlag(strActionFlag)
            .strAppObject(strAppObject)
            .build();

        redisStoreOperation.putWithExpireTime(MODIFY_INFO_PARAM_CACHE_KEY, params, 30, TimeUnit.MINUTES);
        return params;
    }

    private ModifyInforParamsDTO getModifyInfoParamFromCache(){
        ModifyInforParamsDTO params = (ModifyInforParamsDTO) redisStoreOperation.get(MODIFY_INFO_PARAM_CACHE_KEY);
        if(params == null){
            return cacheModifyInfoParam();
        }
        return params;
    }

    /**
     * Kiểm tra subscriber có được phép kích hoạt hay không
     *
     * @param serial: Kiểm tra theo serial
     */
    private SubscriberDTO checkSubscriberIsVerified(String serial){
        SubscriberDTO subscriber = subscriberRepoPort.findBySerialLastSerial(serial).orElseThrow(() -> BaseException.badRequest(ErrorCode.ISDN_NOT_FOUND).build());
        checkSubscriberIsVerified(subscriber);
        return subscriber;
    }

    /**
     * Kiểm tra subscriber có được phép kích hoạt hay không
     *
     * @param isdn: Kiểm tra theo isdn
     */
    private SubscriberDTO checkSubscriberIsVerified(Long isdn){
        SubscriberDTO subscriber = subscriberRepoPort.findByLastIsdn(isdn).orElseThrow(() -> BaseException.badRequest(ErrorCode.ISDN_NOT_FOUND).build());
        checkSubscriberIsVerified(subscriber);
        return subscriber;
    }

    /**
     * Kiểm tra subscriber có được phép kích hoạt hay không
     *
     * @param subscriber: Số thuê bao cần kiểm tra
     */
    private SubscriberDTO checkSubscriberIsVerified(SubscriberDTO subscriber){
        if(Objects.equals(subscriber.getStatus(), Status.INACTIVE.getValue())){
            log.error("Subscriber: {} đã bị cắt hủy", subscriber);
            throw BaseException.badRequest(ErrorCode.SUBSCRIBER_IS_INACTIVE).build();
        }

        if(!Objects.equals(subscriber.getStatusCall900(), Call900Status.CALLED.getValue())){
            log.error("Subscriber: {} chưa gọi 900", subscriber);
            throw BaseException.badRequest(ErrorCode.SUBSCRIBER_NOT_CALL_900).build();
        }

        if(Objects.equals(subscriber.getVerifiedStatus(), Status.ACTIVE.getValue())){
            log.error("Subscriber: {} đã đăng ký thông tin", subscriber);
            throw BaseException.badRequest(ErrorCode.SUBSCRIBER_IS_VERIFIED).build();
        }

        return subscriber;
    }

    private String buildFinalContractUrl(String transactionId){
        return paramContractConfig.getContract().getSimActive().getFinalContractFolder() + Constant.CommonSymbol.FORWARD_SLASH + transactionId;
    }

    private String buildTempContractUrl(String transactionId){
        LocalDate today = LocalDate.now();
        return Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER
            + Constant.CommonSymbol.FORWARD_SLASH // /
            + today
            + Constant.CommonSymbol.FORWARD_SLASH // /
            + transactionId;
    }
}

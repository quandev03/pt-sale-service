package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.config.ParamContractConfig;
import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.mapper.SubscriberMapper;
import com.vnsky.bcss.projectbase.domain.port.primary.*;
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
import com.vnsky.bcss.projectbase.shared.utils.MessageSourceUtils;
import com.vnsky.bcss.projectbase.shared.utils.PdfContractUtils;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.DeleteOptionDTO;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.redis.component.RedisStoreOperation;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
import java.util.stream.Collectors;
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
    private final ApplicationContext applicationContext;
    private final TaskExecutor taskExecutor;
    private final ActionHistoryServicePort actionHistoryServicePort;
    private final SubscriberServicePort subscriberServicePort;
    private final GenContractServicePort genContractServicePort;

    private static final String ACTIVE_SUBSCRIBER_TRANSACTION_REDIS_KEY = "ACTIVE_SUBSCRIBER_TRANSACTION::";
    private static final String VNSKY_EKYC_PASSPORT = "VNSKY_EKYC_PASSPORT";
    private static final String OCR_TYPE = "OCR";
    private static final String FACE_CHECK_TYPE = "FACE_CHECK";
    private static final Integer CARD_TYPE = 2;
    private static final String SUCCESS_CODE = "00";
    private static final String ARR_IMAGES_IMAGE_TYPE = "0";
    private static final String ARR_IMAGES_PORTRAIT_TYPE = "1";
    private static final String ARR_IMAGES_CONTRACT_TYPE = "2";
    private static final String ARR_IMAGES_AGREE_13_TYPE = "7";
    private static final String DEFAULT_PASS_PORT_FILE_NAME = "Hộ chiếu.jpg";
    private static final String DEFAULT_PORTRAIT_FILE_NAME = "Chân dung.jpg";
    private static final String DEFAULT_ACTIVE_CONTRACT_FILE_NAME = "BBXN.png";
    private static final String DEFAULT_AGREE_13_FILE_NAME = "Đồng ý nghị định 13.png";
    private static final String DEGREE_13_CONFIG_TYPE = "SUB_DOCUMENT_ND13";
    private static final String DEFAULT_LANGUAGE = "vi-vn";
    private static final String MBF_CMD = "MBF";
    private static final String UPDATE_SUBSCRIBER_INFO_TYPE = "MODIFY_INFO";
    private static final String DECREE13_TYPE = "DECREE13";
    private static final String MODIFY_INFO_PREPAID_SUCCESS_CODE = "0000";
    private static final String SUBMIT_DECREE13_SUCCESS_CODE = "SUCCESS";
    private static final String MESSAGE = "message";
    private static final String VIETTEL_PREFIX = "84";
    private static final String UNKNOW_ISSUE_PLACE = "NA";

    //Tham số tuyền sang api cập nhật thông tin MBF
    private static final String MODIFY_INFOR_TABLE_NAME = "MODIFY_INFO_MBF";
    private static final String ARR_SUB_TYPE = "ARR_SUB_TYPE";
    private static final String CUST_TYPE = "CUST_TYPE";
    private static final String STR_REASON_CODE = "STR_REASON_CODE";
    private static final String STR_ACTION_FLAG = "STR_ACTION_FLAG";
    private static final String STR_APP_OBJECT = "STR_APP_OBJECT";
    private static final String STR_PROVINCE = "STR_PROVINCE";
    private static final String STR_DISTRICT = "STR_DISTRICT";
    private static final String STR_PRECINCT = "STR_PRECINCT";
    private static final String STR_NATIONALITY = "STR_NATIONALITY";
    private static final String STR_SUB_NAME = "STR_SUB_NAME";
    private static final String STR_SEX = "STR_SEX";
    private static final String STR_BIRTHDAY = "STR_BIRTHDAY";
    private static final String STR_PASSPORT = "STR_PASSPORT";
    private static final String STR_PASSPORT_ISSUE_DATE = "STR_PASSPORT_ISSUE_DATE";
    private static final String STR_PASSPORT_ISSUE_PLACE = "STR_PASSPORT_ISSUE_PLACE";
    private static final String MODIFY_INFO_PARAM_CACHE_KEY = "MODIFY_INFO_PARAM_CACHE_KEY";

    //Cấu hình danh sách quốc gia
    private static final String COUNTRY_CODE_TYPE = "COUNTRY_CODES";

    //Danh sách mã code quốc gia được cấu hình trong file excel
    private static final String COUNTRY_CODES_REDIS_KEY = "::COUNTRY_CODE::";

    private static final String SIGNATURE_FILE_NAME = "signature.jpg";
    private static final String PASSPORT_FILE_NAME = "passport.jpg";
    private static final String PORTRAIT_FILE_NAME = "portrait.jpg";
    private static final String AGREE_13_FILE_NAME = "Đồng ý nghị định 13.";
    private static final String ACTIVE_CONTRACT_FILE_NAME = "BBXN.";

    private static final String INTERNAL_CLIENT_ID = "000000000000";
    private static final String SYSTEM = "SYSTEM";


    @Override
    public String checkIsdn(Long isdn) {
        isdn = formatIsdn(isdn);
        log.info("[ACTIVE_SUBSCRIBER_STEP_1]: Checking isdn {}", isdn);
        SubscriberDTO esimRegistration = checkSubscriberIsNotVerified(isdn);
        return esimRegistration.getSerial();
    }

    @Override
    public OcrAndFaceCheckResponse ocrPassport(MultipartFile passport, String serial) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_2]: Start ocr for serial {}", serial);
        String passportBase64 = buildBase64Image(passport);

        //B0: Kiểm tra trạng thái serial sim
        SubscriberDTO eSim = checkSubscriberIsNotVerified(serial);

        //B0: Cài đặt transactionId
        OcrAndFaceCheckResponse ocrAndFaceCheckResponse = initOcrResponse(serial);
        String transactionId = UUID.randomUUID().toString();
        ocrAndFaceCheckResponse.setTransactionId(transactionId);

        //B1: Tiến hành OCR
        String passportUrl = processOcrAndGetUrl(ocrAndFaceCheckResponse, passportBase64, passport);

        //Nếu verify Hộ chiếu thành công
        if(Objects.equals(ocrAndFaceCheckResponse.getStatus(), Status.ACTIVE.getValue())){
            ActiveSubscriberDataDTO activeData = buildActiveDataAtOcrStep(ocrAndFaceCheckResponse, passportUrl, eSim.getIsdn());

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
    public ActiveSubscriberDataDTO buildActiveDataAtOcrStep(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String passportUrl, Long isdn){
        return ActiveSubscriberDataDTO.builder()
            .ocrData(ocrAndFaceCheckResponse.getOcrData())
            .passportUrl(passportUrl)
            .serial(ocrAndFaceCheckResponse.getSerial())
            .stepActive(ActiveSubscriberStep.VERIFIED_OCR.getValue())
            .transactionId(ocrAndFaceCheckResponse.getTransactionId())
            .idEkyc(ocrAndFaceCheckResponse.getIdEkyc())
            .isdn(isdn)
            .userId(SecurityUtil.getCurrentUserId())
            .employeeFullName(SecurityUtil.getCurrentFullName())
            .build();
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
        ocrAndFaceCheckResponse.setIsdn(activeData.getIsdn());
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
        SubscriberDTO subscriber = checkSubscriberIsNotVerified(serial);

        //B0: Cài đặt transactionId
        String transactionId = UUID.randomUUID().toString();
        ocrAndFaceCheckResponse.setTransactionId(transactionId);
        ocrAndFaceCheckResponse.setIsdn(subscriber.getIsdn());

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

    @Override
    public String buildBase64Image(MultipartFile file){
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

    @Override
    public OcrAndFaceCheckResponse initOcrResponse(String serial){
        return OcrAndFaceCheckResponse.builder()
            .status(Status.INACTIVE.getValue())
            .failedStep(OcrFailedStep.OCR.getStep())
            .serial(serial)
            .build();
    }

    @Override
    public String processOcrAndGetUrl(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String passportBase64, MultipartFile passportFile){
        //Build request
        OcrPassportRequest ocrRequst = OcrPassportRequest.builder()
            .cardType(CARD_TYPE)
            .cardFront(passportBase64)
            .build();

        if(sendOcrRequest(ocrRequst, ocrAndFaceCheckResponse)){
            //Upload ảnh hộ chiếu lên minio
            String url = uploadFileToMinio(passportFile, Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, ocrAndFaceCheckResponse.getTransactionId(), PASSPORT_FILE_NAME);
            log.info("[ACTIVE_SUBSCRIBER]: upload temp passport file successfully");
            return url;
        }
        return null;
    }

    @Override
    public boolean sendOcrRequest(OcrPassportRequest ocrRequst, OcrAndFaceCheckResponse ocrAndFaceCheckResponse){
        BaseIntegrationRequest ocrRequest = integrationPort.buildIntegrationRequest(VNSKY_EKYC_PASSPORT, OCR_TYPE, null, ocrRequst);
        OcrStepResponse ocrResponse = integrationPort.executeRequest(ocrRequest, OcrStepResponse.class);

        //Nếu Ocr thất bại, trả về luôn
        if(!Objects.equals(ocrResponse.getErrCode(), SUCCESS_CODE)){
            log.info("[ACTIVE_SUBSCRIBER]: ocr fail {}", ocrResponse);
            ocrAndFaceCheckResponse.setMessage(ocrResponse.getMessage());
            return false;
        }

        if(!Objects.equals(Boolean.TRUE, ocrResponse.getDataOcr().getOcrPassport().getValidateDocument())){
            log.info("[ACTIVE_SUBSCRIBER]: ocr fail due to invalid document {}", ocrResponse);
            String message = MessageSourceUtils.getMessageDetail(ErrorCode.PASSPORT_NOT_VALID.get());
            ocrAndFaceCheckResponse.setMessage(message);
            return false;
        }

        log.info("[ACTIVE_SUBSCRIBER]: ocr success fully {}", ocrResponse);
        ocrAndFaceCheckResponse.setIdEkyc(ocrResponse.getIdEkyc());
        ocrAndFaceCheckResponse.setStatus(Status.ACTIVE.getValue());
        ocrAndFaceCheckResponse.setOcrData(ocrResponse.getDataOcr().getOcrPassport().getInfors());

        if(Objects.equals(ocrResponse.getDataOcr().getOcrPassport().getInfors().getIssuedPlace(), UNKNOW_ISSUE_PLACE)){
            ocrResponse.getDataOcr().getOcrPassport().getInfors().setIssuedPlace(null);
        }

        return true;
    }

    @Override
    public String processFaceCheckAndGetUrl(OcrAndFaceCheckResponse ocrAndFaceCheckResponse, String portraitBase64, MultipartFile portraitFile){
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
            ocrAndFaceCheckResponse.setMessage(faceCheckResponse.getMessage());
            ocrAndFaceCheckResponse.setStatus(Status.INACTIVE.getValue());
            return null;
        }else{
            log.info("[ACTIVE_SUBSCRIBER]: face check success fully {}", faceCheckResponse);
            ocrAndFaceCheckResponse.setStatus(Status.ACTIVE.getValue());
        }

        String url = uploadFileToMinio(portraitFile, Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, ocrAndFaceCheckResponse.getTransactionId(), PORTRAIT_FILE_NAME);
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
            .isdn(ocrAndFaceCheckResponse.getIsdn())
            .build();

        cacheActiveSubscriberDataToRedis(activeData.getTransactionId(), activeData);
    }

    @Override
    public String uploadFileToMinio(byte[] bytes, String folderPath, String fileName){
        LocalDate today = LocalDate.now();
        UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
            .uri(folderPath,
                today.getYear() + "-" + today.getMonthValue(),
                fileName)
            .isPublic(false)
            .build();

        minioOperations.upload(new ByteArrayInputStream(bytes), uploadOptionDTO);
        log.info("[ACTIVE_SUBSCRIBER]: upload file successfully to {} with {} bytes",uploadOptionDTO.getUri(), bytes.length);
        return uploadOptionDTO.getUri();
    }

    @Override
    public String uploadFileToMinio(MultipartFile file, String folderPath, String transactionId, String fileName){
        try {
            LocalDate today = LocalDate.now();
            UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                .uri(folderPath,
                    today.getYear() + "-" + today.getMonthValue(),
                    transactionId,
                    fileName == null ? file.getOriginalFilename() : fileName)
                .isPublic(false)
                .build();

            minioOperations.upload(file.getInputStream(), uploadOptionDTO);
            log.info("[ACTIVE_SUBSCRIBER]: upload file successfully to {}",uploadOptionDTO.getUri());
            return uploadOptionDTO.getUri();
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: Cannot upload file to minio", e);
            throw BaseException.badRequest(ErrorCode.UPLOAD_FILE_TO_MINIO_FAIL).build();
        }
    }

    @Override
    public String uploadFileToMinio(InputStream inputStream, String folderPath, String fileName){
        UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
            .uri(folderPath,
                fileName)
            .isPublic(false)
            .build();

        minioOperations.upload(inputStream, uploadOptionDTO);
        log.info("[ACTIVE_SUBSCRIBER]: upload file successfully to {}", uploadOptionDTO.getUri());
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

        //Lấy thông tin customerCode và contractCode
        SubscriberDTO subscriber = checkSubscriberIsNotVerified(activeData.getSerial());
        genContractServicePort.genCustomerCode(activeData, subscriber);

        //B2: Upload hợp đồng kích hoạt
        String activeContractUrl = uploadActiveSubscriberContract(activeData, request);
        activeData.setContractPngUrl(activeContractUrl);

        //B3: Cập nhật trạng thái giao dịch trong cáche
        cacheActiveSubscriberDataToRedis(request.getTransactionId(), activeData);

        return new GenContractActiveSubscriberResponse();
    }

    private String uploadActiveSubscriberContract(ActiveSubscriberDataDTO activeData, GenActiveSubscriberContractRequest genContractRequest){
        GenContractDTO genContractData = activeSubscriberMapper.mapGenContractDto(activeData);
        // Lấy thông tin chữ ký người thực hiện
        genContractData.setSignatureCskh(getCskhSignature(activeData.getUserId(), activeData.getTransactionId()));
        genContractData.setEmployeeName(activeData.getEmployeeFullName());

        DownloadOptionDTO downloadOptionDTO = getDownloadActiveContractOption();

        Resource templateFile = getTemplateFile(downloadOptionDTO);
        try {
            ByteArrayOutputStream outPutStreamDegree = contractHandleServicePort.genContractFromTemplate(new ByteArrayInputStream(templateFile.getContentAsByteArray()), genContractData, TypeContract.PDF, ContractUtils.TypeHandlerWord.MAIL_MERGE, ContractUtils.TypeHandlerWord.CHECK_BOX);
            return uploadFileToMinio(new ByteArrayInputStream(outPutStreamDegree.toByteArray()), buildTempContractUrl(activeData.getTransactionId()), ACTIVE_CONTRACT_FILE_NAME + TypeContract.PDF);
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
            log.error("[ACTIVE_SUBSCRIBER]: Cannot parse active subscriber data from redis with transactionId: {}", transactionId, e);
            throw BaseException.badRequest(ErrorCode.CAN_NOT_PARSE_ACTIVE_SUBSCRIBER_DATA_FROM_REDIS).build();
        }
    }

    @Override
    public Resource downloadActiveContract(String id) {
        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(id);

        DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
            .uri(activeData.getContractPngUrl())
            .isPublic(false)
            .build();
        return minioOperations.download(downloadOptionDTO);
    }

    @Transactional
    @Override
    public void signContract(GenActiveSubscriberContractRequest request, MultipartFile signature) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_4]: Sign contract for transactionId {}", request.getTransactionId());

        ActiveSubscriberDataDTO activeData = getActiveDataFromRedis(request.getTransactionId());
        if(activeData.getStepActive() < ActiveSubscriberStep.VERIFIED_FACE.getValue()){
            log.error("[ACTIVE_SUBSCRIBER_STEP_4]: Trạng thái của giao dịch {} không phải là chờ ký hợp đồng", request.getTransactionId());
            throw BaseException.badRequest(ErrorCode.ACTIVE_SUBSCRIBER_TRANSACTION_STEP_IS_NOT_WAITING_SIGN_CONTRACT).build();
        }

        //Lấy thông tin customerCode và contractCode
        SubscriberDTO subscriber = checkSubscriberIsNotVerified(activeData.getSerial());
        genContractServicePort.genCustomerCode(activeData, subscriber);

        //Cài đặt chấp thuận nghị định 13
        activeData.setAgreeDegree13(request.getAgreeDegree13());

        signContractAndUploadSignature(activeData, signature, request.getTransactionId());
    }

    /**
     *
     * @param activeData: Dữ liệu kích hoạt thuê bao
     * @param signature: File chữ ký
     * @param transactionId: Id của giao dịch
     * @apiNote : Hàm này thực hiện ký hợp đồng và upload ảnh chữ ký lên minio
     */
    @Override
    public void signContractAndUploadSignature(ActiveSubscriberDataDTO activeData, MultipartFile signature, String transactionId){
        try {
            byte[] bytes = signature.getBytes();

            ContractFileUrlDTO contractUrl = signActiveContractAndGetUrl(activeData, bytes, TypeContract.PNG, false);
            String signatureUrl = uploadFileToMinio(bytes, Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, transactionId + "/" + SIGNATURE_FILE_NAME);

            activeData.setStepActive(ActiveSubscriberStep.WAITING_SUBMIT.getValue());
            activeData.setContractPngUrl(contractUrl.getPngUrl());
            activeData.setContractPdfUrl(contractUrl.getPdfUrl());
            activeData.setSignatureUrl(signatureUrl);

            genDecree13(activeData, bytes);

            cacheActiveSubscriberDataToRedis(transactionId, activeData);
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER_STEP_4]: Không thể ký hợp đồng cho giao dịch {}", transactionId, e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL).message(e.getMessage()).build();
        }
    }

    @Override
    public ContractFileUrlDTO signActiveContractAndGetUrl(ActiveSubscriberDataDTO activeData, byte[] bytes, TypeContract typeContract, boolean isFinal){
        GenContractDTO genContractData = activeSubscriberMapper.mapGenContractDto(activeData);
        // Lấy thông tin chữ ký người thực hiện
        genContractData.setSignatureCskh(getCskhSignature(activeData.getUserId(), activeData.getTransactionId()));

        DownloadOptionDTO downloadOptionDTO = getDownloadActiveContractOption();

        Resource templateFile = getTemplateFile(downloadOptionDTO);
        try {
            genContractData.setSignatureCustomer(bytes);

            byte[] templateData = IOUtils.toByteArray(templateFile.getInputStream());

            ByteArrayOutputStream outPutStreamContractPdf = PdfContractUtils.fillDataToPdf(templateData, genContractData, GenContractDTO.class);
            ByteArrayOutputStream outputStreamContractPng = PdfContractUtils.convertPdfToOneJpgFile(outPutStreamContractPdf.toByteArray(), false);
            String dirUrl = isFinal ? buildFinalContractUrl(activeData.getTransactionId()) : buildTempContractUrl(activeData.getTransactionId());

            String contractPdfUrl = uploadFileToMinio(new ByteArrayInputStream(outPutStreamContractPdf.toByteArray()), dirUrl, ACTIVE_CONTRACT_FILE_NAME + TypeContract.PDF.name());
            String contractPngUrl = uploadFileToMinio(new ByteArrayInputStream(outputStreamContractPng.toByteArray()), dirUrl, ACTIVE_CONTRACT_FILE_NAME + TypeContract.PNG.name());

            return ContractFileUrlDTO.builder()
                .pdfUrl(contractPdfUrl)
                .pngUrl(contractPngUrl)
                .build();
        } catch (IOException e) {
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

    private DownloadOptionDTO getDownloadDecree13ContractOption(){
        return DownloadOptionDTO.builder()
            .uri(paramContractConfig.getTemplate().getContractSimActiveFolder(),
                paramContractConfig.getTemplate().getDecree13confirmationRecord())
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

        //Gen lại ảnh hợp đồng mờ đi
        try {
            Resource pdfFile = downloadFile(activeSubscriberData.getContractPdfUrl());
            ByteArrayOutputStream finalContractImg = PdfContractUtils.convertPdfToOneJpgFile(pdfFile.getContentAsByteArray(), true);
            String finalContractUrl = uploadFileToMinio(finalContractImg.toByteArray(), Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, transactionId + "/" + ACTIVE_CONTRACT_FILE_NAME + TypeContract.PNG);
            activeSubscriberData.setContractPngUrl(finalContractUrl);
        } catch (Exception e) {
            log.error("Exception when generate final contract png", e);
            throw BaseException.badRequest(ErrorCode.ATTEMPT_GET_ACTIVE_RESULT_EXCEED_MAX_TIMES)
                .addProblemDetail(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage()))
                .build();
        }

        SubscriberDTO esimRegistration = checkSubscriberIsNotVerified(activeSubscriberData.getSerial());

        //B1: Map thông tin từ cache vào Esim Registration
        subscriberMapper.mapFromSubmitData(esimRegistration, activeSubscriberData);

        //B2: Lưu lại thông tin
        subscriberServicePort.saveAndFlushNewTransaction(esimRegistration);

        //B32: Tải file ảnh hộ chiếu, chân dung, các file hợp đồng gửi sang mobifone
        List<List<String>> buildArrImagesForUpdateMbf = buildArrImagesForUpdateMbf(activeSubscriberData);

        //B3: Map thông tin kích hoạt gửi sang MBF
        ModifyInforParamsDTO params = getModifyInfoParamFromCache();
        UpdateSubscriberDataMbfRequest updateSubscriberDataMbf = subscriberMapper.mapUpdateMbfData(esimRegistration, activeSubscriberData, buildArrImagesForUpdateMbf, params);

        //B4: Gọi sang MBF đồng bộ thông tin
        ActiveSubscriberService self = applicationContext.getBean(ActiveSubscriberService.class);
        self.processSendActiveDataToMbf(updateSubscriberDataMbf, activeSubscriberData, true);

        //B5: Lưu lịch sử tác động thuê bao
        actionHistoryServicePort.save(ActionHistoryActionCode.UPDATE_INFO.getCode(), esimRegistration.getId());
    }

    /**
     *
     * @param updateSubscriberDataMbf: Thông tin đồng bộ sang Mbf
     * @param activeData: Dữ liệu chứa các file ảnh, hơp đồng tạm thời
     * @apiNote Xử lý gửi api cập nhật thông tin sang phía MBF, có retry tối đa 5 lần
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    @Retryable(retryFor = Exception.class, maxAttempts = 2, backoff = @Backoff(delay = 100))
    public void processSendActiveDataToMbf(UpdateSubscriberDataMbfRequest updateSubscriberDataMbf, ActiveSubscriberDataDTO activeData, boolean decree13) {
        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: Get active result for serial {}, isdn {}", updateSubscriberDataMbf.getStrSerial(), updateSubscriberDataMbf.getStrIsdn());

        //Step 1: Gửi yêu cầu cập nhật thông tin
        log.info("[ACTIVE_SUBSCRIBER_STEP_5: Data for modify info MBF: {}", updateSubscriberDataMbf);

        BaseIntegrationRequest modifyInfoRequest = integrationPort.buildIntegrationRequest(MBF_CMD, UPDATE_SUBSCRIBER_INFO_TYPE, null, updateSubscriberDataMbf);
        ModifyInfoPrepaidV2Response modifyInfoResponse = integrationPort.executeRequest(modifyInfoRequest, ModifyInfoPrepaidV2Response.class);
        if(Objects.equals(modifyInfoResponse.getCode(), MODIFY_INFO_PREPAID_SUCCESS_CODE)){
            log.info("[ACTIVE_SUBSCRIBER_STEP_5]: Active subscriber successfully for isdn {}, serial {}", updateSubscriberDataMbf.getStrIsdn(), updateSubscriberDataMbf.getStrSerial());

            //Step 2: Gửi yêu cầu submit nghị định 13 (chỉ khi decree13 = true)
            SubmitDecree13Response submitDecree13Response = null;
            if (decree13) {
                submitDecree13Response = submitAgreeDecree13(activeData.getAgreeDegree13(), updateSubscriberDataMbf.getStrIsdn(), activeData.getContractCode(), modifyInfoResponse.getData().get(0).getStrSubId());
            }

            //Cập nhật trạng thái thành đã đồng bộ thông tin thành công
            SubscriberDTO esimRegistration = subscriberRepoPort.findByLastIsdn(activeData.getIsdn()).orElseThrow(() -> BaseException.notFoundError(ErrorCode.ISDN_NOT_FOUND).build());
            esimRegistration.setVerifiedStatus(EsimRegistrationStatus.ACTIVED.getValue());
            if (submitDecree13Response != null) {
                esimRegistration.setMbfSubId(modifyInfoResponse.getData().get(0).getStrSubId());
            }
            esimRegistration.setStatus(Status.ACTIVE.getValue());
            esimRegistration = subscriberServicePort.saveAndFlushNewTransaction(esimRegistration);
            esimRegistration.setUpdateInfoBy(SecurityUtil.getCurrentUsername() == null ? SYSTEM : SecurityUtil.getCurrentUsername());
            esimRegistration.setUpdateInfoDate(LocalDateTime.now());

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
    private SubmitDecree13Response submitAgreeDecree13(AgreeDecree13DTO agreeDecree13, String isdn, String contractId, String subId){
        // "CT01:1;CT02:1;CT03:1;CT04:1;CT05:1;CT06:1;"
         String tc1 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk1());
         String tc2 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk2());
         String tc3 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk3());
         String tc4 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk4());
         String tc5 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk5());

        SubmitDecree13Request submitRequest = SubmitDecree13Request.builder()
            .isdn(isdn)
            .subId(subId)
            .contractId(contractId)
            .tc1(tc1)
            .tc2(tc2)
            .tc3(tc3)
            .tc4(tc4)
            .tc5(tc5)
            .noteDesc("DGL")
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(MBF_CMD, DECREE13_TYPE, null, submitRequest);
        SubmitDecree13Response response = integrationPort.executeRequest(integrationRequest, SubmitDecree13Response.class);

        if(!Objects.equals(response.getCode(), SUBMIT_DECREE13_SUCCESS_CODE)){
            log.error("[ACTIVE_SUBSCRIBER_STEP_5]: Submit decree 13 fail for isdn {} by {}", isdn, response.getDescription());
            throw BaseException.badRequest(ErrorCode.SUBMIT_DECREE13_FAIL)
                .message("Lỗi khi xác nhận nghị định 13 do "+ response.getDescription())
                .addParameter(MESSAGE, response.getData().get(0) == null ? response.getDescription() : response.getData().get(0).getError())
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
        if(activeData.getContractPdfUrl() != null){
            Resource activeContractPdf = downloadFile(activeData.getContractPdfUrl());
            subscriber.setContractPdfUrl(uploadFileToMinio(new ByteArrayInputStream(activeContractPdf.getContentAsByteArray()),
                urlFolder,
                activeContractPdf.getFilename()));
        }

        //File hợp dồng kích hoạt PNG
        Resource activeContractPng = downloadFile(activeData.getContractPngUrl());
        subscriber.setContractPngUrl(uploadFileToMinio(new ByteArrayInputStream(activeContractPng.getContentAsByteArray()),
            urlFolder,
            activeContractPng.getFilename()));

        //File chữ ký nè
        Resource signatureFile = downloadFile(activeData.getSignatureUrl());
        subscriber.setSignatureUrl(uploadFileToMinio(new ByteArrayInputStream(signatureFile.getContentAsByteArray()),
            urlFolder,
            signatureFile.getFilename()));

        //Ảnh nghị định 13
        if(activeData.getAgreeDecree13PdfUrl() != null){
            Resource decree13File = downloadFile(activeData.getAgreeDecree13PdfUrl());
            subscriber.setDecree13PdfUrl(uploadFileToMinio(new ByteArrayInputStream(decree13File.getContentAsByteArray()),
                urlFolder,
                decree13File.getFilename()));
        }

        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: Upload all files to folder {}", urlFolder);
    }

    @Override
    public List<ApplicationConfigDTO> getDegree13(HttpHeaders headers) {
        String language = !headers.getAcceptLanguage().isEmpty() ? headers.getAcceptLanguage().get(0).toString() : DEFAULT_LANGUAGE;
        return applicationConfigRepoPort.getByTypeAndLanguage(DEGREE_13_CONFIG_TYPE, language);
    }

    @Override
    public Resource downloadFile(String url) {
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
    @Override
    public List<List<String>> buildArrImagesForUpdateMbf(ActiveSubscriberDataDTO activeData){
        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: download files for active subscriber with transactionId: {}", activeData.getTransactionId());

        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: download pass");
        Resource passportFile = downloadFile(activeData.getPassportUrl());

        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: download portrait");
        Resource portraitFile = downloadFile(activeData.getPortraitUrl());

        log.info("[ACTIVE_SUBSCRIBER_STEP_5]: download active contract");

        Resource activeContractPngFile = downloadFile(activeData.getContractPngUrl());

        Resource agree13PngFile = downloadFile(activeData.getAgreeDecree13PngUrl());

        //Build tham số hình ảnh hộ chiếu
        List<String> passportArr = List.of(StringUtils.hasText(passportFile.getFilename()) ? passportFile.getFilename() : DEFAULT_PASS_PORT_FILE_NAME, buildBase64Image(passportFile), ARR_IMAGES_IMAGE_TYPE);

        //Build tham số hình ảnh chân dung
        List<String> portraitArr = List.of(StringUtils.hasText(portraitFile.getFilename()) ? portraitFile.getFilename() : DEFAULT_PORTRAIT_FILE_NAME, buildBase64Image(portraitFile), ARR_IMAGES_PORTRAIT_TYPE);

        //Build tham số file hợp đồng
        List<String> activeContractArr = List.of(StringUtils.hasText(activeContractPngFile.getFilename()) ? activeContractPngFile.getFilename() : DEFAULT_ACTIVE_CONTRACT_FILE_NAME, buildBase64Image(activeContractPngFile), ARR_IMAGES_CONTRACT_TYPE);

        //Build tham số file nghị định 13
        List<String> agree13Arr = List.of(StringUtils.hasText(agree13PngFile.getFilename()) ? agree13PngFile.getFilename() : DEFAULT_AGREE_13_FILE_NAME, buildBase64Image(agree13PngFile), ARR_IMAGES_AGREE_13_TYPE);

        return Arrays.asList(passportArr, passportArr, portraitArr, activeContractArr, agree13Arr);
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
        String strProvince = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_PROVINCE).getName();
        String strDistrict = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_DISTRICT).getName();
        String strPrecinct = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_PRECINCT).getName();
        String strNationality = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_NATIONALITY).getName();
        String strSubName = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_SUB_NAME).getName();
        String strSex = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_SEX).getName();
        String strBirthday = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_BIRTHDAY).getName();
        String strPasspost = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_PASSPORT).getName();
        String strPasspostIssueDate = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_PASSPORT_ISSUE_DATE).getName();
        String strPasspostIssuePlace = applicationConfigRepoPort.getByTableNameAndColumnName(MODIFY_INFOR_TABLE_NAME, STR_PASSPORT_ISSUE_PLACE).getName();

        ModifyInforParamsDTO params = ModifyInforParamsDTO.builder()
            .strSubType(strSubType)
            .strCustType(strCustType)
            .strReasonCode(strReasonCode)
            .strActionFlag(strActionFlag)
            .strAppObject(strAppObject)
            .strProvince(strProvince)
            .strDistrict(strDistrict)
            .strPrecinct(strPrecinct)
            .strNationality(strNationality)
            .strSubName(strSubName)
            .strSex(strSex)
            .strBirthday(strBirthday)
            .strPasspost(strPasspost)
            .strPasspostIssueDate(strPasspostIssueDate)
            .strPasspostIssuePlace(strPasspostIssuePlace)
            .build();

//        redisStoreOperation.putWithExpireTime(MODIFY_INFO_PARAM_CACHE_KEY, params, 30, TimeUnit.MINUTES);
        return params;
    }

    @Override
    public ModifyInforParamsDTO getModifyInfoParamFromCache(){
//        ModifyInforParamsDTO params = (ModifyInforParamsDTO) redisStoreOperation.get(MODIFY_INFO_PARAM_CACHE_KEY);
//        if(params == null){
//            return cacheModifyInfoParam();
//        }
//        return params;
        return cacheModifyInfoParam();
    }

    /**
     * Kiểm tra subscriber có được phép kích hoạt hay không
     *
     * @param serial: Kiểm tra theo serial
     */
    @Override
    public SubscriberDTO checkSubscriberIsNotVerified(String serial){
        SubscriberDTO subscriber = subscriberRepoPort.findByLastSerial(serial).orElseThrow(() -> BaseException.badRequest(ErrorCode.ISDN_NOT_FOUND).build());
        checkSubscriberIsNotVerified(subscriber);
        return subscriber;
    }

    /**
     * Kiểm tra subscriber có được phép kích hoạt hay không
     *
     * @param isdn: Kiểm tra theo isdn
     */
    @Override
    public SubscriberDTO checkSubscriberIsNotVerified(Long isdn){
        SubscriberDTO subscriber = subscriberRepoPort.findByLastIsdn(isdn).orElseThrow(() -> BaseException.badRequest(ErrorCode.ISDN_NOT_FOUND).build());
        checkSubscriberIsNotVerified(subscriber);
        return subscriber;
    }

    /**
     * Kiểm tra subscriber có được phép kích hoạt hay không
     *
     * @param subscriber: Số thuê bao cần kiểm tra
     */
    @Override
    public SubscriberDTO checkSubscriberIsNotVerified(SubscriberDTO subscriber){
        if(!Objects.equals(subscriber.getStatusCall900(), Call900Status.CALLED.getValue())
            && SecurityUtil.getCurrentUserId() == null){
                log.error("Subscriber: {} chưa gọi 900", subscriber);
                throw BaseException.badRequest(ErrorCode.SUBSCRIBER_NOT_CALL_900).build();
        }

        if(Objects.equals(subscriber.getVerifiedStatus(), Status.ACTIVE.getValue())){
            log.error("Subscriber: {} đã đăng ký thông tin", subscriber);
            throw BaseException.badRequest(ErrorCode.SUBSCRIBER_IS_VERIFIED).build();
        }

        if(SecurityUtil.getCurrentClientId() != null &&
            !Objects.equals(INTERNAL_CLIENT_ID, SecurityUtil.getCurrentClientId()) &&
            !Objects.equals(subscriber.getClientId(), SecurityUtil.getCurrentClientId())){
                log.error("Subscriber: {} không thuộc client hiện tại", subscriber);
                throw BaseException.badRequest(ErrorCode.ISDN_NOT_BELONG_PARTNER).build();
        }

        if(!Objects.equals(Status.ACTIVE.getValue(), subscriber.getBoughtStatus())){
            log.error("Subscriber: {} chưa được bán", subscriber.getIsdn());
            throw BaseException.badRequest(ErrorCode.SUBSCRIBER_HAS_NOT_BOUGHT).build();
        }

        return subscriber;
    }

    private String buildFinalContractUrl(String transactionId){
        return paramContractConfig.getContract().getSimActive().getFinalContractFolder() + Constant.CommonSymbol.FORWARD_SLASH + transactionId;
    }

    @Override
    public String buildTempContractUrl(String transactionId){
        LocalDate today = LocalDate.now();
        return Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER
            + Constant.CommonSymbol.FORWARD_SLASH // /
            + today.getYear() + "-" + today.getMonthValue()
            + Constant.CommonSymbol.FORWARD_SLASH // /
            + transactionId;
    }

    @Override
    public Long formatIsdn(Long isdn) {
        String isdnStr = isdn.toString();
        return Long.parseLong(isdnStr.startsWith(VIETTEL_PREFIX) ? isdnStr.substring(VIETTEL_PREFIX.length()) : isdnStr);
    }

    @Override
    public byte[] getCskhSignature(String userId, String transactionId) {
        if(userId == null){
            log.info("[ACTIVE_SUBSCRIBER]: Don't need to get cskh signature for transactionId: {}", transactionId);
            return new byte[0];
        }

        try {
            Resource signature = downloadFile(Constant.MinioDir.UserSignature.buildSignatureUrl(userId));
            return signature.getContentAsByteArray();
        } catch (Exception e) {
            log.error("[ACTIVE_SUBSCRIBER]: Get signature of user: {} error", userId, e);
            return new byte[0];
        }
    }

    @Override
    public void genDecree13(ActiveSubscriberDataDTO activeData, byte[] signature) {
        GenContractDTO genContract = activeSubscriberMapper.mapGenContractDto(activeData);
        AgreeDecree13ContractDataDto agree13Contract = activeSubscriberMapper.mapAgree13DFromGenContract(genContract, activeData.getAgreeDegree13());

        //Fill dữ liệu chữ ký
        agree13Contract.setSignatureCustomer(signature);
        agree13Contract.setSignatureCskh(getCskhSignature(activeData.getUserId(), activeData.getTransactionId()));

        DownloadOptionDTO downloadOption =  getDownloadDecree13ContractOption();
        Resource template = minioOperations.download(downloadOption);

        try {
            ByteArrayOutputStream pdfFile = PdfContractUtils.fillDataToPdf(template.getInputStream().readAllBytes(), agree13Contract, AgreeDecree13ContractDataDto.class);
            ByteArrayOutputStream pngFile = PdfContractUtils.convertPdfToOneJpgFile(pdfFile.toByteArray(), true);

            String agree13PdfUrl = uploadFileToMinio(pdfFile.toByteArray(), Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, activeData.getTransactionId() + "/" + AGREE_13_FILE_NAME + TypeContract.PDF);
            String agree13PngUrl = uploadFileToMinio(pngFile.toByteArray(), Constant.MinioDir.ActiveSubscriber.TEMP_FOLDER, activeData.getTransactionId() + "/" + AGREE_13_FILE_NAME + TypeContract.PNG);

            activeData.setAgreeDecree13PdfUrl(agree13PdfUrl);
            activeData.setAgreeDecree13PngUrl(agree13PngUrl);
        } catch (IOException e) {
            log.error("[ACTIVE_SUBSCRIBER]: Generate agreement decree 13 error", e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message(e.getMessage())
                .addProblemDetail(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage()))
                .build();
        }
    }
}

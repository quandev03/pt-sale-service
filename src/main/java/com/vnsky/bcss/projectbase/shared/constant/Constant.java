package com.vnsky.bcss.projectbase.shared.constant;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorSubcriberKey;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.NumberTransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;


@SuppressWarnings("all")
@UtilityClass
public class Constant {
    public static final String REQUIRED_FIELD = "Không được bỏ trống trường này";

    public static final String TOTAL = "TOTAL";

    public static final String SUCCESS_CODE = "0000";

    public static final String EKYC_SUCCESS_CODE = "00";

    public static final String FAIL_CODE = "CM99";

    public static final String INVALID_REQUEST_CODE = "CM98";

    public static final String TIMEOUT_CODE = "CM97";
    public static final String VALIDATION_ERROR_CODE = "CM01";

    public static final String ASSERTION_ERROR_CODE = "CM02";

    public static final String PARTNER_ERROR_CODE = "CM03";

    public static final String FAIL_OCR_CODE = "CM04";

    public static final String FAIL_EKYC_FACE_CHECK_CODE = "CM05";

    public static final String FAIL_C06_CODE = "CM06";

    public static final String FAIL_LOGIN_CODE = "CM07";

    public static final String INVALID_FILE_SIZE = "CM08";

    public static final String FAIL_MBF_CODE = "CM09";

    public static final String FAIL_GET_OTP_CODE = "CM17";

    public static final String FAIL_CONFIRM_OTP_CODE = "CM10";

    public static final String OUT_OF_TIME_ACTIVATE_CODE = "CM11";

    public static final String OUT_OF_ID_EXPIRY_CODE = "CM12";

    public static final String ACTION_TOO_FAST_CODE = "CM13";

    public static final String OUT_OF_MAX_REQUEST_IN_DAY_CODE = "CM14";

    public static final String FAIL_TO_ACTIVATE_SUBSCRIBER_CODE = "CM15";

    public static final String UPLOAD_IMAGE_FAIL_CODE = "CM16";

    public static final String CONTRACT_NO_EXCEED_LIMIT_CODE = "CM17";

    public static final String CUSTOMER_CODE_EXCEED_LIMIT_CODE = "CM18";

    public static final Long DOCUMENT_IS_LASTEST_VERSION = 1L;

    public static final Integer ACTIVED = 1;

    public static final String TEMPLATE_EMAIL_ESIM = "EmailEsim";

    public static final MediaType XLSX = MediaType.valueOf(HeaderConstants.HEADER_VALUE_APPLICATION);

    public static final String VNSKY_CLIENT_ID = "000000000000";

    public static final String COMMA = ",";

    public static final String SYSTEM = "SYSTEM";

    public static final String EMPTY_STRING = "";

    public static final String MESSAGE_SUCCESS = "Thành công";
    public static final String MESSAGE_VALID = "Hợp lệ";
    public static final String MESSAGE_FAILURE = "Thất bại";

    public static final String DATE_TIME_FORMAT = "ddMMyyyyHHmmss";

    public static final String COMMON_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static final DateTimeFormatter COMMON_DATE_TIME_FORMAT_ZONE_FORMATER = DateTimeFormatter
        .ofPattern(COMMON_DATE_TIME_FORMAT)
        .withZone(ZoneId.systemDefault());

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorMessage {
        public static final String SUCCESS_CODE = "CAT00000";

        public static final String REQUEST_INITIATED = "REQUEST_INITIATED";
        public static final String RESPONSE_RECEIVED = "RESPONSE_RECEIVED";
        public static final String INTERNAL_SERVER_ERROR_CODE = "CAT00999";

        public static final String INVALID_REQUEST_CODE = "CAT00998";

        public static final String TIMEOUT_CODE = "CAT00997";
        public static final String VALIDATION_ERROR_CODE = "CAT00001";

        public static final String FIELD_REQUIRED = "CAT00002";

        public static final String FIELD_NOT_MATCH_PATTERN = "CAT00003";

        public static final String ASSERTION_ERROR_CODE = "CAT00004";

        public static final String PARTNER_ERROR_CODE = "CAT00005";
        public static final String FIELD_INVALID_FORMAT = "CAT00006";
        public static final String FIELD_MAX_10 = "CAT00101";
        public static final String FIELD_MAX_20 = "CAT00102";
        public static final String FIELD_MAX_30 = "CAT00103";
        public static final String FIELD_MAX_50 = "CAT00104";
        public static final String FIELD_MAX_100 = "CAT00105";
        public static final String FIELD_MAX_200 = "CAT00106";

        public static final String APP_PICK_LIST_NOT_CONTAINS_CODE = "CAT00100";
        public static final String PARENT_ID_NOT_EXISTS = "CAT00099";

        public static final String ERROR_EXISTED = "SALE15101";
        public static final String ERROR_NOT_FOUND = "SALE15102";

        //sale order
        public static final String SALE_ORDER_NOT_EXISTS = "SALE02000";

        //Stock product
        public static final String NOT_PERMISSION_WATCH_STOCK = "SALE01602";

        //Delivery note
        public static final String DELIVERY_NOTE_OUT_NOT_FOUND = "SALE02001";

        //Reason
        public static final String REASON_NOT_FOUND = "SALE03001";
    }

    public static final class SubAction{
        public static final String ACTIVE = "ACTIVE";
        public static final String CHANGE_SIM = "CHANGE_SIM";
        public static final String BLOCK_1_WAY = "BLOCK_1_WAY";
        public static final String BLOCK_2_WAY = "BLOCK_2_WAY";
        public static final String OPEN_1_WAY = "OPEN_1_WAY";
        public static final String OPEN_2_WAY = "OPEN_2_WAY";
        public static final String CHANGE_INFO = "CHANGE_INFO";
        public static final String CHANGE_ZONE = "CHANGE_ZONE";
        public static final String OPEN_ACTION = "BLOCK_ACTION";
        public static final String BLOCK_ACTION = "BLOCK_ACTION";
        public static final String CANCEL_PACKAGE = "CANCEL_PACKAGE";
        public static final String REGISTER_PACKAGE = "REGISTER_PACKAGE";
        public static final String CANCEL_SERVICE = "CANCEL_SERVICE";
        public static final String REGISTER_SERVICE = "REGISTER_SERVICE";

    }

    public static final class OrderType{
        public static final String BOOK_ESIM = "Book eSIM";
        public static final String MUA_GOI = "Mua gói";
        public static final String BAN_GOI = "Bán gói";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Order{
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class OrderType{
            public static final String BOOK_ESIM = "Book eSIM";
            public static final String MUA_GOI = "Mua gói";
            public static final String BAN_GOI = "Bán gói";
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public class SubscriberStatusConstant {

        public static final String DA_CAP_NHAT_TTTB     = "Đã cập nhật TTTB";
        public static final String DA_GOI_900           = "Đã gọi 900";
        public static final String DA_BAN               = "Đã bán";
        public static final String TRONG_KHO            = "Trong kho";
        public static final String CHUA_SU_DUNG         = "Chưa sử dụng";

        public static final String KHONG_BI_CHAN                 = "Không bị chặn";
        public static final String CHAN_MOT_CHIEU_YEU_CAU        = "Chặn một chiều do yêu cầu";
        public static final String CHAN_HAI_CHIEU_YEU_CAU        = "Chặn hai chiều do yêu cầu";
        public static final String CHAN_MOT_CHIEU_NHA_MANG       = "Chặn một chiều do nhà mạng";
        public static final String CHAN_HAI_CHIEU_NHA_MANG       = "Chặn hai chiều do nhà mạng";
    }


    public static final String ACTIVED_STRING = "1";

    public static final String CREATE_NEW_CODE = "CM";

    public static final String TOKEN_EXPIRED = "JWT token is expired";
    public static final String TOKEN_MISSING = "JWT token is missing";
    public static final String REQUEST_INITIATED = "REQUEST_INITIATED";
    public static final String RESPONSE_RECEIVED = "RESPONSE_RECEIVED";
    public static final String REMOTE_ADDRESS = "REMOTE_ADDRESS";

    public static final String TIMESTAMP_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String TIMESTAMP_DATE_PATTERN_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TIMESTAMP_DATE_PATTERN_UTC_2 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String TIMESTAMP_DATE_PATTERN_DEFAULT = "yyyy-MM-dd";
    public static final String TIMESTAMP_DATE_PATTERN_DEFAULT_2 = "dd-MM-yyyy";
    public static final String TIMESTAMP_DATE_PATTERN_DEFAULT_3 = "dd-MM-yyyy'T'HH:mm:ss";
    public static final String YEAR_AND_MONTH_PATTERN = "yyyyMM";
    public static final String C06_DATE_PATTERN = "yyyyMMdd";
    public static final String OCR_DATE_PATTERN = "dd-MM-yyyy";
    public static final String FE_DATE_PATTERN = "dd/MM/yyyy";
    public static final String TIME_STAMP_FE_DATE = "dd/MM/yyyy HH:mm:ss";
    public static final String TIME_STAMP_EXPORT_DATE = "ddMMyyyyHHmmss";
    public static final String DATE_TIME_NO_SYMBOL_PATTERN = "yyyyMMddHHmmss";
    public static final String REGEX_VALID_ID_NO = "^[0-9]{9,12}$";
    public static final String YEAR = "yy";
    public static final String TIMESTAMP_DATE_PATTERN_SIMPLE = "yyyy-MM-dd'T'HH:mm:ss";

    public static final DateTimeFormatter FE_DATE_FORMATTER = DateTimeFormatter.ofPattern(FE_DATE_PATTERN);
    public static final DateTimeFormatter TIME_STAMP_FE_DATE_FORMATTER = DateTimeFormatter.ofPattern(TIME_STAMP_FE_DATE);

    public static class ApplicationConfig {
        public static final String ACTIVE = "1";
        public static final String INACTIVE = "0";
        public static final Integer ACTIVE_NUMBER = 1;
        public static final Integer INACTIVE_NUMBER = 0;
        public static final String SUBSCRIBER_ACTIVE_PARAM = "SUBSCRIBER_ACTIVE_PARAM";
        public static final String SUB_FREQUENCY = "SUB_FREQUENCY";
        public static final String MAX_SUBSCRIBER_BY_USER = "MAX_SUBSCRIBER_BY_USER";
        public static final String SUBSCRIBER_ACTIVE_TIME = "SUBSCRIBER_ACTIVE_TIME";
        public static final String SUBSCRIBER_ACTIVE_REQUIREMENT = "SUBSCRIBER_ACTIVE_REQUIREMENT";
        public static final String SUBSCRIBER_ACTIVE_BYPASS = "SUBSCRIBER_ACTIVE_BYPASS";
        public static final String ALLOW_CHECK_FAILED = "ALLOW_CHECK_FAILED";
        public static final String OTP_REQUIRE = "OTP_REQUIRE";
        public static final String C06_REQUIRED = "C06_REQUIRE";
        public static final Integer FIELD_CODE = 1;
        public static final Integer FIELD_NAME = 2;
        public static final String ID_TYPE = "ID_TYPE";
        public static final Set<String> VALID_CODE = Set.of(
            ApplicationConfig.MAX_SUBSCRIBER_BY_USER,
            ApplicationConfig.SUB_FREQUENCY,
            ApplicationConfig.SUBSCRIBER_ACTIVE_TIME
        );
        public static final Set<String> VALID_TYPE = Set.of(
            ApplicationConfig.SUBSCRIBER_ACTIVE_REQUIREMENT,
            ApplicationConfig.SUBSCRIBER_ACTIVE_PARAM,
            ApplicationConfig.SUBSCRIBER_ACTIVE_TIME
        );
    }

    public static class ConditionCode {
        public static final String NAME_MAX30 = "NAME_MAX30";
        public static final String NAME_DIFFIRENT = "NAME_DIFFIRENT";
        public static final String BIRTH_DIFFIRENT = "BIRTH_DIFFIRENT";
        public static final String IDNO_SPECIAL = "IDNO_SPECIAL";
        public static final String IDNO_LENGTH_9_12 = "IDNO_LENGTH_9_12";
        public static final String MAX_SUBSCRIBER = "MAX_SUBSCRIBER";
        public static final String CCCD_BIRTH_DIFF = "CCCD_BIRTH_DIFF";
        public static final String CMND_PLACE_DIFF = "CMND_PLACE_DIFF";
        public static final String CONDITON_KEY_MAP = "CONDITON_KEY_MAP";
        public static final String C06_REQUIRE = "C06_REQUIRE";
        public static final Long CHECKED = 1L;
        public static final Long UN_CHECKED = 0L;

    }

    public static class AuditCode {
        public static final String NOT_FOUND_AUDIT = "Không xác định";
        public static final String APPROVE_DATE = "APPROVE_DATE";
        public static final String CREATED_DATE = "CREATED_DATE";
        public static final String MODIFIED_DATE = "MODIFIED_DATE";
        public static final String CONTRACT_DATE = "CONTRACT_DATE";
        public static final String AUDIT_DATE = "AUDIT_DATE";

        public static final String BIRTH_DATE = "BIRTH_DATE";

        public static final String ID_ISSUE_DATE = "ID_ISSUE_DATE";

        public static final String NOT_FOUND = "Không xác định";
        public static final List<String> conditionCodes = Arrays.asList("NAME_MAX30", "NAME_DIFFIRENT", "BIRTH_DIFFIRENT", "IDNO_SPECIAL", "IDNO_LENGTH_9_12", "MAX_SUBSCRIBER", "CCCD_BIRTH_DIFF", "CMND_PLACE_DIFF");

        public static final  Map<String, String> ID_TYPE_MAP =
            Map.of(
            "1", "CCCD",
            "2", "CMND",
            "3", "HC"
        );

        public static final Map<Integer, String> CUSTOMER_STATUS_NOTE = Map.of(
            1, "Hoạt động",
            0, "Không hoạt động"
        );

        public static final  Map<String, String> CUSTOMER_TYPE_MAP = Map.of(
            "1", "Cá nhân",
            "2", "Doanh nghiệp"
        );
        public static final Map<Integer, String> APPROVE_STATUS_MAP = Map.of(
            1, "Chờ duyệt",
            2, "Đã duyệt",
            3, "Kiểm duyệt lại",
            4, "Yêu cầu cập nhật giấy tờ"
        );
        public static final Map<Integer, String> AUDIT_STATUS_MAP = Map.of(
            0, "Chưa hậu kiểm",
            1, "Đã hậu kiểm"
        );
        public static final Map<String, String> FIELD_ERROR_MAPPING = Map.of(
            "NAME_MAX30", "name",
            "NAME_DIFFIRENT", "name",
            "IDNO_SPECIAL", "idNo",
            "IDNO_LENGTH_9_12", "idNo",
            "BIRTH_DIFFIRENT", "birthDate",
            "BIRTH_DATE", "birthDate",
            "CCCD_BIRTH_DIFF", "idIssueDate",
            "CMND_PLACE_DIFF", "idNo",
            "MAX_SUBSCRIBER", "idNo"
        );
    }

    public static class CustomerCodePrefix {
        public static final String CUSTOMER_CODE_PREFIX = "VNS516";
    }

    public static class SequenceFromDB {
        public static final String SUB_CONTRACT_NO_SEQ = "SUB_CONTRACT_NO_SEQ";
        public static final String SUB_CUSTOMER_CODE_SEQ = "SUB_CUSTOMER_CODE_SEQ";
    }

    public static class ContractNoPrefix {
        public static final String CONFIRM_NOTE_ONLINE_PREFIX = "CNON";
        public static final String CONFIRM_NOTE_OFFLINE_PREFIX = "CNTT";
        public static final String CONTRACT_PREFIX = "HDTT";
        public static final String CONTRACT = "1";
        public static final String NOTE = "2";
    }

    public static class Error {
        public static final String CODE = "code";
        public static final String MESSAGE = "message";
        public static final String CUS00033_01 = "contract template not found";
        public static final String CUS00033_02 = "file size to large";
        public static final String CUS00033_03 = "convert data to base64 false";
        public static final String CUS00033_04 = "file contract not exists";
        public static final String CUS00099_04 = "file not exists";
        public static final String C06_FAIL = "Số giấy tờ không tồn tại trong cơ sở dữ liệu quốc gia";
    }


    public static class ImageCode {
        public static final String ID_FRONT = "1";
        public static final String ID_BACK = "2";
        public static final String FRONTAL_PORTRAIT = "3";
        public static final String CROSS_PORTRAIT = "4";
    }

    public static class ImageCodeSub {
        public static final int ID_FRONT = 1;
        public static final int ID_BACK = 2;
        public static final int FRONTAL_PORTRAIT = 3;
        public static final int CONTRACT = 4;
    }

    public static class ImageType {
        public static final String ID_DOCUMENT = "1";
        public static final String CONTRACT = "2";
        public static final String DECREE_13 = "3";
    }

    public static class ImageTypeSub {
        public static final int ID_DOCUMENT = 1;
        public static final int CONTRACT = 2;
        public static final int CONTRACT_FINAL = 3;
    }

    public static class ImageMultiFile {
        public static final String FRONT = "front";
        public static final String BACK = "back";
        public static final String PORTRAIT = "portrait";
        public static final String CONTRACT = "contract";
        public static final String DECREE13 = "decree13";
    }

    public static class Sex {
        public static final Map<String, String> SEX_MAP = Map
            .of("Nam", "1",
                "Nữ", "0");
        public static final String MALE = "Nam";
        public static final String FEMALE = "Nữ";
        public static final String SEX = "SEX";
        public static final String MALE_NUMBER = "1";
        public static final String FEMALE_NUMBER = "0";
        public static final Map<String, String> CCCD_SEX_BEFORE_2000_MAP = Map.of(
            "1", "0",
            "0", "1"
        );
        public static final Map<String, String> CCCD_SEX_AFTER_2000_MAP = Map.of(
            "1", "2",
            "0", "3"
        );
    }

    public static class IdType {
        public static final String CCCD = "CCCD";
        public static final String CMND = "CMND";
        public static final String HC = "HC";
        public static final String CCCD_NUMBER = "1";
        public static final String CMND_NUMBER = "2";
        public static final String HC_NUMBER = "3";
        public static final Map<String, String> idTypeMap = Map.of(
            "CCCD", "1",
            "CMND", "2",
            "HC", "3"
        );
        public static final Map<String, String> idTypeMapForC06 = Map.of(
            "1", "CCCD",
            "2", "CMND",
            "3", "HC"
        );
    }

    public static class RedisActiveKey {
        public static final String KEY_VERIFY = "VERIFY";
        public static final String C06 = "C06";
        public static final String OTP = "OTP";
        public static final String ACTIVE_SUB = "ACTIVE_SUB";
        public static final String CHECK_SUM = "CHECK_SUM";
        public static final String NUMBER_ISDN_OF_IDNO = "NUMBER_ISDN_OF_IDNO";
        public static final String SAR_EKYC = "SAR_EKYC";
        public static final String SAR_DECREE = "SAR_DECREE";
        public static final String DECREE13_CHECK = "DECREE13_CHECK";
        public static final String DECREE13_DATA = "DECREE13_DATA";
        public static final String USER_FREQUENCY = "FREQUENCY";
        public static final String CHECK_TYPE_CONTRACT_AND_PROVIDER_AREA_CODE = "CHECK_TYPE_CONTRACT_AND_PROVIDER_AREA_CODE";
        public static final String CONDITION_NO_7_SEX = "CONDITION_NO_7_SEX";
        public static final String CONDITION_NO_7_BIRTH = "CONDION_NO_7_BIRTH";

    }

    public static class HeaderConstants {
        public static final String LIST_REQUEST_APPROVAL = "Danh sách yêu cầu kích hoạt";
        public static final String LIST_PRE_APPROVAL_ASSIGNMENT = "Danh sách phân công tiền kiểm";
        public static final String LIST_PRE_CHECK = "Danh sách tiền kiểm";

        public static final String NO = "STT";
        public static final String EMPLOYEE_CODE = "Mã Nhân viên phát triển";
        public static final String EMPLOYEE_NAME = "Nhân viên phát triển";
        public static final String DISTRIBUTOR = "Nhà phân phối";
        public static final String CONTRACT_NUMBER = "Số hợp đồng";
        public static final String REQUEST_DATE = "Ngày tạo yêu cầu";
        public static final String SUBSCRIBER_NUMBER = "Số thuê bao";
        public static final String CUSTOMER_NAME = "Tên KH";
        public static final String BIRTH_DAY = "Ngày sinh";
        public static final String IDENTIFICATION = "Số GTTT";
        public static final String IDENTIFICATION_TYPE = "Loại GTTT";
        public static final String CUSTOMER_TYPE = "Loại KH";
        public static final String PRECHECK_USER = "User tiền kiểm";
        public static final String PRECHECK_DATE = "Ngày tiền kiểm";
        public static final String PRECHECK_STATUS = "Trạng thái tiền kiểm";
        public static final String ACTIVE_STATUS = "Trạng thái kích hoạt";
        public static final String PRECHECK_APPROVE_STATUS = "Trạng thái phân công tiền kiểm";
        public static final String REJECTION_REASON = "Lý do từ chối";

        // Search Subscriber
        public static final String HEADER_VALUE_FILENAME_SEARCH_SUB = "attachment; filename=search-subscriber.xlsx";
        public static final String ISDN = "Số thuê bao";
        public static final String SERIAL = "Serial sim";
        public static final String GTTT = "Số GTTT";
        public static final String CUS_NAME = "Tên khách hàng";
        public static final String ACTIVE_DATE = "Ngày kích hoạt";
        public static final String ACTIVE_USER = "User kích hoạt";
        public static final String STATUS = "Trạng thái thuê bao";
        public static final String APPROVAL_STATUS = "Trạng thái kiểm duyệt";
        public static final String UPDATE_DOCUMENT_STATUS = "Trạng thái cập nhật giấy tờ";
        public static final String BLOCK_STATUS = "Trạng thái chặn cắt";
        public static final String REVOKE_DATE = "Ngày thu hồi";

        public static final String CONTENT_DISPOSITION = "Content-Disposition";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String HEADER_VALUE_FILENAME = "attachment; filename=subscriber_active_list.xlsx";
        public static final String HEADER_VALUE_APPLICATION = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    public static class FileNameExportExcelPreApprove {
        public static final String ATTACHMENT_FILENAME = "attachment; filename=";
        public static final String FILE_TYPE = ".xlsx";
        public static final String LIST_REQUEST_APPROVAL = "Danh_sach_yeu_cau_kich_hoat";
        public static final String LIST_PRE_APPROVAL_ASSIGNMENT = "Danh_sach_phan_cong_tien_kiem";
        public static final String LIST_PRE_CHECK = "Danh_sach_tien_kiem";
    }

    public static final String PDF_EXTENSION = ".PDF";
    public static final String PNG_EXTENSION = ".PNG";

    public static class DocType {
        public static final String ID_IMAGE = "0";
        public static final String FACE_IMAGE = "1";
        public static final String CONTRACT = "2";
        public static final String CCCD = "CCCD";
        public static final String CCC = "CCC";
        public static final String ID_FRONT = "FRONT";
        public static final String ID_BACK = "BACK";
        public static final String ID_STR_FACE = "FACE";
        public static final String SIGN = "SIGN";
    }

    public static class StaticFixParam {
        public static final String REASON_CODE = "REASON_CODE";
        public static final String ARR_REG_PROM = "ARR_REG_PROM";
        public static final String STR_REG_TYPE = "STR_REG_TYPE";
        public static final String STR_KIT_TYPE = "STR_KIT_TYPE";
        public static final String STR_CUST_TYPE = "STR_CUST_TYPE";
        public static final String ARR_REG_SERVICE = "ARR_REG_SERVICE";
        public static final String STR_APP_OBJECT = "STR_APP_OBJECT";
        public static final String REG_SERVICE_LIST = "STATIC_FIX_PARAM";
        public static final String STR_SUB_TYPE = "STR_SUB_TYPE";
    }

    public static class ContractND13 {
        public static final String SUB_DOCUMENT_ND13 = "SUB_DOCUMENT_ND13";
        public static final String DK1 = "DK1";
        public static final String DK2 = "DK2";
        public static final String DK3 = "DK3";
        public static final String DK4 = "DK4";
        public static final String DK5 = "DK5";
        public static final String DK6 = "DK6";
        public static final String DEFAULT_DK = "DEFAULT_DK";
        public static final String Checked = "X";

        public static final String CONTRACT_DECREE_PREFIX = "ND13_";
        public static final String CONTRACT_DECREE_SIGNATURE = "SIGNATURE_";
    }

    public static class MessageWelcome {
        public static final String MESSAGE = "Chao mung Quy khach den voi VNSKY! Quy khach da kich hoat thanh cong so dien thoai phoneNumber. Hay truy cap app VNSKY de trai nghiem nhung tinh nang thu vi. Cam on Quy khach da tin tuong va lua chon dich vu cua VNSKY. Moi thac mac Quy khach vui long lien he 19005222 de duoc ho tro chi tiet *Cuoc phi 1000d/phut).";
        public static final String SENDER = "VNSKY";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String MESSAGE_REQUIRE_UPDATE_DOCUMENT = "So theu bao phoneNumber. Thong tin ho so theu bao khong hop le. Vui long cap nhat thong tin ho so trong thoi han 5 ngay. Trong truong hop quy khach khong cap nhat thong tin ho so, thue bao cua quy khach se bi chan";
    }

    public static class EightConditionFieldError {
        public static final String BIRTH_DATE = "birthday";
        public static final String ID_ISSUE_PLACE = "issue_by";
        public static final String ID_NO = "id";
        public static final String NAME = "name";
        public static final String SEX = "sex";
    }

    public static class Ekyc {
        public static final List<String> ERROR_LIST = Arrays.asList("03", "04", "05", "09");
    }

    public static class Mbf {
        public static final List<String> ACTIVE_ISDN = Arrays.asList("00", "01", "10", "11");
    }

    public static class Folder {
        public static final String CUSTOMER_MANAGEMENT = "projectbase-management";
        public static final String LOG_SYNCH_MBF = "BASE64 DATA OMITTED";
    }

    public static class EightConditionFieldErrorForApprove {
        public static final String BIRTH_DATE = "birthday";
        public static final String ID_ISSUE_PLACE = "issue_by";
        public static final String ID_NO = "id";
        public static final String MAX_SUBSCRIBER = "MAX_SUBSCIRBER";
        public static final String NAME = "name";
        public static final String FORCE_CHECK_CRITERIA = "FORCE_CHECK_CRITERIA";
    }

    public static final String CHECK_APPROVE_INFO_MESSAGE = "Kiểm tra thông tin thành công";

    public static class ApproveStatus {
        public static final Integer WAITING_FOR_APPROVE = 1;
        public static final Integer APRROVED = 2;
        public static final Integer APPROVE_AGAIN = 3;
        public static final Integer REQUIRE_UPDATE_IDENTIFIER = 4;

        public static final String WAITING_FOR_APPROVE_MESSAGE = "Chờ duyệt";
        public static final String APPROVED_MESSAGE = "Đã duyệt";
        public static final String APPROVE_AGAIN_MESSAGE = "Kiểm duyệt lại";
        public static final String REQUIRE_UPDATE_IDENTIFIER_MESSAGE = "Yêu cầu cập nhật giấy tờ";
    }

    public static class ApproveStatusNumber {
        public static final Integer WAITING = 0;
        public static final Integer APPROVED = 1;
        public static final Integer REFUSE = 2;
    }

    public static class ApproveStatusText {
        public static final String WAITING = "Chờ duyệt";
        public static final String APPROVED = "Đã duyệt";
        public static final String REFUSE = "Từ chối";
    }

    public static class ActiveStatusText {
        public static final String WAITING = "Chờ xử lý";
        public static final String SUCCESS = "Thành công";
        public static final String FAIL = "Thất bại";
    }

    public static class DocAndAssignStatus {
        public static final Integer NOT_ASSIGN_YET = 0;
        public static final Integer ASSIGNED = 1;
        public static final Integer NOT_UPDATE_DOC_YET = 0;
        public static final Integer UPDATED_DOC = 1;
    }

    public static class AuditStatus {
        public static final Integer NOT_AUDIT_YET = 0;
        public static final Integer AUDITED = 1;
    }

    public static final String ID_TYPE_CCCD_BY_NUMBER = "1";

    public static class MessageApproveSubdocument {
        public static final String SENDER = "VNSKY";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String IDENTIFIER_APPROVE_REJECT_CODE = "APPROVE_11";
        public static final String MESSAGE_REQUIRE_SUBSCRIBER_UPDATE_DOCUMENT =
            "Thong tin thue bao di dong cua Quy khach can duoc chuan hoa theo Nghi dinh 49 cua Chinh phu. " +
                "De tranh gian doan dich vu, Quy khach vui long tai ung dung VNSKY va lam theo huong dan " +
                "de cap nhat thong tin. Moi thac mac Quy khach vui long ket noi tong dai 19005222 " +
                "(Cuoc phi 1000d/phut) de duoc ho tro. Tran trong!";
        public static final String MESSAGE_REQUIRE_SUBSCRIBER_UPDATE_DOCUMENT_IDENTIFIER =
            "Thong tin thue bao di dong cua Quy khach can duoc chuan hoa theo Nghi dinh 49 "
                + "cua Chinh phu do giay to tuy than qua han. De tranh gian doan dich vu, "
                + "Quy khach vui long tai ung dung VNSKY va lam theo huong dan de cap nhat thong tin. "
                + "Moi thac mac Quy khach vui long ket noi tong dai 19005222 (Cuoc phi 1000d/phut) "
                + "de duoc ho tro. Tran trong!";
    }

    public static class FixSonarqueConstant {
        public static final String COMMON_RESPONSE_MESSAGE = "SUCCESS";
        public static final String ERROR_CONSTANT = "Error";
        public static final List<String> EightConditionListConstant = Arrays.asList("NAME_MAX30", "NAME_DIFFIRENT", "BIRTH_DIFFIRENT",
            "IDNO_SPECIAL", "IDNO_LENGTH_9_12", "MAX_SUBSCRIBER", "CCCD_BIRTH_DIFF", "CMND_PLACE_DIFF");
        public static final String PROVINCE_AREA_CODE_TYPE = "1";
        public static final String DISTRICT_AREA_CODE_TYPE = "2";
        public static final String PRECINT_AREA_CODE_TYPE = "3";
        public static final String ID_TYPE_UNDEFINE = "Không xác định";
    }

    public static class TupleFieldConstant {
        public static final String CREATED_BY = "CREATED_BY";
        public static final String ACTIVE_CHANNEL = "ACTIVE_CHANNEL";
        public static final String MODIFIED_DATE = "MODIFIED_DATE";
        public static final String CUST_TYPE = "CUST_TYPE";
        public static final String STATUS = "STATUS";
        public static final String APPROVE_STATUS = "APPROVE_STATUS";
        public static final String DOC_UPDATE_STATUS = "DOC_UPDATE_STATUS";
        public static final String AUDIT_STATUS = "AUDIT_STATUS";
        public static final String ASSIGN_STATUS = "ASSIGN_STATUS";
        public static final String ASSIGN_USER_NAME = "ASSIGN_USER_NAME";
        public static final String AUDIT_REJECT_REASON_CODE = "AUDIT_REJECT_REASON_CODE";
        public static final String VERSION = "VERSION";
        public static final String ID_NO = "ID_NO";
        public static final String ID_TYPE = "ID_TYPE";
        public static final String ID_ISSUE_DATE = "ID_ISSUE_DATE";
        public static final String BIRTH_DATE = "BIRTH_DATE";
        public static final String CONTRACT_NO = "CONTRACT_NO";
        public static final String CUSTOMER_CODE = "CUSTOMER_CODE";
        public static final String CREATED_DATE = "CREATED_DATE";
    }

    public static class SubscriberStatus {
        public static final Integer WAITING_FOR_ACTIVE = 0;
        public static final Integer ACTIVE = 1;
        public static final Integer FAILED = 2;
        public static final Integer TIMEOUT = 3;
    }

    public static class ExportExcelColumnName {
        public static final String ORDINAL_NUMBER = "STT";
        public static final String CUSTOMER_CODE = "Mã KH";
        public static final String CUSTOMER_NAME = "Tên KH";
        public static final String USER_ACTIVE = "User kích hoạt";
        public static final String BIRTH_DAY = "Ngày sinh";
        public static final String CONTRACT_NO = "Số hợp đồng";
        public static final String ACTIVE_CHANNEL = "Kênh kích hoạt";
        public static final String MODIFIED_DATE = "Ngày cập nhật";
        public static final String ID_TYPE = "Loại giấy tờ";
        public static final String ID_NO = "Số giấy tờ";
        public static final String ID_ISSUE_DATE = "Ngày cấp";
        public static final String CUSTOMER_TYPE = "Loại KH";
        public static final String STATUS = "Trạng thái KH";
        public static final String APPROVE_STATUS = "Trạng thái kiểm duyệt";
        public static final String DOC_UPDATE_STATUS = "Trạng thái cập nhật hồ sơ";
        public static final String AUDIT_STATUS = "Trạng thái hậu kiểm";
        public static final String AUDIT_REASON = "Lý do hậu kiểm";
        public static final String ASSIGN_STATUS = "Trạng thái phân công";
        public static final String ASSIGN_USER_NAME = "Người kiểm duyệt";
        public static final String CREATED_DATE = "Ngày kích hoạt";
        public static final String PHONE_NUMBER = "Số thuê bao";

        public static final String EXCEL_SHEET_NAME_FOR_APPROVE = "Danh sách kiểm duyệt";
        public static final String EXCEL_SHEET_NAME_FOR_APPROVE_STAFF = "Danh sách kiểm duyệt (CSKH)";
        public static final String DATE_TIME_FORMATER_EXPORT_EXCEL_ADMIN = "dd/MM/yyyy";


    }

    public static class ProviderAreaCode {
        public static final String HN = "HN001";
        public static final String HCM = "HCM001";
    }

    public static class ContractType {
        public static final Integer BBXN = 1;
        public static final Integer HD = 2;
    }

    public static class SearchSubscriber {
        public static final String SEND_MESSAGE = "Thực hiện gửi tin nhắn";
        public static final String DONT_SEND_MESSAGE = "Không thực hiện gửi tin nhắn";
        public static final String HOUR = "giờ";
        public static final String DAY = "ngày";
        public static final String MONTH = "tháng";
        public static final String WEEK = "tuần";
        public static final String REG_PACKAGE_SUCCESS = "Đăng ký gói cước thành công";
        public static final String DEL_PACKAGE_SUCCESS = "Hủy gói cước thành công";
        public static final String REG_PACKAGE = "REGISTER_PACKAGE";
        public static final String DEL_PACKAGE = "CANCEL_PACKAGE";
        public static final String LOCK_ONE = "BLOCK_1_WAY";
        public static final String LOCK_TWO = "BLOCK_2_WAY";
        public static final String UNLOCK_ONE = "OPEN_1_WAY";
        public static final String UNLOCK_TWO = "OPEN_2_WAY";
        public static final String REG_PACKAGE_DESCRIPTION = "Đăng ký gói cước %s (Mã gói cước: %s)";
        public static final String DEL_PACKAGE_DESCRIPTION = "Hủy gói cước %s (Mã gói cước: %s)";
        public static final String KHYC = "KHYC";
        public static final String MOBI_TYPE = "MC";
        public static final String SUCCESS = "Success";
        public static final String MBF_RESPONSE_CODE_SUCCESS = "0000";
        public static final String OPEN_ACTION = "OPEN_ACTION";
        public static final String BLOCK_ACTION = "BLOCK_ACTION";
        public static final Map<String, String> LOCK_UNLOCK_SUCCESS_MAP = Map.of(
            SearchSubscriber.LOCK_ONE, "Chặn 1 chiều thành công",
            SearchSubscriber.LOCK_TWO, "Chặn 2 chiều thành công",
            SearchSubscriber.UNLOCK_ONE, "Mở 1 chiều thành công",
            SearchSubscriber.UNLOCK_TWO, "Mở 2 chiều thành công"
        );

        public static final String STATUS_TRUE = "Đang hoạt động";
        public static final String STATUS_FALSE = "Đã hủy";
        public static final String APP_STATUS_TRUE = "Đã đăng kí";
        public static final String APP_STATUS_FALSE = "Chưa đăng kí";
        public static final String STATUS_UPDATE_DOC_TRUE = "Đã cập nhật";
        public static final String STATUS_UPDATE_DOC_FALSE = "Chưa cập nhật";
        public static final String ACTIVE_STATUS_O = "Không bị chặn";
        public static final String ACTIVE_STATUS_B1 = "Chặn 1 chiều";
        public static final String ACTIVE_STATUS_B2 = "Chặn 2 chiều";


        public static final String SEND_MESSAGE_FAIL = ". Gửi tin nhắn thất bại do lỗi hệ thống";
        public static final String OTHER = "OTHER";
        public static final Map<String, String> LOCK_UNLOCK_OPTION_MAP = Map.of(
            SearchSubscriber.LOCK_ONE, "1",
            SearchSubscriber.LOCK_TWO, "2",
            SearchSubscriber.UNLOCK_ONE, "1",
            SearchSubscriber.UNLOCK_TWO, "2"
        );
        public static final Map<String, Integer> LOCK_UNLOCK_STATUS_UPDATE = Map.of(
            SearchSubscriber.LOCK_ONE, 10,
            SearchSubscriber.LOCK_TWO, 20,
            SearchSubscriber.UNLOCK_ONE, 1,
            SearchSubscriber.UNLOCK_TWO, 1
        );
        public static final String SUB_CHARGE_PACKAGE = "SUB_CHARGE_PACKAGE";
        public static final String ACTIVE_STATUS = "ACTIVE_STATUS";

        public static final String URL_QRCODE = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=";

        public static final String BLOCK_2_WAY_TRUE = "1";
        public static final String BLOCK_2_WAY_FALSE = "0";
        public static final String ZONE_NAME_FIELD = "zone";
        public static final String SHEET_NAME = "Tra cứu thuê bao";
        public static final String TYPE_IMAGE_ID = "1";
        public static final String TYPE_IMAGE_CONTRACT = "2";
        public static final String TYPE_IMAGE_REGULATION = "3";
        public static final String ALL = "ALL";
        public static final String CODE_FRONT_ID = "1";
        public static final String CODE_BACK_ID = "2";
        public static final String CODE_PORTRAIT = "3";
        public static final Integer CRITERION_STATUS_PASSED = 1;
        public static final Integer CRITERION_STATUS_FAILED = 0;

    }

    public static class C06_CHECK {
        public static final Map<String, ErrorSubcriberKey> C06_CHECK_MAP = Map.of(
            "document", ErrorSubcriberKey.MISSING_ID_TYPE_FOR_C06,
            "birthday", ErrorSubcriberKey.MISSING_BIRTHDAY_FOR_C06,
            "name", ErrorSubcriberKey.MISSING_FULL_NAME_FOR_C06,
            "id", ErrorSubcriberKey.MISSING_ID_NO_FOR_C06,
            "id_ekyc", ErrorSubcriberKey.MISSING_ID_EKYC
        );
        public static final String DOCUMENT = "document";
        public static final String BIRTHDAY = "birthday";
        public static final String NAME = "name";
        public static final String ID = "id";
        public static final String ID_EKYC = "id_ekyc";
    }

    public static class SubscriberActiveStatus {
        public static final Integer OPEN = 1;
        public static final List<Integer> CLOSE_1_WAY = new ArrayList<>(Arrays.asList(10, 11));
        public static final List<Integer> CLOSE_2_WAY = new ArrayList<>(Arrays.asList(20, 21));
        public static final List<Integer> NOT_CLOSE_2_WAY = new ArrayList<>(Arrays.asList(1, 10, 11));
    }
    public static class ActiveChannel {
        public static String WEB_CRM = "02";
        public static String MOBILE_APP = "01";
    }

    public static class FillTextListSubsActiveReq {
        public static final String CUSTOMER_TYPE = "Cá nhân";
        public static final String NATIONALITY = "Việt Nam";
        public static final String CONTRIBUTOR = "VNSKY";
        public static final String ASSIGNED = "Đã phân công";
        public static final String NOT_ASSIGNED = "Chưa phân công";
    }

    public static class ProcessFileForSyncInfo {
        public static final String LOWER_CASE_PDF_EXTENSION = ".pdf";
        public static final String DECREE_13_CONTAINER = "ND13-";
        public static final String TEMPORARY_SYNC_CONTRACT_PNG = "sync";
    }

    public static class SyncInfoMode {
        public static final String SYNC_INFO_AGAIN_IF_FAIL = "1";
        public static final String SYNC_INFO_NEED_GEN_PDF = "2";
    }

    public static class StatusToTextExportExcel {
        public static final String TEXT_NOT_ASSIGN_YET = "Chưa phân công";
        public static final String TEXT_ASSIGNED = "Đã phân công";

        public static final String TEXT_NOT_UPDATE_DOC_YET = "Chưa cập nhật";
        public static final String TEXT_UPDATED_DOC = "Đã cập nhật";

        public static final String TEXT_NOT_AUDIT_YET = "Chưa hậu kiểm";
        public static final String TEXT_AUDITED = "Đã hậu kiểm";

        public static class EKYCError {
            public static final List<String> OCR_ERROR_LIST = Arrays.asList("03", "04");
            public static final List<String> FACE_CHECK_ERROR_LIST = Arrays.asList("03", "04", "05", "09");
        }







        public static final String TEXT_WAITING_FOR_APPROVE = "Chờ duyệt";
        public static final String TEXT_APRROVED = "Đã duyệt";
        public static final String TEXT_APPROVE_AGAIN = "Kiểm duyệt lại";
        public static final String TEXT_REQUIRE_UPDATE_IDENTIFIER = "Yêu cầu cập nhật giấy tờ";

        public static final String TEXT_ACTIVE = "Hoạt động";
        public static final String TEXT_NOT_ACTIVE = "Không hoạt động";

        public static final String NOT_AVAILABLE = "N/A";
    }
    public static class SendNotification {
        public static String ND13 = "ND13";
        public static String ND13_TITLE = "Ký nghị định 13 thành công";
    }
    public static final class Mod {
        public static Integer ACTIVE = 1;
        public static Integer SYN_DATA = 2;
    }

    public static final String EXCEPTION_HAD_MAPPING = "EXCEPTION HAD MAPPING";
    public static final Long MINUTE_TO_SECOND = 60L;
    public static final String TIMESTAMP_FOR_CHECK_FREQUENCY = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String CMND_EXPIRY_DATE = "31-12-2024";
    public static final String ISDN_PREFIX = "0";
    public static final String UPLOAD_CONTRACT_DECREE_13_FINAL_LOG = "upload contract decree 13 final to storage server ";
    public static final String GEN_CONTRACT_ERROR_LOG = "genContract error: ";
    public static final String ADD_DATA_CONTRACT_WITH_SIGNATURE_CUSTOMER_LOG = "add data contract with signature projectbase";
    public static final String DOWNLOAD_CONTRACT_TEMPLATE_FROM_STORAGE_SERVER_LOG = "download contract template from storage server ";
    public static final String DELELTE_CONTRACT_TMP_REDIS_AND_CONTRACT_WITH_DEVICE_TOKEN_LOG = "delete contract tmp redis and contract with token device ";
    public static final String SUCCESS_LOG = "successfully================ done";
    public static final String CLOSE_ERROR_LOG = "Close error";
    public static final String UPLOAD_CONTRACT_FINAL_TO_STOGARE_SERVER_LOG = "upload contract final to storage server ";
    public static final String GEN_CONTRACT_FROM_TEMPLATE_LOG = "gen contract from template";
    public static final String GET_CONTRACT_ERROR = "getContract error: ";

    public static class Reason {
        public static final String CODE = "code";
        public static final String NAME = "name";
    }
    public static class ChangeSimProcessStatus {
        public static final Integer PROCESSING = 1;
        public static final Integer DONE = 2;

    }

    public static class EmailContent{
        public static final String MAIL_SUBJECT_ESIM = "Thông báo đơn hàng VNSKY";
    }


    public static class PhoneNumber {
        public static final String ZERO = "0";
    }

    public static class TimeParam {
        public static final String FROM = "from";
        public static final String TO = "to";
    }

    public static final String C06_SUCCESS = "Xác thực thông tin thành công";
    public static final String VIETNAM = "Việt Nam";
    public static final String PERSONAL = "Cá nhân";

    public static class IntegrationServiceConstant {
        public static final String EKYC = "EKYC";
        public static final String MBF = "MBF";
        public static final String C06 = "C06";
        public static final String SEND_MESSAGE = "SEND_MESSAGE";
        public static final String CHANGE_ZONE = "CHANGE_ZONE";
        public static final String OCR = "OCR";
        public static final String INFO = "INFO";
        public static final String FACE_CHECK = "FACE_CHECK";
        public static final String ACTIVE_SUB = "ACTIVE_SUB";
        public static final String CONFIRM_OTP = "CONFIRM_OTP";
        public static final String SEND_OTP = "SEND_OTP";
        public static final String CHANGE_SIM = "CHANGE_SIM";
        public static final String MODIFY_INFO = "MODIFY_INFO";
        public static final String REG_DEL_PACKAGE = "REG_DEL_PACKAGE";
        public static final String OCS = "OCS";
        public static final String PACKAGE_CAPACITY = "PACKAGE_CAPACITY";
        public static final String CHECK_SUB_PROM_DIGILIFE = "CHECK_SUB_PROM_DIGILIFE";
    }

    public static final LocalDate YEAR_2000 = LocalDate.of(2000, 1, 1);

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CommonSymbol {
        public static final String FORWARD_SLASH = "/";
        public static final String COMMA = ",";
        public static final String PERCENT = "%";
        public static final String ASTERISK = "*";
        public static final String DOT = ".";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MinioDir{
        public static final String ISDN_TRANSACTION = "ISDN_TRANSACTION";
        public static final String PARTNER_FILE = "PARTNER_FILE/";
        public static final String ATTACHMENTS = "ATTACHMENTS";
        public static final String BATCH_PACKAGE_SALE = "BATCH_PACKAGE_SALE";

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class ActiveSubscriber{
            public static final String FOLDER_URL = "active-subscriber";

            public static final String TEMP_FOLDER = FOLDER_URL + "/temp";
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class UpdateInformation{
            public static final String FOLDER_URL = "update-information";

            public static final String TEMP_FOLDER = FOLDER_URL + "/temp";
        }
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class PackageProfile{
            public static final String FOLDER_URL = "image-package-profile";
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class UserSignature{
            public static final String FOLDER_URL = "USER_SIGNATURE";

            public static String buildSignatureUrl(@NotNull String userId){
                return FOLDER_URL + "/" + userId + ".png";
            }
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class BatchPackageSale{
            public static final String INPUT_FOLDER = BATCH_PACKAGE_SALE + "/input";
            public static final String RESULTS_FOLDER = BATCH_PACKAGE_SALE + "/results";
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Status {
        public static final Integer ACTIVE = 1;
        public static final Integer IN_ACTIVE = 0;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OrgType {
        public static final String NBO = "NBO";
        public static final String PARTNER = "PARTNER";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ApprovalStatus {
        public static final Integer WAITING_APPROVE = 1;
        public static final Integer IN_APPROVE = 2;
        public static final Integer APPROVED = 3;
        public static final Integer REJECTED = 4;
        public static final Integer CANCEL = 5;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SaleOrder {

        @UtilityClass
        public class Type {
            public static final int PARTNER = 1;
            public static final int ONLINE = 2;
            public static final int CUS = 3;
            public static final int BUY_PACKAGE = 4;
        }

        @UtilityClass
        public class PaymentOption {
            public static final Integer CASH = 1;
            public static final Integer TRANFER = 2;
            public static final Integer DEBT = 3;

        }

        @UtilityClass
        public class DeliveryStatus {
            public static final int TAO_MOI = 0;
            public static final int DA_GIAO_HANG = 3;
            public static final int CHO_LAY_HANG = 1;
            public static final int DANG_GIAO_HANG = 2;
            public static final int GIAO_HANG_THAT_BAI = 4;
            public static final int DANG_HOAN_HANG = 5;
            public static final int DA_HOAN_HANG = 6;
            public static final int HUY = 7;
            public static final int CRAFT_KIT_ERROR = 8;
            public static final int DANG_HUY = 9;
        }

        @UtilityClass
        public class DeliveryMethod {
            public static final String CUS_PICKUP = "CUS_PICKUP";
            public static final String AT_STORE = "STORE";
            public static final String EXPRESS = "EXPRESS";
            public static final String FAST = "FAST";
        }

        @UtilityClass
        public class PaymentMethod {
            public static final String COD = "COD";
            public static final String VNPAY = "VNPAY";
        }

        @UtilityClass
        public class KitStatus {
            /** Kit not yet used */
            public static final int NOT_USED = 1;

            /** Kit is being assembled */
            public static final int ASSEMBLING = 2;

            /** Kit has been assembled */
            public static final int ASSEMBLED = 3;

            /** Kit has been activated */
            public static final int ACTIVATED = 4;

            /** Kit has been cancelled */
            public static final int CANCELLED = 5;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AuditTarget {
        public static final String PROMOTION_PROGRAM_CATALOG = "PROMOTION_CATALOG";
        public static final String DELIVERY_PROMOTION = "DELIVERY_PROMOTION";
        public static final String PARTNER_USER = "PARTNER_USER";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UploadFile {
        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class SalePackageBatch {
            public static final String SUCCESS = "Thành công";
            public static final String FAIL = "Thất bại";
        }

        public static final String PHONE_NUMBER_NOT_FOUND = "Số điện thoại không được để trống; ";
        public static final String PACKAGE_CODE_NOT_FOUND = "Mã gói cước không được để trống; ";
        public static final String PACKAGE_NOT_FOUND = "Gói cước không tồn tại; ";
        public static final String INVALID_PHONE_NUMBER = "Số thuê bao không đúng định dạng; ";
        public static final String PACKAGE_NOT_EXIST = "Gói cước không tồn tại; ";

        public static final Pattern PHONE_PATTERN = Pattern.compile("^(84\\d{9}|0\\d{9}|\\d{9})$");

        public static final List<String> HEADER_FILE_SALE_PACKAGE = Arrays.asList("Số thuê bao", "Mã gói cước");
    }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TableNameConstant {
        public static final String ISDN_TRANSACTION = "ISDN_TRANSACTION";
        public static final String STOCK_ISDN_ORG = "STOCK_ISDN_ORG";
        public static final String STOCK_ISDN = "STOCK_ISDN";
        public static final String COMBINE_KIT = "COMBINE_KIT";
        public static final String REASON = "REASON";
        public static final String SALE_ORDER = "SALE_ORDER";
        public static final String STOCK_PRODUCT_UPLOAD_ORDER = "STOCK_PRODUCT_UPLOAD_ORDER";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FixedListName {
        public static final String FIELD_STATUS = "fieldStatus";
        public static final String APPROVAL_STATUS = "approvalStatus";
        public static final String TRANSACTION_STATUS = "transactionStatus";
        public static final String TRANSACTION_TYPE = "transactionType";
        public static final String PROCESS_TYPE = "processType";
        public static final String UPLOAD_STATUS = "uploadStatus";
        public static final String TRANSFER_STATUS = "transferStatus";
        public static final String STOCK_STATUS = "status";
        public static final String MOVE_TYPE = "MoveType";
        public static final String NUMBER_STATUS = "numberStatus";
    }

    public static class NumberProcessFile {

        private static final DateTimeFormatter FILE_TIMESTAMP_FMT = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

        public static final Pattern NUMBER_REGEX = Pattern.compile("^84.{14}$");

        public static String attachmentAtIndex(int index) {
            return String.format("attachments[%s]", index);
        }

        public static String getCheckFileName() {
            LocalDateTime now = LocalDateTime.now();
            return String.format("Ket_qua_kiem_tra_%s.xlsx", FILE_TIMESTAMP_FMT.format(now));
        }

        public static String getSampleFileName(NumberTransactionType numberTransactionType) {
            return getSampleFileName(numberTransactionType, ".xlsx");
        }

        public static String getSampleFileName(NumberTransactionType numberTransactionType, String extension) {
            return numberTransactionType.getUnaccentDescription() + extension;
        }
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProcessCode {
        public static final String UPLOAD_NUMBER = "UPLOAD_NUMBER";
        public static final String TRANSFER_NUMBER_OTHER = "TRANSFER_WAREHOUSE";
        public static final String DISTRIBUTE_NUMBER = "NUMBER_DISTRIBUTION";
        public static final String EXPORT_NUMBER_FOR_PARTNER = "EXPORT_NUMBER_FOR_PARTNER";
        public static final String BACK_NUMBER = "BACK_NUMBER";
        public static final String STOCK_PRODUCT_UPLOAD_ORDER = "SERIAL_UPLOAD_ORDER";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ActiveStatusSub {
        public static final String ACTIVE = "Bình thường";
        public static final String BLOCK_OUTGOING_BY_REQUEST = "Chặn một chiều theo yêu cầu";
        public static final String BLOCK_BOTH_BY_REQUEST = "Chặn hai chiều theo yêu cầu";
        public static final String BLOCK_OUTGOING_BY_OPERATOR = "Chặn một chiều do nhà mạng";
        public static final String BLOCK_BOTH_BY_OPERATOR = "Chặn hai chiều do nhà mạng";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class StatusSub {
        public static final String IN_STOCK = "Trong kho";
        public static final String SOLD = "Đã bán";
        public static final String UPDATE_INFO = "Đã cập nhật TTTB";
        public static final String CALL_900 = "Đã gọi 900";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FileUpload{
        public static final String REGEX = "^84.{14}$";
        public static final List<String> HEADER_FILE_STOCK_IN = List.of("Mã sản phẩm", "Serial đầu", "Serial cuối", "Số lượng");
        public static final List<String> HEADER_FILE_STOCK_OUT = List.of("Mã sản phẩm", "Số lượng", "Serial đầu");
        public static final Pattern pattern = Pattern.compile(REGEX);
        public static final String PRODUCT_NOT_EXISTED = "Mã sản phẩm không tồn tại;";
        public static final String INVALID_SERIAL = "Serial không hợp lệ;";
        public static final String FROM_SERIAL_MUST_NOT_GREATER_THAN_TO_SERIAL = "Serial cuối không được nhỏ hơn serial đầu;";
        public static final String SERIAL_NOT_REQUIRED = "Không cần Serial;";
        public static final String INVALID_QUANTITY = "Số lượng không hợp lệ;";
        public static final String FORMAT_NUMBER = "%.0f";
        public static final String SUCESS = "Hợp lệ";
        public static final String SERIAL_EXISTED = "Serial đã tồn tại;";
        public static final String SERIAL_NOT_EXISTED = "Serial không tồn tại;";
        public static final String INVALID_PARAM = "Tham số không hợp lệ;";
        public static final String CATEGORY_INVALID = "Loại sản phẩm không hợp lệ;";
        public static final List<String> HEADER_FILE_SALE_PACKAGE = List.of("Số thuê bao", "Serial SIM", "Mã gói cước");
        public static final String PACKAGE_NOT_FOUND = "Không tìm thấy thông tin gói cước;";
        public static final String INVALID_PHONE_NUMBER = "Số thuê bao không hợp lệ;";
        public static final String PHONE_NUMBER_NOT_EXISTS = "Số thuê bao không tồn tại;";
        public static final String PACKAGE_NOT_EXIST = "Mã gói cước không tồn tại;";
        public static final String PHONE_NUMBER_NOT_FOUND = "Số thuê bao không được bỏ trống;";
        public static final String PACKAGE_CODE_NOT_FOUND = "Mã gói cước không được bỏ trống;";
        public static final String SERIAL_NOT_FOUND = "Serial không được bỏ trống;";
        public static final String TOPUP_PACKAGE_TRANSACTION_DIR = "TOPUP_PACKAGE_TRANACTION_DIR";

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static class SalePackageBatch{
            public static final String SUCCESS = "Thành công";
            public static final String FAIL = "Thất bại";
        }

    }
}

package com.vnsky.bcss.projectbase.shared.utils;

import com.vnsky.bcss.projectbase.shared.constant.IntegrationConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RegisterPackageErrorHandler {

    private static final Map<String, String> ERROR_MESSAGES = new HashMap<>();

    static {
        ERROR_MESSAGES.put(IntegrationConstant.SUCCESS_CODE, "Xử lý thành công");
        ERROR_MESSAGES.put(IntegrationConstant.COMMAND_NOT_EXIST_CODE, "Lệnh thực hiện không tồn tại hoặc không được cấu hình trong DB");
        ERROR_MESSAGES.put(IntegrationConstant.MISSING_SUBSCRIBER_TYPE_CODE, "Thiếu thông tin của loại thuê bao");
        ERROR_MESSAGES.put(IntegrationConstant.MISSING_SUBSCRIBER_NUMBER_CODE, "Thiếu thông tin của số thuê bao");
        ERROR_MESSAGES.put(IntegrationConstant.MISSING_SHOP_TYPE_CODE, "Thiếu thông tin của loại cửa hàng");
        ERROR_MESSAGES.put(IntegrationConstant.MISSING_EMPLOYEE_CODE, "Thiếu thông tin của nhân viên");
        ERROR_MESSAGES.put(IntegrationConstant.MISSING_REASON_CODE, "Thiếu thông tin của mã lý do");
        ERROR_MESSAGES.put(IntegrationConstant.INVALID_NUMBER_FORMAT_CODE, "Sai định dạng số");
        ERROR_MESSAGES.put(IntegrationConstant.INVALID_MOBI_SUB_TYPE_CODE, "Trường strMobiSubType chưa đúng. Nhập 1 trong 2 giá trị MC hoặc MF");
        ERROR_MESSAGES.put(IntegrationConstant.MISSING_PACKAGE_CODE, "Thiếu thông tin gói cước");
        ERROR_MESSAGES.put(IntegrationConstant.REGISTER_CANCEL_SAME_TIME_CODE, "Bạn không thể đăng ký và hủy gói cước trong một lần thực hiện");
        ERROR_MESSAGES.put(IntegrationConstant.SUBSCRIBER_NOT_FOUND_CODE, "Không tồn tại thông tin chi tiết thuê bao có SUB_ID: %s");
        ERROR_MESSAGES.put(IntegrationConstant.INVALID_PROMOTION_CODE, "Mã khuyến mãi không đúng hoặc đã hết hiệu lực");
        ERROR_MESSAGES.put(IntegrationConstant.PROMOTION_ALREADY_REGISTERED_CODE, "Thuê bao %s đã đăng ký khuyến mãi %s");
        ERROR_MESSAGES.put(IntegrationConstant.PROMOTION_REQUIRES_IMEI_CODE, "Khuyến mại này tặng kèm máy, bạn cần nhập số IMEI");
        ERROR_MESSAGES.put(IntegrationConstant.INVALID_IMEI_CODE, "Số imei của device không hợp lệ");
        ERROR_MESSAGES.put(IntegrationConstant.PROMOTION_NOT_REGISTERED_CODE, "Thuê bao %s chưa đăng ký khuyến mãi %s");
        ERROR_MESSAGES.put(IntegrationConstant.PACKAGE_NOT_EXIST_CODE, "Gói cước %s không tồn tại hoặc hết hiệu lực");
        ERROR_MESSAGES.put(IntegrationConstant.PACKAGE_ALREADY_REGISTERED_CODE, "Gói cước %s đã được đăng kí!");
        ERROR_MESSAGES.put(IntegrationConstant.PACKAGE_NOT_REGISTERED_CODE, "Thuê bao %s chưa đăng ký gói cước %s");
        ERROR_MESSAGES.put(IntegrationConstant.INVALID_PROMOTION_OR_EXPIRED_CODE, "Mã KM không đúng hoặc CTKM đã hết hiệu lực");
        ERROR_MESSAGES.put(IntegrationConstant.PROMOTION_COMMITMENT_NOT_EXPIRED_CODE, "Prom_program_code : %s Chưa hết thời gian cam kết");
        ERROR_MESSAGES.put(IntegrationConstant.SUBSCRIBER_INACTIVE_CODE, "Thuê bao %s đang không hoạt động");
        ERROR_MESSAGES.put(IntegrationConstant.PACKAGE_REGISTRATION_FAILED_CODE, "Đăng ký gói cước thất bại: %s");
        ERROR_MESSAGES.put(IntegrationConstant.INFO_CHANGE_SUCCESS_PACKAGE_FAILED_CODE, "Thay đổi thông tin thành công. Đăng ký gói cước thất bại: %s");
        ERROR_MESSAGES.put(IntegrationConstant.SYSTEM_ERROR_CODE, "Lỗi từ hệ thống: %s");
        ERROR_MESSAGES.put(IntegrationConstant.POSTPAID_SUBSCRIBER_CHANGE_ERROR_CODE, "Lỗi trong khi thay đổi thông tin thuê bao trả sau: %s");
    }

    public static String getErrorMessage(String errorCode) {
        return ERROR_MESSAGES.getOrDefault(errorCode, "Lỗi không xác định với mã: " + errorCode);
    }

    public static String getErrorMessage(String errorCode, String... parameters) {
        String message = getErrorMessage(errorCode);
        if (parameters != null) {
            for (String param : parameters) {
                message = message.replaceFirst("%s", param);
            }
        }
        return message;
    }

    public static boolean isRetryableError(String errorCode) {
        return IntegrationConstant.SYSTEM_ERROR_CODE.equals(errorCode) ||
               IntegrationConstant.PACKAGE_REGISTRATION_FAILED_CODE.equals(errorCode) ||
               IntegrationConstant.INFO_CHANGE_SUCCESS_PACKAGE_FAILED_CODE.equals(errorCode);
    }

    public static boolean isSuccess(String errorCode) {
        return IntegrationConstant.SUCCESS_CODE.equals(errorCode);
    }

    public static void logError(String errorCode, String serial, String packageCode, String description) {
        log.error("Register package failed for serial: {}, package: {}, errorCode: {}, description: {}",
                 serial, packageCode, errorCode, description);
    }
}

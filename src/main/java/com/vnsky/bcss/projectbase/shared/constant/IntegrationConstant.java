package com.vnsky.bcss.projectbase.shared.constant;

public final class IntegrationConstant {

    private IntegrationConstant() {
        // Private constructor to prevent instantiation
    }

    // Integration Commands
    public static final String MBF_CMD = "MBF";
    public static final String BOOK_ESIM_TYPE = "BOOK_ESIM";
    public static final String CRAFT_KIT_TYPE = "CRAFT_KIT";
    public static final String REGISTER_PACKAGE_TYPE = "REG_DEL_PACKAGE";
    public static final String CHECK_ISDN_TYPE = "CHECK_ISDN";

    // eSIM Commands
    public static final String ESIM_REGISTER_COMMAND = "API_REG_DEL_PROM_PACKAGE";

    // Response Codes
    public static final String SUCCESS_CRAFT_KIT_CODE = "S001";

    public static final String SUCCESS_CODE = "0000";
    public static final String COMMAND_NOT_EXIST_CODE = "9999";
    public static final String MISSING_SUBSCRIBER_TYPE_CODE = "2008";
    public static final String MISSING_SUBSCRIBER_NUMBER_CODE = "2001";
    public static final String MISSING_SHOP_TYPE_CODE = "2018";
    public static final String MISSING_EMPLOYEE_CODE = "2019";
    public static final String MISSING_REASON_CODE = "2020";
    public static final String INVALID_NUMBER_FORMAT_CODE = "2010";
    public static final String INVALID_MOBI_SUB_TYPE_CODE = "2009";
    public static final String MISSING_PACKAGE_CODE = "2067";
    public static final String REGISTER_CANCEL_SAME_TIME_CODE = "1260";
    public static final String SUBSCRIBER_NOT_FOUND_CODE = "355";
    public static final String INVALID_PROMOTION_CODE = "2072";
    public static final String PROMOTION_ALREADY_REGISTERED_CODE = "2070";
    public static final String PROMOTION_REQUIRES_IMEI_CODE = "2073";
    public static final String INVALID_IMEI_CODE = "289";
    public static final String PROMOTION_NOT_REGISTERED_CODE = "2071";
    public static final String PACKAGE_NOT_EXIST_CODE = "2063";
    public static final String PACKAGE_ALREADY_REGISTERED_CODE = "2064";
    public static final String PACKAGE_NOT_REGISTERED_CODE = "2065";
    public static final String INVALID_PROMOTION_OR_EXPIRED_CODE = "122";
    public static final String PROMOTION_COMMITMENT_NOT_EXPIRED_CODE = "3040";
    public static final String SUBSCRIBER_INACTIVE_CODE = "2021";
    public static final String PACKAGE_REGISTRATION_FAILED_CODE = "2059";
    public static final String INFO_CHANGE_SUCCESS_PACKAGE_FAILED_CODE = "1264";
    public static final String SYSTEM_ERROR_CODE = "1049";
    public static final String POSTPAID_SUBSCRIBER_CHANGE_ERROR_CODE = "450";
}

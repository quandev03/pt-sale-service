package com.vnsky.bcss.projectbase.shared.constant;

public final class EsimBookingConstant {

    private EsimBookingConstant() {
        // Private constructor to prevent instantiation
    }

    // Sale Order Types
    public static final int BOOK_ESIM = 1;

    // Sale Order Book eSIM Status
    public static final int PROCESSING = 1;
    public static final int DONE = 2;

    // EsimRegistrationLine Status
    public static final int BOOK_ESIM_FAILED = 2;
    public static final int CRAFT_KIT_FAILED = 3;
    public static final int REG_PACKAGE_FAILED = 4;
    public static final int SUCCESS = 1;
} 
package com.vnsky.bcss.projectbase.shared.constant;

public final class SaleOrderConstant {

    private SaleOrderConstant() {
        // Private constructor to prevent instantiation
    }

    // Sale Order Types
    public static final int BOOK_ESIM = 0;
    public static final int BATCH_PACKAGE = 1;

    // Sale Order Book eSIM Status
    public static final int PROCESSING = 1;
    public static final int DONE = 2;

    // EsimRegistrationLine Status
    public static final int BOOK_ESIM_FAILED = 2;
    public static final int CRAFT_KIT_FAILED = 3;
    public static final int REG_INFO_FAILED = 4;
    public static final int BOOK_ESIM_SUCCESS = 1;
}

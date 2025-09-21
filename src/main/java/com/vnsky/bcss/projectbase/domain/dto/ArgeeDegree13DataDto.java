package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.pdf.FillDataPdf;
import lombok.Data;

@Data
public class ArgeeDegree13DataDto {
    private String code;

    @FillDataPdf(keyFill = "day")
    private String day;

    @FillDataPdf(keyFill = "month")
    private String month;

    @FillDataPdf(keyFill = "year")
    private String year;

    @FillDataPdf(keyFill = "customer_name")
    private String fullName;

    @FillDataPdf(keyFill = "strDK1")
    private String strDK1;

    @FillDataPdf(keyFill = "strDK2")
    private String strDK2;

    @FillDataPdf(keyFill = "strDK3")
    private String strDK3;

    @FillDataPdf(keyFill = "strDK4")
    private String strDK4;

    @FillDataPdf(keyFill = "strDK5")
    private String strDK5;

    @FillDataPdf(keyFill = "strDK6")
    private String strDK6;

    @FillDataPdf(keyFill = "checkDK1")
    private String agreeDk1;

    @FillDataPdf(keyFill = "checkDK2")
    private String agreeDk2;

    @FillDataPdf(keyFill = "checkDK3")
    private String agreeDk3;

    @FillDataPdf(keyFill = "checkDK4")
    private String agreeDk4;

    @FillDataPdf(keyFill = "checkDK5")
    private String agreeDk5;

    @FillDataPdf(keyFill = "checkDK6")
    private String agreeDk6;

    @FillDataPdf(keyFill = "signature_customer", image = true, maxWidth = 1000)
    private byte[] signatureCustomer;
}


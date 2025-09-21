package com.vnsky.bcss.projectbase.domain.port.secondary.external.mail;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class MailInfoDTO {

    private String requester;

    @NotBlank
    private String to;

    private String subject;

    private String orderNo;

    private String note;

    private String time;

    private String amount;

    private String name;

    private String isdn;

    private String serial;

    private String urlQR;

    private String orderDate;

    private List<FileCid> imageCids;

    private String amountProduct;

    private String amountDiscount;

    private String amountTotal;

    private String payStatus;

    private List<OrderInfo> orderInfos;

    private List<ESimInfo> eSimInfos;

    private String linkBackground;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class FileCid {
        private String contentId;
        private String path;
        private String contentType;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class OrderInfo {
        private String isdn;
        private String simType;
        private String packageCode;
        private String expiryDate;
        private String price;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ESimInfo {
        private String url;
        private String isdn;
        private String serial;
        private String name;
        private String highSpeed;
        private String coverageRange;
        private String usingDay;
        private String lpa;
    }
}

package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchPackageSaleDTO implements Serializable {

    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("FILE_URL")
    private String fileUrl;

    @DbColumnMapper("RESULT_FILE_URL")
    private String resultFileUrl;

    @DbColumnMapper("TOTAL_NUMBER")
    private Long totalNumber;

    @DbColumnMapper("FAILED_NUMBER")
    private Long failedNumber;

    @DbColumnMapper("SUCCEEDED_NUMBER")
    private Long succeededNumber;

    @DbColumnMapper("PAYMENT_TYPE")
    private String paymentType;

    @DbColumnMapper("STATUS")
    private Integer status;

    @DbColumnMapper("FILE_NAME")
    private String fileName;

    @DbColumnMapper("CLIENT_ID")
    private String clientId;

    @DbColumnMapper("TYPE")
    private Integer type;

    @DbColumnMapper("ORDER_ID")
    private String orderId;

    @DbColumnMapper("CREATED_DATE")
    private LocalDateTime createdDate;

    @DbColumnMapper("CREATED_BY")
    private String createdBy;

    @DbColumnMapper("PCK_CODE")
    private String pckCode;

    @DbColumnMapper("ISDN")
    private String isdn;

    // Computed sale type: 1 if PCK_CODE and ISDN are not null; 2 if FILE_URL is not null
    @DbColumnMapper("SALE_TYPE")
    private Integer saleType;

    @DbColumnMapper("FINISHED_DATE")
    private LocalDateTime finishedDate;
}

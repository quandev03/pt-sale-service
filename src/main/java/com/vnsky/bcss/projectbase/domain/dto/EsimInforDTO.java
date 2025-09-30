package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EsimInforDTO {

    @DbColumnMapper("ISDN")
    private Long isdn;

    @DbColumnMapper("SERIAL")
    private String serial;

    @DbColumnMapper("PACK_CODE")
    private String packCode;

    @DbColumnMapper("ORDER_NO")
    private String orderNo;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("STATUS")
    private Integer status;

    @DbColumnMapper("STATUS_900")
    private Integer status900;

    @DbColumnMapper("ACTIVE_STATUS")
    private Integer activeStatus;

    @DbColumnMapper("MODIFIED_DATE")
    private LocalDateTime modifiedDate;

    @DbColumnMapper("GEN_QR_BY")
    private String genQrBy;

    @DbColumnMapper("SUB_ID")
    private String subId;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    @DbColumnMapper("STATUS_SUB")
    private int statusSub;

    @DbColumnMapper("LPA")
    private String lpaCode;

    @DbColumnMapper("CREATED_DATE")
    private LocalDateTime createdDate;
}

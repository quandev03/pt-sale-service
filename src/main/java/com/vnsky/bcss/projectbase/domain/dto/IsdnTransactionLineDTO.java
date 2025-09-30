package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.database.annotation.DbColumnMapper;
import com.vnsky.excel.annotation.XlsxColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@SuperBuilder
public class IsdnTransactionLineDTO {

    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("TRANS_DATE")
    private LocalDateTime transDate;

    @DbColumnMapper("ISDN_TRANS_ID")
    private String isdnTransId;

    @XlsxColumn(writeIndex = 0, header = "Số thuê bao")
    @DbColumnMapper("FROM_ISDN")
    private Long fromIsdn;

    @DbColumnMapper("TO_ISDN")
    private Long toIsdn;

    @DbColumnMapper("QUANTITY")
    private Integer quantity;

    @DbColumnMapper("STATUS")
    private Integer status;

    @DbColumnMapper("ERROR")
    private String error;

    @DbColumnMapper("DESCRIPTION")
    private String description;

    @DbColumnMapper("GROUP_CODE")
    @Schema(description = "Nhóm số")
    private String groupCode;

    private String groupName;

    private String isdnPattern;

    @DbColumnMapper("GENERAL_FORMAT")
    @Schema(description = "Số")
    private String generalFormat;

    @XlsxColumn(writeIndex = 1, header = "Kết quả")
    private String result;

}

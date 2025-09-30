package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.NumberTransactionStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.NumberUploadStatus;
import com.vnsky.database.annotation.AppPickListDomain;
import com.vnsky.database.annotation.AppPickListField;
import com.vnsky.database.annotation.AppPickListFixed;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@AppPickListDomain(table = "ISDN_TRANSACTION")
public class IsdnTransactionDTO extends CommonDTO {

    private String id;

    private LocalDateTime transDate;

//    private String stockId;
//
//    @Schema(hidden = true)
//    @AppPickListField(table = Constant.TableNameConstant.STOCK_ISDN_ORG, column = "STOCK_NAME", joinColumn = "ID", referencedColumn = "STOCK_ID")
//    private String stockName;
//
//    private Integer ieStockId;
//
//    @Schema(hidden = true)
//    @AppPickListField(table = Constant.TableNameConstant.STOCK_ISDN_ORG, column = "STOCK_NAME", joinColumn = "ID", referencedColumn = "IE_STOCK_ID")
//    private String ieStockName;

    private Integer transStatus = NumberTransactionStatus.PRE_START.getValue();

    @Schema(hidden = true)
    @AppPickListField(keyOption = "TRANS_STATUS")
    private String transStatusName;

//    private Integer transType;

//    @Schema(hidden = true)
//    @AppPickListField(keyOption = "TRANS_TYPE")
//    private String transTypeName;

    private Integer approvalStatus;

    @Schema(hidden = true)
    @AppPickListField(keyOption = "APPROVAL_STATUS")
    private String approvalStatusName;

    private Integer reasonId;

//    private Integer status;

    private Integer processType;

    @Schema(hidden = true)
    @AppPickListFixed(fieldName = Fields.processType, listKey = Constant.FixedListName.PROCESS_TYPE)
    private String processTypeName;

    @JsonRawValue
    private String metadata;

    private Integer totalNumber;

    private Integer failedNumber;

    private Integer succeededNumber;

    private String description;

    private Integer uploadStatus = NumberUploadStatus.PROCESSING.getValue();

    @Schema(hidden = true)
    @AppPickListFixed(fieldName = Fields.uploadStatus, listKey = Constant.FixedListName.UPLOAD_STATUS)
    private String uploadStatusName;

    private Integer validSucceededNumber;

    private Integer validFailedNumber;

//    private Integer moveType;

//    @Schema(hidden = true)
//    @AppPickListFixed(fieldName = Fields.moveType, listKey = Constant.FixedListName.MOVE_TYPE)
//    private String moveTypeName;

    @Schema(hidden = true)
    private FileInfoDTO resultCheckFile;

    @Schema(hidden = true)
    private FileInfoDTO resultFile;

//    private Integer productId;
//
//    private String productName;

    @AppPickListField(table = Constant.TableNameConstant.REASON, column = "REASON_CODE", joinColumn = "ID", referencedColumn = "REASON_ID")
    private String reasonCode;

    @AppPickListField(table = Constant.TableNameConstant.REASON, column = "REASON_NAME", joinColumn = "ID", referencedColumn = "REASON_ID")
    private String reasonName;

    @AppPickListField(table = Constant.TableNameConstant.REASON, column = "STATUS", joinColumn = "ID", referencedColumn = "REASON_ID")
    private Integer reasonStatus;

    private FileInfoDTO uploadFile;

    private String uploadFilename;

    @Schema
    private List<IsdnTransactionLineDTO> lines = new ArrayList<>();

    private Integer stepStatus;

    private Integer orderId;

    @AppPickListField(table = Constant.TableNameConstant.SALE_ORDER, column = "ORDER_NO", joinColumn = "ID", referencedColumn = "ORDER_ID")
    private String orderNo;

    private Integer quantity;

    private String clientId;
}

package com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateSubscriberDataMbfRequest {
    @Schema(description = "Số thuê bao")
    private Long strIsdn;

    @Schema(description = "Thông tin serial")
    private String strSerial;

    @Schema(description = "Imsi")
    private Long strImsi;

    @Schema(description = "Số hộ chiếu")
    private String strPasspost;

    @Schema(description = "Ngày cấp hộ chiếu")
    private String strPasspostIssueDate;

    @Schema(description = "Nơi cấp hộ chiếu")
    private String strPasspostIssuePlace;

    @Schema(description = "Danh sách các dịch vụ")
    private List<String> arrRegService;

    private List<List<String>> arrRegProm;

    private List<List<String>> arrImages;

    private String strSex;

    private String strNationality;

    private String strSubName;

    private String strBirthday;

    private String strSubType;

    private String strCustType;

    private String strReasonCode;

    private String strContractNumber;

    private String strActionFlag;

    private String strContactName;

    private String strAppObject;

    private Integer countNumber;

    @Override
    public String toString() {
        return "UpdateSubscriberDataMbfRequest{" +
            "strIsdn=" + strIsdn +
            ", strSerial='" + strSerial + '\'' +
            ", strImsi=" + strImsi +
            ", strPasspost='" + strPasspost + '\'' +
            ", strPasspostIssueDate='" + strPasspostIssueDate + '\'' +
            ", strPasspostIssuePlace='" + strPasspostIssuePlace + '\'' +
            ", arrRegService=" + arrRegService +
            ", arrRegProm=" + arrRegProm +
            ", strSex='" + strSex + '\'' +
            ", strNationality='" + strNationality + '\'' +
            ", strSubName='" + strSubName + '\'' +
            ", strBirthday='" + strBirthday + '\'' +
            ", strSubType='" + strSubType + '\'' +
            ", strCustType='" + strCustType + '\'' +
            ", strReasonCode='" + strReasonCode + '\'' +
            ", strContractNumber='" + strContractNumber + '\'' +
            ", strActionFlag='" + strActionFlag + '\'' +
            ", strContactName='" + strContactName + '\'' +
            ", strAppObject='" + strAppObject + '\'' +
            ", countNumber=" + countNumber +
            '}';
    }
}

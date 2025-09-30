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
    private String strIsdn;

    @Schema(description = "Thông tin serial")
    private String strSerial;

    @Schema(description = "Số hộ chiếu")
    private String strPasspost;

    @Schema(description = "Ngày cấp hộ chiếu")
    private String strPasspostIssueDate;

    @Schema(description = "Nơi cấp hộ chiếu")
    private String strPasspostIssuePlace;

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

    private String strProvince;

    private String strDistrict;

    private String strPrecinct;

    private String strHome;

    private String strImsi;

    private String strIdNo;

    private String strIdIssueDate;

    private String strIdIssuePlace;

    @Schema(description = "Ngôn ngữ: 1 - Tiếng Anh, 2 - Tiếng Việt")
    private Integer strLanguage;

    @Override
    public String toString() {
        return "UpdateSubscriberDataMbfRequest{" +
            "strIsdn=" + strIsdn +
            ", strSerial='" + strSerial + '\'' +
            ", strPasspost='" + strPasspost + '\'' +
            ", strPasspostIssueDate='" + strPasspostIssueDate + '\'' +
            ", strPasspostIssuePlace='" + strPasspostIssuePlace + '\'' +
            ", arrImages=" + (arrImages != null ? "[BASE64_IMAGES_EXCLUDED: " + arrImages.size() + " items]" : "null") +
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
            ", strProvince='" + strProvince + '\'' +
            ", strDistrict='" + strDistrict + '\'' +
            ", strPrecinct='" + strPrecinct + '\'' +
            ", strHome='" + strHome + '\'' +
            ", strImsi='" + strImsi + '\'' +
            ", strIdNo='" + strIdNo + '\'' +
            ", strIdIssueDate='" + strIdIssueDate + '\'' +
            ", strIdIssuePlace='" + strIdIssuePlace + '\'' +
            ", strLanguage=" + strLanguage +
            '}';
    }
}

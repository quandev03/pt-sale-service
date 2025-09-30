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
public class ModifyInfoCorpRequest {
    @Schema(description = "Gender")
    private String strSex;

    @Schema(description = "Nationality")
    private String strNationality;

    @Schema(description = "Subscriber name")
    private String strSubName;

    @Schema(description = "ID number")
    private String strIdNo;

    @Schema(description = "ID issue date")
    private String strIdIssueDate;

    @Schema(description = "ID issue place")
    private String strIdIssuePlace;

    @Schema(description = "Birthday")
    private String strBirthday;

    @Schema(description = "Province")
    private String strProvince;

    @Schema(description = "District")
    private String strDistrict;

    @Schema(description = "Precinct")
    private String strPrecinct;

    @Schema(description = "House number")
    private String strHome;

    @Schema(description = "Address")
    private String strAddress;

    @Schema(description = "Registration type")
    private String strRegType;

    @Schema(description = "Subscriber type")
    private String strSubType;

    @Schema(description = "Kit type")
    private String strKitType;

    @Schema(description = "Customer type")
    private String strCustType;

    @Schema(description = "Reason code")
    private String strReasonCode;

    @Schema(description = "Contract number")
    private String strContractNo;

    @Schema(description = "Application object")
    private String strAppObject;

    @Schema(description = "ISDN")
    private String strIsdn;

    @Schema(description = "Serial")
    private String strSerial;

    @Schema(description = "IMSI")
    private String strImsi;

    @Schema(description = "Sign date")
    private String strSignDate;

    @Schema(description = "Mobi type")
    private String strMobiType;

    @Schema(description = "Image array")
    private List<List<String>> arrImages;

    @Schema(description = "User subscriber name")
    private String strUserSubName;

    @Schema(description = "User birthday")
    private String strUserBirthday;

    @Schema(description = "User gender")
    private String strUserSex;

    @Schema(description = "User option")
    private String strUserOption;

    @Schema(description = "User ID or passport number")
    private String strUserIdOrPpNo;

    @Schema(description = "User ID or passport issue date")
    private String strUserIdOrPpIssueDate;

    @Schema(description = "User ID or passport issue place")
    private String strUserIdOrPpIssuePlace;

    @Schema(description = "User province")
    private String strUserProvince;

    @Schema(description = "User district")
    private String strUserDistrict;

    @Schema(description = "User precinct")
    private String strUserPrecinct;

    @Schema(description = "User street block name")
    private String strUserStreetBlockName;

    @Schema(description = "User street name")
    private String strUserStreetName;

    @Schema(description = "User house")
    private String strUserHome;

    @Schema(description = "Registration business")
    private String strRegBussiness;

    @Schema(description = "Founded permit number")
    private String strFoundedPermNo;

    @Schema(description = "Contact address")
    private String strContactAddress;

    @Schema(description = "Business permit number")
    private String strBusPermitNo;

    @Schema(description = "Contact name")
    private String strContactName;

    @Schema(description = "User nationality")
    private String strUserNationality;

    @Schema(description = "Founded permit date")
    private String strFoundedPermDate;

    @Schema(description = "TIN")
    private String strTin;

    @Schema(description = "Telephone")
    private String strTel;

    @Schema(description = "Option")
    private String strOption;

    @Schema(description = "Ngôn ngữ: 1 - Tiếng Anh, 2 - Tiếng Việt")
    private Integer strLanguage;

    @Override
    public String toString() {
        return "ModifyInfoCorpRequest{" +
            "strSex='" + strSex + '\'' +
            ", strNationality='" + strNationality + '\'' +
            ", strSubName='" + strSubName + '\'' +
            ", strIdNo='" + strIdNo + '\'' +
            ", strIdIssueDate='" + strIdIssueDate + '\'' +
            ", strIdIssuePlace='" + strIdIssuePlace + '\'' +
            ", strBirthday='" + strBirthday + '\'' +
            ", strProvince='" + strProvince + '\'' +
            ", strDistrict='" + strDistrict + '\'' +
            ", strPrecinct='" + strPrecinct + '\'' +
            ", strHome='" + strHome + '\'' +
            ", strAddress='" + strAddress + '\'' +
            ", strRegType='" + strRegType + '\'' +
            ", strSubType='" + strSubType + '\'' +
            ", strKitType='" + strKitType + '\'' +
            ", strCustType='" + strCustType + '\'' +
            ", strReasonCode='" + strReasonCode + '\'' +
            ", strContractNo='" + strContractNo + '\'' +
            ", strAppObject='" + strAppObject + '\'' +
            ", strIsdn='" + strIsdn + '\'' +
            ", strSerial='" + strSerial + '\'' +
            ", strImsi='" + strImsi + '\'' +
            ", strSignDate='" + strSignDate + '\'' +
            ", strMobiType='" + strMobiType + '\'' +
            ", arrImages=" + (arrImages != null ? "[BASE64_IMAGES_EXCLUDED: " + arrImages.size() + " items]" : "null") +
            ", strUserSubName='" + strUserSubName + '\'' +
            ", strUserBirthday='" + strUserBirthday + '\'' +
            ", strUserSex='" + strUserSex + '\'' +
            ", strUserOption='" + strUserOption + '\'' +
            ", strUserIdOrPpNo='" + strUserIdOrPpNo + '\'' +
            ", strUserIdOrPpIssueDate='" + strUserIdOrPpIssueDate + '\'' +
            ", strUserIdOrPpIssuePlace='" + strUserIdOrPpIssuePlace + '\'' +
            ", strUserProvince='" + strUserProvince + '\'' +
            ", strUserDistrict='" + strUserDistrict + '\'' +
            ", strUserPrecinct='" + strUserPrecinct + '\'' +
            ", strUserStreetBlockName='" + strUserStreetBlockName + '\'' +
            ", strUserStreetName='" + strUserStreetName + '\'' +
            ", strUserHome='" + strUserHome + '\'' +
            ", strRegBussiness='" + strRegBussiness + '\'' +
            ", strFoundedPermNo='" + strFoundedPermNo + '\'' +
            ", strContactAddress='" + strContactAddress + '\'' +
            ", strBusPermitNo='" + strBusPermitNo + '\'' +
            ", strContactName='" + strContactName + '\'' +
            ", strUserNationality='" + strUserNationality + '\'' +
            ", strFoundedPermDate='" + strFoundedPermDate + '\'' +
            ", strTin='" + strTin + '\'' +
            ", strTel='" + strTel + '\'' +
            ", strOption='" + strOption + '\'' +
            ", strLanguage=" + strLanguage +
            '}';
    }
}

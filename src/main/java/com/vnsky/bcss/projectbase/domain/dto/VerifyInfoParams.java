package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VerifyInfoParams {
    private String strSex;
    private String strNationality;
    private String strSubName;
    private String strIdNo;
    private String strIdIssueDate;
    private String strIdIssuePlace;
    private String strBirthday;
    private String strProvince;
    private String strDistrict;
    private String strPrecinct;
    private String strHome;
    private String strAddress;
    private String strRegType;
    private String strSubType;
    private String strKitType;
    private String strCustType;
    private String strReasonCode;
    private String strContractNo;
    private String strAppObject;
    private String strSignDate;
    private String strMobiType;
    private List<List<String>> arrImages;
    private String strUserSubName;
    private String strUserBirthday;
    private String strUserSex;
    private String strUserOption;
    private String strUserIdOrPpNo;
    private String strUserIdOrPpIssueDate;
    private String strUserIdOrPpIssuePlace;
    private String strUserProvince;
    private String strUserDistrict;
    private String strUserPrecinct;
    private String strUserStreetBlockName;
    private String strUserStreetName;
    private String strUserHome;
    private String strRegBussiness;
    private String strFoundedPermNo;
    private String strContactAddress;
    private String strBusPermitNo;
    private String strContactName;
    private String strUserNationality;
    private String strFoundedPermDate;
    private String strTin;
    private String strTel;
    private String strOption;
}

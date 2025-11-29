package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataOCRTwoSideField {
    @JsonProperty("address")
    private String address;
    @JsonProperty("addressconf")
    private String addressconf;
    @JsonProperty("birthday")
    private String birthday;
    @JsonProperty("characteristics")
    private String characteristics;
    @JsonProperty("charactoristics_conf")
    private String charactoristicsConf;
    @JsonProperty("copyright")
    private String copyright;
    @JsonProperty("country")
    private String country;
    @JsonProperty("district")
    private String district;
    @JsonProperty("document")
    private String document;
    @JsonProperty("ethnicity")
    private String ethnicity;
    @JsonProperty("ethnicityconf")
    private String ethnicityConf;
    @JsonProperty("expiry")
    private String expiry;
    @JsonProperty("expiryconf")
    private String expiryConf;
    @JsonProperty("hometown")
    private String hometown;
    @JsonProperty("homtownconf")
    private String homtownConf;
    @JsonProperty("id")
    private String id;
    @JsonProperty("id_check")
    private String idCheck;
    @JsonProperty("id_full")
    private String idFull;
    @JsonProperty("idconf")
    private String idconf;
    @JsonProperty("issue_by")
    private String issueBy;
    @JsonProperty("issue_date")
    private String issueDate;
    @JsonProperty("issue_date_conf")
    private String issueDateConf;
    @JsonProperty("name")
    private String name;
    @JsonProperty("nameconf")
    private String nameConf;
    @JsonProperty("national")
    private String national;
    @JsonProperty("precinct")
    private String precinct;
    @JsonProperty("province")
    private String province;
    @JsonProperty("religion")
    private String religion;
    @JsonProperty("religionconf")
    private String religionConf;
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("result_code")
    private String resultCode;
    @JsonProperty("server_name")
    private String serverName;
    @JsonProperty("server_ve")
    private String serverVer;
    @JsonProperty("sex")
    private String sex;
    @JsonProperty("sexconf")
    private String sexConf;
    @JsonProperty("street")
    private String street;
    @JsonProperty("street_name")
    private String streetName;
}

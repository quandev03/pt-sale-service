package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizationAddressResponse {
    @DbColumnMapper("ORG_ID")
    private Long orgId;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("PROVINCE_CODE")
    private String provinceCode;

    @DbColumnMapper("DISTRICT_CODE")
    private String districtCode;

    @DbColumnMapper("WARD_CODE")
    private String wardCode;

    @DbColumnMapper("ADDRESS")
    private String address;

    @DbColumnMapper("FULL_ADDRESS")
    private String fullAddress;

    @DbColumnMapper("PROVINCE_NAME")
    private String provinceName;

    @DbColumnMapper("DISTRICT_NAME")
    private String districtName;

    @DbColumnMapper("WARD_NAME")
    private String wardName;
}

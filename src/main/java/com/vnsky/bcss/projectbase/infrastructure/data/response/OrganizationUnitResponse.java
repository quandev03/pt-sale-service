package com.vnsky.bcss.projectbase.infrastructure.data.response;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationUnitResponse {
    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("PARENT_ID")
    private String parentId;

    @DbColumnMapper("ORG_CODE")
    private String orgCode;

    @DbColumnMapper("ORG_NAME")
    private String orgName;

    private String address;
    private String phone;
    private String email;
    private BigDecimal priceRoom;
    private String rentalStatus;
}

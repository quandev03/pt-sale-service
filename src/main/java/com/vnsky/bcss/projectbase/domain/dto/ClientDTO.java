package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClientDTO implements Serializable {

    private String id;

    private String code;

    private String name;

    private String contactName;

    private String contactPosition;

    private String contactEmail;

    private String contactPhone;

    private String permanentAddress;

    private String permanentProvinceId;

    private Long permanentDistrictId;

    private Long permanentWardId;

    private Integer status;

    private String parentCode;

    private String employeeCode;

    private Long debtLimit;

    private Long debtLimitMbf;
}

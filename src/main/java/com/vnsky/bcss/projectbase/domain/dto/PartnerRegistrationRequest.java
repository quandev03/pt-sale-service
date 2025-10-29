package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerRegistrationRequest {
    private String name;
    private Integer partnerType; // 1 = CHO_THUE_PHONG, 2 = DICH_VU
    private String taxCode;
    private String email;
    private String phone;
    private String address;

    // Optional admin account info; if null, backend may auto-generate
    private String adminUsername;
    private String adminEmail;
    private String adminPhone;
    private String adminPassword;
}




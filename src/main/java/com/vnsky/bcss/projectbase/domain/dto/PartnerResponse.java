package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerResponse {
    private Long id;
    private String name;
    private Integer partnerType;
    private String email;
    private String phone;
    private Integer status;
}




package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncompleteRegistrationRowDTO {
    @DbColumnMapper("ESIM_REGISTRATION_ID")
    private String registrationId;
    @DbColumnMapper("PCK_CODE")
    private String pckCode;
    @DbColumnMapper("QUANTITY")
    private Integer quantity;
}

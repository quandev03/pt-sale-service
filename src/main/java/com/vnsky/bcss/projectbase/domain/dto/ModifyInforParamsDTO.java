package com.vnsky.bcss.projectbase.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModifyInforParamsDTO {
    private String strSubType;

    private String strCustType;

    private String strReasonCode;

    private String strActionFlag;

    private String strAppObject;
}

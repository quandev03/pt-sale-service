package com.vnsky.bcss.projectbase.infrastructure.data.request.external;

import com.vnsky.bcss.projectbase.shared.constant.IntegrationConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterPackageMbfRequest {
    @Builder.Default
    private String command = IntegrationConstant.ESIM_REGISTER_COMMAND;

    private String strMobiSubType;
    private String strIsdn;
    private String strShopCode;
    private String strEmployee;
    private String strReasonCode;
    private List<List<String>> arrReg;
    private List<List<String>> arrDel;
    private String qlkhUsername;
    private String qlkhPassword;
} 
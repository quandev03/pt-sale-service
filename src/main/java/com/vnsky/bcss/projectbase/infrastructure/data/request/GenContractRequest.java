package com.vnsky.bcss.projectbase.infrastructure.data.request;

import com.vnsky.bcss.projectbase.domain.dto.ContractData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenContractRequest {
    @NotBlank
    private String organizationUnitId;

    @NotNull
    @Valid
    private ContractData contractData;
}


package com.vnsky.bcss.projectbase.infrastructure.data.request;

import com.vnsky.bcss.projectbase.domain.dto.ContractData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateContractRequest {
    @NotBlank
    private String organizationUnitId;

    @NotNull
    @Valid
    private ContractData contractData;

    // Images are received separately via @RequestPart, not in JSON
    // They will be set by the controller after validation
    private MultipartFile frontImage;

    private MultipartFile backImage;

    private MultipartFile portraitImage;
}


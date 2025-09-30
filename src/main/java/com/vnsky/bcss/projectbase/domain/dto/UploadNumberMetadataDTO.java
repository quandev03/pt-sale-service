package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(innerTypeName = "UploadNumberMetadataFields")
public class UploadNumberMetadataDTO {

    @Schema(description = "Mô tả giao dich")
    private String description;

}

package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class SalePackageDTO {
    @Schema(description = "so dien thoai nap goi cuoc")
    @NotNull(message = "Field is required")
    private String isdn;

    @NotNull(message = "Field is required")
    private String pckCode;
}

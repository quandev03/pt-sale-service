package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.domain.entity.AbstractAuditingEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@SuperBuilder
@FieldNameConstants
public class PackageProfileDTO extends AbstractAuditingEntity implements Serializable {

    @Schema(description = "ID gói cước")
    private String id;

    @Schema(description = "Mã gói cước")
    private String pckCode;

    @Schema(description = "Tên gói cước")
    private String pckName;

    private Long packagePrice;

    @Schema(description = "Trạng thái hoạt động")
    private Integer status = 1;

    @Schema(description = "Mô tả")
    private String description;

    @Schema(hidden = true)
    private String urlImagePackage;

    @Schema(description = "Chu kỳ gói cước")
    private Integer cycleValue;

    @Schema(description = "Đơn vị chu kỳ gói cước")
    private Integer cycleUnit;

    private String packageType;
}

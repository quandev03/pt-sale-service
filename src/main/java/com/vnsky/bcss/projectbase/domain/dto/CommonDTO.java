package com.vnsky.bcss.projectbase.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vnsky.bcss.projectbase.shared.utils.CustomDateDeserializer;
import com.vnsky.bcss.projectbase.shared.utils.CustomDateSerialize;
import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CommonDTO implements Serializable {
    @Schema(description = "Người tạo")
    @DbColumnMapper("CREATED_BY")
    private String createdBy;

    @Schema(description = "Ngày tạo")
    @DbColumnMapper("CREATED_DATE")
    @JsonSerialize(using = CustomDateSerialize.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime createdDate;

    @Schema(description = "Người cập nhật")
    @DbColumnMapper("MODIFIED_BY")
    private String modifiedBy;

    @Schema(description = "Ngày cập nhật")
    @DbColumnMapper("MODIFIED_DATE")
    @JsonSerialize(using = CustomDateSerialize.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalDateTime modifiedDate;
}

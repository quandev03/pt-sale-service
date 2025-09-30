package com.vnsky.bcss.projectbase.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class AppPickListDTO extends CommonDTO {
    
    @Schema(description = "ID")
    private String id;
    
    @Schema(description = "Tên bảng")
    private String tableName;
    
    @Schema(description = "Tên cột")
    private String columnName;
    
    @Schema(description = "Mã code")
    private String code;
    
    @Schema(description = "Giá trị")
    private String value;
    
    @Schema(description = "Kiểu dữ liệu")
    private String valueType;
    
    @Schema(description = "ID tham chiếu")
    private Long refId;
    
    @Schema(description = "Trạng thái")
    private Integer status;
    
    @Schema(description = "Mặc định")
    private Integer isDefault;
}

package com.vnsky.bcss.projectbase.infrastructure.data.response;


import com.vnsky.bcss.projectbase.domain.dto.CommonDTO;
import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class GetAllOrganizationUnitResponse extends CommonDTO {
    @DbColumnMapper("ID")
    @Schema(description = "Id đơn vị")
    private Long id;

    @DbColumnMapper("PARENT_ID")
    @Schema(description = "Id đơn vị cha")
    private Long parentId;

    @DbColumnMapper("ORG_CODE")
    @Schema(description = "Mã đơn vị")
    private String orgCode;

    @DbColumnMapper("ORG_NAME")
    @Schema(description = "Tên đơn vị")
    private String orgName;

    @DbColumnMapper("STATUS")
    private Integer status;

    @DbColumnMapper("EMAIL")
    private String email;

    @DbColumnMapper("PROVINCE_CODE")
    private String provinceCode;

    @DbColumnMapper("DISTRICT_CODE")
    private String districtCode;

    @DbColumnMapper("WARD_CODE")
    private String wardCode;

    @DbColumnMapper("ADDRESS")
    private String address;

    @DbColumnMapper("NOTE")
    private String note;
}

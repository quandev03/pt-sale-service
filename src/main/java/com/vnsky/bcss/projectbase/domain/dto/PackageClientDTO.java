package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.utils.DbColumnMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
public class PackageClientDTO {

    @DbColumnMapper("ID")
    private String id;

    @DbColumnMapper("PACKAGE_ID")
    private String packageId;

    @DbColumnMapper("CLIENT_ID")
    private String clientId;

    @DbColumnMapper("NAME")
    private String name;

    @DbColumnMapper("STATUS")
    private Integer status;
}

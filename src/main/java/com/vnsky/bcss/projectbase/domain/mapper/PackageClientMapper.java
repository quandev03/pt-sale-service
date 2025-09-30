package com.vnsky.bcss.projectbase.domain.mapper;


import com.vnsky.bcss.projectbase.domain.dto.PackageClientDTO;
import com.vnsky.bcss.projectbase.domain.entity.PackageClientEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PackageClientMapper extends BaseMapper<PackageClientEntity, PackageClientDTO> {

}

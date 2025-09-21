package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.entity.PackageProfileEntity;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PackageManagerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PackageProfileMapper extends BaseMapper<PackageProfileEntity, PackageProfileDTO> {
}

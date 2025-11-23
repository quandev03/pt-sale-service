package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.AdvertisementDTO;
import com.vnsky.bcss.projectbase.domain.entity.AdvertisementEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdvertisementMapper extends BaseMapper<AdvertisementEntity, AdvertisementDTO> {
}


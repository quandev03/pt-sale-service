package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionDTO;
import com.vnsky.bcss.projectbase.domain.entity.PartnerPackageSubscriptionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PartnerPackageSubscriptionMapper extends BaseMapper<PartnerPackageSubscriptionEntity, PartnerPackageSubscriptionDTO> {
}






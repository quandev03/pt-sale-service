package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionPaymentDTO;
import com.vnsky.bcss.projectbase.domain.entity.PartnerPackageSubscriptionPaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PartnerPackageSubscriptionPaymentMapper extends BaseMapper<PartnerPackageSubscriptionPaymentEntity, PartnerPackageSubscriptionPaymentDTO> {
}


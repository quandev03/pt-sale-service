package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.PartnerPackageSubscriptionPaymentEntity;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PartnerPackageSubscriptionPaymentRepository extends BaseJPARepository<PartnerPackageSubscriptionPaymentEntity, String> {

    Optional<PartnerPackageSubscriptionPaymentEntity> findByTxnRef(String txnRef);

    Optional<PartnerPackageSubscriptionPaymentEntity> findFirstBySubscriptionIdAndPaymentStatusInOrderByCreatedDateDesc(String subscriptionId,
                                                                                                                          List<PartnerPackageSubscriptionPaymentStatus> statuses);
}


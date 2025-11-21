package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionPaymentDTO;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;

import java.util.List;
import java.util.Optional;

public interface PartnerPackageSubscriptionPaymentRepoPort {

    PartnerPackageSubscriptionPaymentDTO saveAndFlush(PartnerPackageSubscriptionPaymentDTO dto);

    Optional<PartnerPackageSubscriptionPaymentDTO> findById(String id);

    Optional<PartnerPackageSubscriptionPaymentDTO> findByTxnRef(String txnRef);

    Optional<PartnerPackageSubscriptionPaymentDTO> findFirstBySubscriptionIdAndStatuses(String subscriptionId,
                                                                                         List<PartnerPackageSubscriptionPaymentStatus> statuses);
}

package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionPaymentDTO;

import java.util.Optional;

public interface PartnerPackageSubscriptionPaymentRepoPort {

    PartnerPackageSubscriptionPaymentDTO saveAndFlush(PartnerPackageSubscriptionPaymentDTO dto);

    Optional<PartnerPackageSubscriptionPaymentDTO> findByTxnRef(String txnRef);

    Optional<PartnerPackageSubscriptionPaymentDTO> findBySubscriptionId(String subscriptionId);
}


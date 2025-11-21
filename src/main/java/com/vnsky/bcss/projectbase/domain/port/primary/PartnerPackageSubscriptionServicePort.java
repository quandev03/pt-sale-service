package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionCreateCommand;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionDTO;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionView;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PartnerPackageSubscriptionServicePort {

    PartnerPackageSubscriptionDTO createSubscription(PartnerPackageSubscriptionCreateCommand command);

    Page<PartnerPackageSubscriptionView> listSubscriptions(String organizationUnitId,
                                                           String packageProfileId,
                                                           PartnerPackageSubscriptionStatus status,
                                                           Pageable pageable);

    PartnerPackageSubscriptionDTO stopSubscription(String subscriptionId);

    int expireSubscriptions();
}



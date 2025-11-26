package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionDTO;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionView;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PartnerPackageSubscriptionRepoPort {
    PartnerPackageSubscriptionDTO saveAndFlush(PartnerPackageSubscriptionDTO dto);

    Optional<PartnerPackageSubscriptionDTO> findById(String id);

    Optional<PartnerPackageSubscriptionDTO> findActiveByOrgUnitAndPackage(String organizationUnitId, String packageProfileId);

    Optional<PartnerPackageSubscriptionDTO> findByOrgUnitAndPackageAndStatuses(String organizationUnitId,
                                                                              String packageProfileId,
                                                                              List<PartnerPackageSubscriptionStatus> statuses);

    List<PartnerPackageSubscriptionDTO> findActiveSubscriptionsEndingBefore(LocalDateTime deadline);

    Page<PartnerPackageSubscriptionView> search(String organizationUnitId, String packageProfileId, String status, Pageable pageable);

    void updateStatusActive(String id);
}



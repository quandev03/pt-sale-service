package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.PartnerPackageSubscriptionEntity;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PartnerPackageSubscriptionRepository extends BaseJPARepository<PartnerPackageSubscriptionEntity, String> {

    Optional<PartnerPackageSubscriptionEntity> findByOrganizationUnitIdAndPackageProfileIdAndStatus(String organizationUnitId,
                                                                                                   String packageProfileId,
                                                                                                   PartnerPackageSubscriptionStatus status);

    Optional<PartnerPackageSubscriptionEntity> findFirstByOrganizationUnitIdAndPackageProfileIdAndStatusIn(String organizationUnitId,
                                                                                                           String packageProfileId,
                                                                                                           List<PartnerPackageSubscriptionStatus> statuses);

    List<PartnerPackageSubscriptionEntity> findByStatusAndEndTimeLessThanEqual(PartnerPackageSubscriptionStatus status,
                                                                               LocalDateTime deadline);

    @Query(value = """
        SELECT
            pps.ID,
            pps.ORG_UNIT_ID,
            ou.ORG_NAME,
            pps.PACKAGE_PROFILE_ID,
            pp.PCK_NAME AS PACKAGE_NAME,
            pps.START_TIME,
            pps.END_TIME,
            pps.STATUS,
            pps.CREATED_BY,
            pps.CREATED_DATE,
            pps.MODIFIED_BY,
            pps.MODIFIED_DATE
        FROM PARTNER_PACKAGE_SUBSCRIPTION pps
                 JOIN ORGANIZATION_UNIT ou ON ou.ID = pps.ORG_UNIT_ID
                 JOIN PACKAGE_PROFILE pp ON pp.ID = pps.PACKAGE_PROFILE_ID
        WHERE (:organizationUnitId IS NULL OR pps.ORG_UNIT_ID = :organizationUnitId)
          AND (:packageProfileId IS NULL OR pps.PACKAGE_PROFILE_ID = :packageProfileId)
          AND (:status IS NULL OR pps.STATUS = :status)
        ORDER BY pps.CREATED_DATE DESC
        """,
        countQuery = """
        SELECT COUNT(1)
        FROM PARTNER_PACKAGE_SUBSCRIPTION pps
        WHERE (:organizationUnitId IS NULL OR pps.ORG_UNIT_ID = :organizationUnitId)
          AND (:packageProfileId IS NULL OR pps.PACKAGE_PROFILE_ID = :packageProfileId)
          AND (:status IS NULL OR pps.STATUS = :status)
        """,
        nativeQuery = true)
    Page<Tuple> search(@Param("organizationUnitId") String organizationUnitId,
                       @Param("packageProfileId") String packageProfileId,
                       @Param("status") String status,
                       Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = """
        update PARTNER_PACKAGE_SUBSCRIPTION set STATUS = 'ACTIVE'
            where id = :id
    """, nativeQuery = true)
    void updateStatus(@Param("id") String id);
}



package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.AdvertisementEntity;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, String> {

    List<AdvertisementEntity> findByClientId(String clientId);

    @Query("SELECT a FROM AdvertisementEntity a WHERE a.status = :status " +
           "AND :currentDate >= a.startDate AND :currentDate <= a.endDate")
    List<AdvertisementEntity> findActiveAdvertisements(
        @Param("status") AdvertisementStatus status,
        @Param("currentDate") LocalDateTime currentDate
    );

    @Query(value = """
        SELECT * FROM (
            SELECT a.* FROM ADVERTISEMENT a
            WHERE a.STATUS = :status
              AND :currentDate >= a.START_DATE
              AND :currentDate <= a.END_DATE
            ORDER BY DBMS_RANDOM.VALUE
        ) WHERE ROWNUM = 1
        """, nativeQuery = true)
    Optional<AdvertisementEntity> findRandomActiveAdvertisement(
        @Param("status") String status,
        @Param("currentDate") LocalDateTime currentDate
    );

    Optional<AdvertisementEntity> findByIdAndClientId(String id, String clientId);
}


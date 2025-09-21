package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.PackageProfileEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PackageProfileRepository extends JpaRepository<PackageProfileEntity, String> {

    @Query(value = """
           SELECT COUNT(*)
           FROM package_profile
           WHERE pck_code = :packageCode
        """, nativeQuery = true)
    int isExistPackageCode(@Param("packageCode") String packageCode);

    String id(String id);

    @Query(
        value = """
                SELECT *
                FROM PACKAGE_PROFILE pp
                WHERE
                    (:pckCodeOrPckName IS NULL OR PCK_CODE LIKE '%' || :pckCodeOrPckName || '%' OR PCK_NAME LIKE '%' || :pckCodeOrPckName || '%')
                    AND (:status IS NULL OR pp.STATUS = :status)
                    AND (:minPrice IS NULL OR pp.PCK_PRICE >= :minPrice)
                    AND (:maxPrice IS NULL OR pp.PCK_PRICE <= :maxPrice)
            """, nativeQuery = true
    )
    Page<PackageProfileEntity> searchPackageProfile(@Param("pckCodeOrPckName") String pckCodeOrPckName, @Param("status") Integer status, @Param("minPrice") Long minPrice, @Param("maxPrice") Long maxPrice, Pageable pageable);

    @Query(value = """
            SELECT *
            FROM PACKAGE_PROFILE pp
            WHERE pp.PCK_PRICE = 0
        """, nativeQuery = true)
    List<PackageProfileEntity> getPackageProfileFree();

    Optional<PackageProfileEntity> findByPckCode(String pckCode);

    @Query(value = """
        SELECT *
        FROM package_profile
        WHERE pck_code = :packageCode
    """, nativeQuery = true)
    PackageProfileEntity findByPackageCode(@Param("packageCode") String packageCode);
}

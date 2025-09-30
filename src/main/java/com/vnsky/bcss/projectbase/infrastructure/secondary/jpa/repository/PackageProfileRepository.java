package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.PackageProfileEntity;
import jakarta.persistence.Tuple;
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
            (:pckCodeOrPckName IS NULL
                OR UPPER(pp.PCK_CODE) LIKE '%' || UPPER(:pckCodeOrPckName) || '%'
                OR UPPER(pp.PCK_NAME ) LIKE '%' || UPPER(:pckCodeOrPckName) || '%')
            AND (:status IS NULL OR pp.STATUS = :status)
            AND (:minPrice IS NULL OR pp.PCK_PRICE >= :minPrice)
            AND (:maxPrice IS NULL OR pp.PCK_PRICE <= :maxPrice)
        ORDER BY MODIFIED_DATE DESC
    """, nativeQuery = true
    )
    Page<PackageProfileEntity> searchPackageProfile(@Param("pckCodeOrPckName") String pckCodeOrPckName, @Param("status") Integer status, @Param("minPrice") Long minPrice, @Param("maxPrice") Long maxPrice, Pageable pageable);

    @Query(value = """
            SELECT pp.* FROM PACKAGE_CLIENT pc
            JOIN PACKAGE_PROFILE pp ON pc.PACKAGE_ID = pp.ID
            WHERE CLIENT_ID = :clientId
            AND pp.STATUS = 1
            ORDER BY pp.PCK_CODE
        """, nativeQuery = true)
    List<PackageProfileEntity> getPackageProfile(@Param("clientId") String clientId);

    Optional<PackageProfileEntity> findByPckCode(String pckCode);

    @Query(value = """
        SELECT *
        FROM package_profile
        WHERE pck_code = :packageCode
    """, nativeQuery = true)
    PackageProfileEntity findByPackageCode(@Param("packageCode") String packageCode);

    boolean existsByPckName(String pckName);

    @Query(value = """
          SELECT pp.*
          FROM PACKAGE_PROFILE pp
          WHERE EXISTS (
            SELECT 1
            FROM PACKAGE_CLIENT pc
            WHERE pc.PACKAGE_ID = pp.ID
              AND pc.CLIENT_ID  = :clientId
          )
  """, nativeQuery = true)
    List<PackageProfileEntity> getPackageByClientId(@Param("clientId") String clientId);

    @Query(value = "SELECT * FROM PACKAGE_PROFILE WHERE PCK_CODE IN :codes", nativeQuery = true)
    List<PackageProfileEntity> findAllByPackageCodeIn(@Param("codes") List<String> codes);

    @Query(value = """
        SELECT NVL(SUM(bps.SUCCEEDED_NUMBER), 0) AS total_quantity
        FROM BATCH_PACKAGE_SALE bps
        JOIN SALE_ORDER so ON so.ID = bps.ORDER_ID
        JOIN ORGANIZATION_UNIT ouh
          ON ouh.ID = so.ORG_ID AND ouh.ORG_TYPE = 'NBO'
        WHERE (
            :orgCode IS NULL
            OR ouh.ID IN (
                    SELECT ou.ID
                    FROM ORGANIZATION_UNIT ou
                    WHERE ou.ORG_TYPE = 'NBO'
                    START WITH ou.ORG_CODE = :orgCode
                    CONNECT BY PRIOR ou.ID = ou.PARENT_ID
            )
        )
     """, nativeQuery = true)
    Long totalPackagesSold(@Param("orgCode") String orgCode);

    @Query(value = """
        SELECT SUM(sol.PRICE) AS REVENUES
        FROM SALE_ORDER_LINE sol
        JOIN SALE_ORDER so ON so.ID = sol.SALE_ORDER_ID
        WHERE sol.PAY_STATUS = 1 AND (
        	:orgCode IS NULL OR so.ORG_ID IN (
        	SELECT ou.ID
        	FROM ORGANIZATION_UNIT ou
        	WHERE ou.ORG_TYPE = 'NBO'
        	START WITH ou.ORG_CODE = :orgCode
        	CONNECT BY PRIOR ou.ID = ou.PARENT_ID
        ))
     """, nativeQuery = true)
    Long revenusPackageSold(@Param("orgCode") String orgCode);

    @Query(value = """
                                                  /* :granularity = 1 -> NGÀ
                                                     :granularity = 2 -> TUẦN  (ISO week: 'IW', ISO year: 'IYYY'
                                                     :granularity = 3 -> THÁNG (STAT_DATE = ngày đầu tháng
                                                     :granularity = 4 -> NĂM   (STAT_DATE = 01/01
                                                  */
                                                  WITH
                                                  params AS (
                                                    SELECT
                                                      TO_NUMBER(:granularity) AS g,
                                                      TRUNC(TO_DATE(:startDate,'YYYY-MM-DD'))        AS start_day,
                                                      TRUNC(TO_DATE(:startDate,'YYYY-MM-DD'),'IW')   AS start_week,
                                                      TRUNC(TO_DATE(:startDate,'YYYY-MM-DD'),'MM')   AS start_month,
                                                      TRUNC(TO_DATE(:startDate,'YYYY-MM-DD'),'YYYY') AS start_year,
                                                      TRUNC(TO_DATE(:endDate,'YYYY-MM-DD'))          AS end_day,
                                                      TRUNC(TO_DATE(:endDate,'YYYY-MM-DD'),'IW')     AS end_week,
                                                      TRUNC(TO_DATE(:endDate,'YYYY-MM-DD'),'MM')     AS end_month,
                                                      TRUNC(TO_DATE(:endDate,'YYYY-MM-DD'),'YYYY')   AS end_year
                                                    FROM dual
                                                  ),
                                                  date_range AS ( -- sinh đầy đủ các mốc đầu kỳ
                                                    SELECT
                                                      p.g,
                                                      CASE
                                                        WHEN p.g = 1 THEN p.start_day  + (LEVEL - 1)                   -- ngày
                                                        WHEN p.g = 2 THEN p.start_week + 7 * (LEVEL - 1)               -- thứ Hai đầu tuần (ISO)
                                                        WHEN p.g = 3 THEN ADD_MONTHS(p.start_month, LEVEL - 1)         -- đầu tháng
                                                        WHEN p.g = 4 THEN ADD_MONTHS(p.start_year,  12 * (LEVEL - 1))  -- 01/01 mỗi năm
                                                      END AS stat_date
                                                    FROM params p
                                                    CONNECT BY LEVEL <= CASE
                                                      WHEN p.g = 1 THEN (p.end_day  - p.start_day)  + 1
                                                      WHEN p.g = 2 THEN TRUNC( (p.end_week - p.start_week) / 7 ) + 1
                                                      WHEN p.g = 3 THEN MONTHS_BETWEEN(p.end_month, p.start_month) + 1
                                                      WHEN p.g = 4 THEN TRUNC( MONTHS_BETWEEN(p.end_year, p.start_year) / 12 ) + 1
                                                    END
                                                  ),
                                                  base AS (  -- dữ liệu gốc + điều kiện org + lọc ngày để tối ưu
                                                    SELECT
                                                      so.ORDER_DATE,
                                                      NVL(bps.SUCCEEDED_NUMBER, 0) AS qty
                                                    FROM SALE_ORDER so
                                                    JOIN BATCH_PACKAGE_SALE bps
                                                      ON bps.ORDER_ID = so.ID
                                                    JOIN ORGANIZATION_UNIT ouh
                                                      ON ouh.ID = so.ORG_ID
                                                     AND ouh.ORG_TYPE = 'NBO'
                                                    WHERE (
                                                      :orgCode IS NULL
                                                      OR ouh.ID IN (
                                                          SELECT ou.ID
                                                          FROM ORGANIZATION_UNIT ou
                                                          WHERE ou.ORG_TYPE = 'NBO'
                                                          START WITH ou.ORG_CODE = :orgCode
                                                          CONNECT BY PRIOR ou.ID = ou.PARENT_ID
                                                        )
                                                    )
                                                    /* Lọc theo khoảng ngày để tận dụng index trên ORDER_DATE */
                                                    AND so.ORDER_DATE >= (SELECT start_day FROM params)
                                                    AND so.ORDER_DATE <  (SELECT end_day   FROM params) + 1
                                                  ),
                                                  agg AS (  -- chuẩn hoá ORDER_DATE về đầu kỳ rồi cộng
                                                    SELECT
                                                      period_start,
                                                      SUM(qty) AS total_quantity
                                                    FROM (
                                                      SELECT
                                                        CASE
                                                          WHEN TO_NUMBER(:granularity) = 1 THEN TRUNC(order_date)         -- ngày
                                                          WHEN TO_NUMBER(:granularity) = 2 THEN TRUNC(order_date, 'IW')   -- tuần (ISO)
                                                          WHEN TO_NUMBER(:granularity) = 3 THEN TRUNC(order_date, 'MM')   -- tháng
                                                          WHEN TO_NUMBER(:granularity) = 4 THEN TRUNC(order_date, 'YYYY') -- năm
                                                        END AS period_start,
                                                        qty
                                                      FROM base
                                                    )
                                                    GROUP BY period_start
                                                  )
                                                  SELECT
                                                    dr.stat_date                                      AS STAT_DATE,   -- mốc đầu kỳ
                                                    /* Tuần theo năm (ISO) khi granularity=2 */
                                                    CASE WHEN dr.g = 2 THEN TO_NUMBER(TO_CHAR(dr.stat_date, 'IYYY')) END AS STAT_YEAR,
                                                    CASE WHEN dr.g = 2 THEN TO_NUMBER(TO_CHAR(dr.stat_date, 'IW'))   END AS STAT_WEEK,
                                                    /* Nhãn hiển thị gọn */
                                                    CASE
                                                      WHEN dr.g = 1 THEN TO_CHAR(dr.stat_date, 'YYYY-MM-DD')
                                                      WHEN dr.g = 2 THEN TO_CHAR(dr.stat_date, 'IYYY') || '-W' || LPAD(TO_CHAR(dr.stat_date,'IW'),2,'0')
                                                      WHEN dr.g = 3 THEN TO_CHAR(dr.stat_date, 'YYYY-MM')
                                                      WHEN dr.g = 4 THEN TO_CHAR(dr.stat_date, 'YYYY')
                                                    END                                             AS STAT_LABEL,
                                                    NVL(a.total_quantity, 0)                        AS STAT_COUNT     -- kỳ không có dữ liệu => 0
                                                  FROM date_range dr
                                                  LEFT JOIN agg a
                                                    ON a.period_start = dr.stat_date
                                                  ORDER BY dr.stat_date
    """, nativeQuery = true)
    List<Tuple> statisticPackagesSold(@Param("orgCode") String orgCode,
                                      @Param("startDate") String startDate,
                                      @Param("endDate") String endDate,
                                      @Param("granularity") int granularity);

    @Query(value = """
                            SELECT
                                 ouh.ID                           AS ORG_ID,
                                 ouh.ORG_CODE                     AS ORG_CODE,
                                 ouh.ORG_NAME                     AS ORG_NAME,
                                 NVL(SUM(bps.SUCCEEDED_NUMBER),0) AS COUNT
                             FROM ORGANIZATION_UNIT ouh
                             LEFT JOIN SALE_ORDER so
                               ON so.ORG_ID = ouh.ID
                              AND ( :startDate IS NULL
                                    OR so.ORDER_DATE >= TO_TIMESTAMP(:startDate, 'YYYY-MM-DD') )
                              AND ( :endDate   IS NULL
                                    OR so.ORDER_DATE <  TO_TIMESTAMP(:endDate  , 'YYYY-MM-DD') + INTERVAL '1' DAY )
                             LEFT JOIN BATCH_PACKAGE_SALE bps
                               ON bps.ORDER_ID = so.ID
                             WHERE (
                               :orgCode IS NULL OR (
                                 ouh.ID IN (
                                   SELECT ou.ID
                                   FROM ORGANIZATION_UNIT ou
                                   WHERE ou.ORG_TYPE = 'NBO'
                                   START WITH ou.ORG_CODE = :orgCode
                                   CONNECT BY PRIOR ou.ID = ou.PARENT_ID
                                 )
                               )
                             )
                                AND ouh.ORG_TYPE = 'NBO'
                             GROUP BY ouh.ID, ouh.ORG_CODE, ouh.ORG_NAME
                             ORDER BY ouh.ORG_CODE
    """, nativeQuery = true)
    List<Tuple> statisticPackagesSoldOrg(@Param("orgCode") String orgCode,
                                      @Param("startDate") String startDate,
                                      @Param("endDate") String endDate);
}

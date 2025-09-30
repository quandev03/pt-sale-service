package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.SaleOrderEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrderEntity, String> {
    @Query(value = """
    SELECT so.*
    FROM SALE_ORDER so
    JOIN PACKAGE_PROFILE pp ON pp.PCK_CODE = so.PCK_CODE
    WHERE (:isFree = 0 OR pp.PCK_PRICE = 0)
      AND (:fromDate IS NULL OR so.ORDER_DATE >= TO_DATE(:fromDate, 'YYYY-MM-DD HH24:MI:SS'))
      AND (:toDate   IS NULL OR so.ORDER_DATE <= TO_DATE(:toDate,   'YYYY-MM-DD HH24:MI:SS'))
      AND (:textSearch IS NULL OR so.CREATED_BY = :textSearch)
      AND so.ORG_ID = :orgId
    ORDER BY so.ORDER_DATE DESC
""",
        countQuery = """
    SELECT COUNT(*)
    FROM SALE_ORDER so
    JOIN PACKAGE_PROFILE pp ON pp.PCK_CODE = so.PCK_CODE
    WHERE (:isFree = 0 OR pp.PCK_PRICE = 0)
      AND (:fromDate IS NULL OR so.ORDER_DATE >= TO_DATE(:fromDate, 'YYYY-MM-DD HH24:MI:SS'))
      AND (:toDate   IS NULL OR so.ORDER_DATE <= TO_DATE(:toDate,   'YYYY-MM-DD HH24:MI:SS'))
      AND (:textSearch IS NULL OR so.CREATED_BY = :textSearch)
      AND so.ORG_ID = :orgId
""",
        nativeQuery = true)
    Page<SaleOrderEntity> findBookFreeByOrgId(
        Pageable pageable,
        @Param("orgId") String orgId,
        @Param("toDate")  String toDate,
        @Param("fromDate") String fromDate,
        @Param("textSearch") String textSearch,
        @Param("isFree") Integer isFree
    );

    @Query(value = """
        SELECT DISTINCT so.ID as ID,
               so.ORG_ID as ORG_ID,
               so.ORDER_NO as ORDER_NO,
               so.AMOUNT_TOTAL as AMOUNT_TOTAL,
               so.REASON_ID as REASON_ID,
               so.DESCRIPTION as DESCRIPTION,
               so.NOTE as NOTE,
               er.BOOK_ESIM_STATUS as STATUS,
               so.ORDER_TYPE as ORDER_TYPE,
               so.CUSTOMER_EMAIL as CUSTOMER_EMAIL,
               so.QUANTITY as QUANTITY,
               so.CANCEL_REASON as CANCEL_REASON,
               so.ORDER_DATE as ORDER_DATE,
               so.CREATED_BY as CREATED_BY,
               so.CREATED_DATE as CREATED_DATE,
               so.MODIFIED_BY as MODIFIED_BY,
               so.MODIFIED_DATE as MODIFIED_DATE,
               er.SUCCESSED_NUMBER as SUCCESSED_NUMBER,
               er.FAILED_NUMBER as FAILED_NUMBER,
               LISTAGG(DISTINCT pp.PCK_CODE, ',') WITHIN GROUP (ORDER BY pp.PCK_CODE) as PACKAGE_CODES
        FROM SALE_ORDER so
        LEFT JOIN SALE_ORDER_LINE sol ON so.ID = sol.SALE_ORDER_ID
        LEFT JOIN PACKAGE_PROFILE pp ON sol.PCK_CODE = pp.PCK_CODE
        LEFT JOIN ESIM_REGISTRATION er ON so.ID = er.ORDER_ID
        WHERE so.ORDER_TYPE = 0
        AND (:toDate IS NULL OR TRUNC(so.ORDER_DATE) <= TO_DATE(:toDate, 'dd/MM/yyyy'))
        AND (:fromDate IS NULL OR TRUNC(so.ORDER_DATE) >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
        AND (:textSearch IS NULL OR so.CREATED_BY LIKE :textSearch)
        AND so.ORG_ID = :orgId
        GROUP BY so.ID, so.ORG_ID, so.ORDER_NO, so.AMOUNT_TOTAL, so.REASON_ID, er.BOOK_ESIM_STATUS, so.DESCRIPTION,
                 so.NOTE, so.ORDER_TYPE, so.CUSTOMER_EMAIL, so.QUANTITY, so.CANCEL_REASON,
                 so.ORDER_DATE, so.CREATED_BY, so.CREATED_DATE, so.MODIFIED_BY, so.MODIFIED_DATE,
                 er.SUCCESSED_NUMBER, er.FAILED_NUMBER
        ORDER BY so.ORDER_DATE DESC
    """,
            countQuery = """
        SELECT COUNT(DISTINCT so.ID) FROM SALE_ORDER so
        LEFT JOIN SALE_ORDER_LINE sol ON so.ID = sol.SALE_ORDER_ID
        LEFT JOIN PACKAGE_PROFILE pp ON sol.PCK_CODE = pp.PCK_CODE
        LEFT JOIN ESIM_REGISTRATION er ON so.ID = er.ORDER_ID
        WHERE so.ORDER_TYPE = 0
        AND (:toDate IS NULL OR TRUNC(so.ORDER_DATE) <= TO_DATE(:toDate, 'dd/MM/yyyy'))
        AND (:fromDate IS NULL OR TRUNC(so.ORDER_DATE) >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
        AND (:textSearch IS NULL OR so.CREATED_BY LIKE :textSearch)
        AND so.ORG_ID = :orgId
    """,
            nativeQuery = true)
    Page<Tuple> findBookByOrgId(Pageable pageable, @Param("orgId") String orgId, @Param("toDate") String toDate, @Param("fromDate") String fromDate, @Param("textSearch") String textSearch);

    @Query(value = """
        SELECT DISTINCT so.ID as ID,
               so.ORG_ID as ORG_ID,
               so.ORDER_NO as ORDER_NO,
               so.AMOUNT_TOTAL as AMOUNT_TOTAL,
               so.REASON_ID as REASON_ID,
               so.DESCRIPTION as DESCRIPTION,
               so.NOTE as NOTE,
               er.BOOK_ESIM_STATUS as STATUS,
               so.ORDER_TYPE as ORDER_TYPE,
               so.CUSTOMER_EMAIL as CUSTOMER_EMAIL,
               so.QUANTITY as QUANTITY,
               so.CANCEL_REASON as CANCEL_REASON,
               so.ORDER_DATE as ORDER_DATE,
               so.CREATED_BY as CREATED_BY,
               so.CREATED_DATE as CREATED_DATE,
               so.MODIFIED_BY as MODIFIED_BY,
               so.MODIFIED_DATE as MODIFIED_DATE,
               er.SUCCESSED_NUMBER as SUCCESSED_NUMBER,
               er.FAILED_NUMBER as FAILED_NUMBER,
               LISTAGG(pp.PCK_CODE, ',') WITHIN GROUP (ORDER BY pp.PCK_CODE) as PACKAGE_CODES
        FROM SALE_ORDER so
        LEFT JOIN SALE_ORDER_LINE sol ON so.ID = sol.SALE_ORDER_ID
        LEFT JOIN PACKAGE_PROFILE pp ON sol.PCK_CODE = pp.PCK_CODE
        LEFT JOIN ESIM_REGISTRATION er ON so.ID = er.ORDER_ID
        WHERE (:toDate IS NULL OR TRUNC(so.ORDER_DATE) <= TO_DATE(:toDate, 'dd/MM/yyyy'))
        AND (:fromDate IS NULL OR TRUNC(so.ORDER_DATE) >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
        AND (:textSearch IS NULL OR so.CREATED_BY LIKE :textSearch)
        AND so.ORG_ID = :orgId
        GROUP BY so.ID, so.ORG_ID, so.ORDER_NO, so.AMOUNT_TOTAL, so.REASON_ID, er.BOOK_ESIM_STATUS, so.DESCRIPTION,
                 so.NOTE, so.ORDER_TYPE, so.CUSTOMER_EMAIL, so.QUANTITY, so.CANCEL_REASON,
                 so.ORDER_DATE, so.CREATED_BY, so.CREATED_DATE, so.MODIFIED_BY, so.MODIFIED_DATE,
                 er.SUCCESSED_NUMBER, er.FAILED_NUMBER
        ORDER BY so.ORDER_DATE DESC
    """,
        nativeQuery = true)
    List<Tuple> findBookByOrgIdExport(@Param("orgId") String orgId, @Param("toDate") String toDate, @Param("fromDate") String fromDate, @Param("textSearch") String textSearch);

    @Query(value = """
    SELECT
        so.ID AS ID,
        so.ORDER_NO AS ORDER_NO,
        ou.ORG_CODE AS ORG_CODE,
        ou.ORG_NAME AS ORG_NAME,
        so.ORDER_TYPE AS ORDER_TYPE,
        so.AMOUNT_TOTAL AS AMOUNT_TOTAL,
        so.QUANTITY AS QUANTITY,
        er.SUCCESSED_NUMBER AS SUCCEEDED_NUMBER,
        so.CREATED_BY AS CREATED_BY,
        so.ORDER_DATE AS ORDER_DATE,
        count(*) over() as total
    FROM SALE_ORDER so
    JOIN ORGANIZATION_UNIT ou ON so.ORG_ID = ou.ID
    JOIN ESIM_REGISTRATION er ON so.ID = er.ORDER_ID
    WHERE
        (:q IS NULL OR LOWER(so.ORDER_NO) LIKE LOWER('%' || :q || '%'))
        AND (:orgCodesIsNull = 1 OR ou.ORG_CODE IN (:orgCodes))
        AND er.BOOK_ESIM_STATUS = 2
        AND (:startDate IS NULL OR TRUNC(so.ORDER_DATE) >= TO_DATE(:startDate, 'yyyy/MM/dd'))
        AND (:endDate   IS NULL OR TRUNC(so.ORDER_DATE) <=  TO_DATE(:endDate, 'yyyy/MM/dd'))
        AND (
                :currentOrgCode IS NULL
                OR ou.ID IN (
                      SELECT oun.ID
                      FROM ORGANIZATION_UNIT oun
                      WHERE oun.ORG_TYPE = 'NBO'
                      START WITH oun.ORG_CODE = :currentOrgCode
                      CONNECT BY PRIOR oun.ID = oun.PARENT_ID
                )
              )
    ORDER BY so.ORDER_DATE DESC
    OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY
""", nativeQuery = true)
    List<Tuple> searchOrderRevenueReport(
        @Param("currentOrgCode") String currentOrgCode,
        @Param("q") String q,
        @Param("orgCodes") List<String> orgCodes,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("offset") long offset,
        @Param("pageSize") int pageSize,
        @Param("orgCodesIsNull") int orgCodesIsNull
    );

    @Query(value = """
    SELECT
          so.ID AS ID,
          so.ORDER_NO AS ORDER_NO,
          ou.ORG_CODE AS ORG_CODE,
          ou.ORG_NAME AS ORG_NAME,
          so.ORDER_TYPE AS ORDER_TYPE,
          so.AMOUNT_TOTAL AS AMOUNT_TOTAL,
          so.QUANTITY AS QUANTITY,
          er.SUCCESSED_NUMBER AS SUCCEEDED_NUMBER,
          so.CREATED_BY AS CREATED_BY,
          so.ORDER_DATE AS ORDER_DATE
    FROM SALE_ORDER so
    JOIN ORGANIZATION_UNIT ou ON so.ORG_ID = ou.id
    JOIN ESIM_REGISTRATION er ON so.ID = er.ORDER_ID
    WHERE
        (:q IS NULL OR LOWER(so.ORDER_NO) LIKE LOWER('%' || :q || '%'))
        AND (:orgCodesIsNull = 1 OR ou.ORG_CODE IN (:orgCodes))
        AND (:startDate IS NULL OR TRUNC(so.ORDER_DATE) >= TO_DATE(:startDate, 'yyyy/MM/dd'))
        AND (:endDate   IS NULL OR TRUNC(so.ORDER_DATE) <=  TO_DATE(:endDate, 'yyyy/MM/dd'))
        AND er.BOOK_ESIM_STATUS = 2
        AND (
                :currentOrgCode IS NULL
                OR ou.ID IN (
                      SELECT oun.ID
                      FROM ORGANIZATION_UNIT oun
                      WHERE oun.ORG_TYPE = 'NBO'
                      START WITH oun.ORG_CODE = :currentOrgCode
                      CONNECT BY PRIOR oun.ID = oun.PARENT_ID
                )
              )
    ORDER BY so.ORDER_DATE DESC
""", nativeQuery = true)
    List<Tuple> getOrderRevenueReport(
        @Param("currentOrgCode") String currentOrgCode,
        @Param("q") String q,
        @Param("orgCodes") List<String> orgCodes,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate,
        @Param("orgCodesIsNull") int orgCodesIsNull);

    @Query(value = """
        /* :granularity = 1 -> NGÀY
           :granularity = 2 -> TUẦN  (ISO week: 'IW', ISO year: 'IYYY')
           :granularity = 3 -> THÁNG (STAT_DATE = ngày đầu tháng)
           :granularity = 4 -> NĂM   (STAT_DATE = 01/01 năm)
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
  date_range AS (
    SELECT
      p.g,
      CASE
        WHEN p.g = 1 THEN p.start_day  + (LEVEL - 1)
        WHEN p.g = 2 THEN p.start_week + 7 * (LEVEL - 1)
        WHEN p.g = 3 THEN ADD_MONTHS(p.start_month, LEVEL - 1)
        WHEN p.g = 4 THEN ADD_MONTHS(p.start_year,  12 * (LEVEL - 1))
      END AS stat_date
    FROM params p
    CONNECT BY LEVEL <= CASE
      WHEN p.g = 1 THEN (p.end_day  - p.start_day)  + 1
      WHEN p.g = 2 THEN TRUNC( (p.end_week - p.start_week) / 7 ) + 1
      WHEN p.g = 3 THEN MONTHS_BETWEEN(p.end_month, p.start_month) + 1
      WHEN p.g = 4 THEN TRUNC( MONTHS_BETWEEN(p.end_year, p.start_year) / 12 ) + 1
    END
  ),
  base AS (
    SELECT
      MIN(ah.ACTION_DATE) AS created_date,
      s.ID                AS sub_id
    FROM SUBSCRIBER s
    JOIN ACTION_HISTORY ah
      ON ah.SUB_ID = s.ID
     AND ah.ACTION_CODE IN ('GEN_QR_CODE','SEND_QR_CODE')
    JOIN ORGANIZATION_UNIT ouh
      ON ouh.ID = s.ORG_ID
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
    GROUP BY s.ID
  ),
  agg AS (
    SELECT
      CASE p.g
        WHEN 1 THEN TRUNC(created_date)
        WHEN 2 THEN TRUNC(created_date, 'IW')
        WHEN 3 THEN TRUNC(created_date, 'MM')
        WHEN 4 THEN TRUNC(created_date, 'YYYY')
      END AS period_start,
      COUNT(*) AS total_sub
    FROM base, params p
    GROUP BY CASE p.g
      WHEN 1 THEN TRUNC(created_date)
      WHEN 2 THEN TRUNC(created_date, 'IW')
      WHEN 3 THEN TRUNC(created_date, 'MM')
      WHEN 4 THEN TRUNC(created_date, 'YYYY')
    END
  )
SELECT
  dr.stat_date AS STAT_DATE,
  CASE WHEN dr.g = 2 THEN TO_NUMBER(TO_CHAR(dr.stat_date, 'IYYY')) END AS STAT_YEAR,
  CASE WHEN dr.g = 2 THEN TO_NUMBER(TO_CHAR(dr.stat_date, 'IW'))   END AS STAT_WEEK,
  CASE
    WHEN dr.g = 1 THEN TO_CHAR(dr.stat_date, 'YYYY-MM-DD')
    WHEN dr.g = 2 THEN TO_CHAR(dr.stat_date, 'IYYY') || '-W' || LPAD(TO_CHAR(dr.stat_date,'IW'),2,'0')
    WHEN dr.g = 3 THEN TO_CHAR(dr.stat_date, 'YYYY-MM')
    WHEN dr.g = 4 THEN TO_CHAR(dr.stat_date, 'YYYY')
  END AS STAT_LABEL,
  NVL(a.total_sub, 0) AS STAT_COUNT
FROM date_range dr
LEFT JOIN agg a
  ON a.period_start = dr.stat_date
ORDER BY dr.stat_date
    """, nativeQuery = true)
    List<Tuple> statisticEsimSold(@Param("orgCode") String orgCode,
                                  @Param("startDate") String startDate,
                                  @Param("endDate") String endDate,
                                  @Param("granularity") int granularity);

    @Query(value = """
                                    SELECT
                                       ouh.ID                       AS ORG_ID,
                                       ouh.ORG_CODE                 AS ORG_CODE,
                                       ouh.ORG_NAME                 AS ORG_NAME,
                                       NVL(SUM(er.SUCCESSED_NUMBER), 0) AS COUNT
                                     FROM ORGANIZATION_UNIT ouh
                                     LEFT JOIN SALE_ORDER so
                                       ON ouh.ID = so.ORG_ID
                                      AND ( :startDate IS NULL
                                            OR so.ORDER_DATE >= TO_TIMESTAMP(:startDate, 'YYYY-MM-DD') )
                                      AND ( :endDate   IS NULL
                                            OR so.ORDER_DATE <  TO_TIMESTAMP(:endDate  , 'YYYY-MM-DD') + INTERVAL '1' DAY )
                                     LEFT JOIN ESIM_REGISTRATION er
                                       ON so.ID = er.ORDER_ID
                                     WHERE (
                                       :orgCode IS NULL
                                       OR (
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
    List<Tuple> statisticEsimSoldOrg(@Param("orgCode") String orgCode,
                                  @Param("startDate") String startDate,
                                  @Param("endDate") String endDate);

    @Query(value = """
                    SELECT sum(so.AMOUNT_TOTAL)
                    FROM SALE_ORDER so
                    JOIN ORGANIZATION_UNIT ouh ON ouh.ID = so.ORG_ID
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
    """,nativeQuery = true)
    BigDecimal revenue(@Param("orgCode") String orgCode);

    @Query(value = """
            SELECT erl.ISDN, erl.SERIAL, erl.LPA, erl.STATUS, erl.CREATED_DATE
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION er ON so.ID = er.ORDER_ID
            JOIN ESIM_REGISTRATION_LINE erl ON er.ID = erl.ESIM_REGISTRATION_ID
            WHERE so.ID = :saleOrderId
            AND erl.status IS NOT NULL
        """, nativeQuery = true)
    List<Tuple> getResultBookEsim(@Param("saleOrderId") String saleOrderId);

    // Báo cáo tổng hợp theo từng tổ chức
    @Query(value = """
            WITH org_hierarchy AS (
                SELECT
                    ou.ID,
                    ou.ORG_CODE,
                    ou.ORG_NAME,
                    ou.ORG_TYPE,
                    ou.PARENT_ID,
                    CONNECT_BY_ROOT ou.ORG_CODE AS ROOT_ORG_CODE,
                    CONNECT_BY_ROOT ou.ORG_NAME AS ROOT_ORG_NAME
                FROM ORGANIZATION_UNIT ou
                WHERE ou.ORG_TYPE = 'NBO'
                START WITH ou.PARENT_ID IS NULL
                CONNECT BY PRIOR ou.ID = ou.PARENT_ID
            ),
            esim_ordered_stats AS (
                SELECT
                    oh.ROOT_ORG_CODE,
                    oh.ROOT_ORG_NAME,
                    oh.ORG_CODE,
                    oh.ORG_NAME,
                    -- Số lượng thuê bao đặt hàng
                    COUNT(CASE WHEN sol.CREATED_DATE IS NOT NULL THEN 1 END) AS total_esim_ordered,
                    COUNT(CASE WHEN TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                               AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_today,
                    COUNT(CASE WHEN EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_month,
                    COUNT(CASE WHEN EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM ADD_MONTHS(TO_DATE(:endDate, 'YYYY-MM-DD'), -1)) THEN 1 END) AS total_esim_ordered_prev_month,
                    COUNT(CASE WHEN EXTRACT(YEAR FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_year
                FROM SALE_ORDER_LINE sol
                JOIN SALE_ORDER so ON so.ID = sol.SALE_ORDER_ID AND so.ORDER_TYPE = 0
                JOIN org_hierarchy oh ON oh.ID = so.ORG_ID
                GROUP BY oh.ROOT_ORG_CODE, oh.ROOT_ORG_NAME, oh.ORG_CODE, oh.ORG_NAME
            ),
            esim_activated_stats AS (
                SELECT
                    oh.ROOT_ORG_CODE,
                    oh.ROOT_ORG_NAME,
                    oh.ORG_CODE,
                    oh.ORG_NAME,
                    -- Số lượng Thuê Bao kích hoạt 900
                    COUNT(CASE WHEN sol.PAY_STATUS = 1 AND sol.MODIFIED_DATE IS NOT NULL THEN 1 END) AS total_esim_activated_900,
                    COUNT(CASE WHEN sol.PAY_STATUS = 1 AND TRUNC(FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                               AND TRUNC(FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_activated_900_today,
                    COUNT(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_activated_900_month,
                    COUNT(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM ADD_MONTHS(TO_DATE(:endDate, 'YYYY-MM-DD'), -1)) THEN 1 END) AS total_esim_activated_900_prev_month,
                    COUNT(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(YEAR FROM FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_activated_900_year
                FROM SALE_ORDER_LINE sol
                JOIN SALE_ORDER so ON so.ID = sol.SALE_ORDER_ID AND so.ORDER_TYPE = 0
                JOIN org_hierarchy oh ON oh.ID = so.ORG_ID
                GROUP BY oh.ROOT_ORG_CODE, oh.ROOT_ORG_NAME, oh.ORG_CODE, oh.ORG_NAME
            ),
            revenue_stats AS (
                SELECT
                    oh.ROOT_ORG_CODE,
                    oh.ROOT_ORG_NAME,
                    oh.ORG_CODE,
                    oh.ORG_NAME,
                    -- Doanh thu gói cước (chỉ tính eSIM đã gọi 900 và có gán gói) - từ SALE_ORDER_LINE với pay_status = 1
                    NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 THEN sol.PRICE ELSE 0 END), 0) AS total_revenue,
                    NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                               AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN sol.PRICE ELSE 0 END), 0) AS total_revenue_today,
                    NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN sol.PRICE ELSE 0 END), 0) AS total_revenue_month,
                    NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM ADD_MONTHS(TO_DATE(:endDate, 'YYYY-MM-DD'), -1)) THEN sol.PRICE ELSE 0 END), 0) AS total_revenue_prev_month,
                    NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(YEAR FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN sol.PRICE ELSE 0 END), 0) AS total_revenue_year
                FROM SALE_ORDER_LINE sol
                JOIN SALE_ORDER so ON so.ID = sol.SALE_ORDER_ID
                JOIN org_hierarchy oh ON oh.ID = so.ORG_ID
                GROUP BY oh.ROOT_ORG_CODE, oh.ROOT_ORG_NAME, oh.ORG_CODE, oh.ORG_NAME
            )
            SELECT
                oh.ROOT_ORG_CODE,
                oh.ROOT_ORG_NAME,
                oh.ORG_CODE,
                oh.ORG_NAME,
                NVL(eo.total_esim_ordered, 0) AS total_esim_ordered,
                NVL(eo.total_esim_ordered_today, 0) AS total_esim_ordered_today,
                NVL(eo.total_esim_ordered_month, 0) AS total_esim_ordered_month,
                CASE WHEN eo.total_esim_ordered_prev_month = 0 THEN NULL ELSE ROUND(((eo.total_esim_ordered_month - eo.total_esim_ordered_prev_month) / NULLIF(eo.total_esim_ordered_prev_month, 0)) * 100, 2) END AS total_esim_ordered_month_growth,
                NVL(eo.total_esim_ordered_year, 0) AS total_esim_ordered_year,
                NVL(ea.total_esim_activated_900, 0) AS total_esim_activated_900,
                NVL(ea.total_esim_activated_900_today, 0) AS total_esim_activated_900_today,
                NVL(ea.total_esim_activated_900_month, 0) AS total_esim_activated_900_month,
                CASE WHEN ea.total_esim_activated_900_prev_month = 0 THEN NULL ELSE ROUND(((ea.total_esim_activated_900_month - ea.total_esim_activated_900_prev_month) / NULLIF(ea.total_esim_activated_900_prev_month, 0)) * 100, 2) END AS total_esim_activated_900_month_growth,
                NVL(ea.total_esim_activated_900_year, 0) AS total_esim_activated_900_year,
                NVL(r.total_revenue, 0) AS total_revenue,
                NVL(r.total_revenue_today, 0) AS total_revenue_today,
                NVL(r.total_revenue_month, 0) AS total_revenue_month,
                CASE WHEN r.total_revenue_prev_month = 0 THEN NULL ELSE ROUND(((r.total_revenue_month - r.total_revenue_prev_month) / NULLIF(r.total_revenue_prev_month, 0)) * 100, 2) END AS total_revenue_month_growth,
                NVL(r.total_revenue_year, 0) AS total_revenue_year
            FROM org_hierarchy oh
            LEFT JOIN esim_ordered_stats eo ON eo.ORG_CODE = oh.ORG_CODE
            LEFT JOIN esim_activated_stats ea ON ea.ORG_CODE = oh.ORG_CODE
            LEFT JOIN revenue_stats r ON r.ORG_CODE = oh.ORG_CODE
            WHERE oh.ORG_TYPE = 'NBO'
            ORDER BY oh.ROOT_ORG_CODE, oh.ORG_CODE
        """, nativeQuery = true)
    List<Tuple> getSummaryByOrgReportDataAll(@Param("startDate") String startDate, @Param("endDate") String endDate);

    // Báo cáo tổng hợp theo từng tổ chức với phân trang
    @Query(value = """
        WITH org_hierarchy AS (
            SELECT
                ou.ID,
                ou.ORG_CODE,
                ou.ORG_NAME,
                ou.ORG_TYPE,
                ou.PARENT_ID,
                CONNECT_BY_ROOT ou.ORG_CODE AS ROOT_ORG_CODE,
                CONNECT_BY_ROOT ou.ORG_NAME AS ROOT_ORG_NAME
            FROM ORGANIZATION_UNIT ou
            WHERE ou.ORG_TYPE = 'NBO'
            START WITH ou.PARENT_ID IS NULL
            CONNECT BY PRIOR ou.ID = ou.PARENT_ID
        ),
        esim_ordered_stats AS (
            SELECT
                oh.ROOT_ORG_CODE,
                oh.ROOT_ORG_NAME,
                oh.ORG_CODE,
                oh.ORG_NAME,
                -- Số lượng thuê bao đặt hàng
                COUNT(CASE WHEN sol.CREATED_DATE IS NOT NULL THEN 1 END) AS total_esim_ordered,
                COUNT(CASE WHEN TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                           AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_today,
                COUNT(CASE WHEN EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_month,
                COUNT(CASE WHEN EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM ADD_MONTHS(TO_DATE(:endDate, 'YYYY-MM-DD'), -1)) THEN 1 END) AS total_esim_ordered_prev_month,
                COUNT(CASE WHEN EXTRACT(YEAR FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_year
            FROM SALE_ORDER_LINE sol
            JOIN SALE_ORDER so ON so.ID = sol.SALE_ORDER_ID AND so.ORDER_TYPE = 0
            JOIN org_hierarchy oh ON oh.ID = so.ORG_ID
            GROUP BY oh.ROOT_ORG_CODE, oh.ROOT_ORG_NAME, oh.ORG_CODE, oh.ORG_NAME
        ),
        esim_activated_stats AS (
            SELECT
                oh.ROOT_ORG_CODE,
                oh.ROOT_ORG_NAME,
                oh.ORG_CODE,
                oh.ORG_NAME,
                -- Số lượng Thuê Bao kích hoạt 900
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND sol.MODIFIED_DATE IS NOT NULL THEN 1 END) AS total_esim_activated_900,
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND TRUNC(FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                           AND TRUNC(FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_activated_900_today,
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_activated_900_month,
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM ADD_MONTHS(TO_DATE(:endDate, 'YYYY-MM-DD'), -1)) THEN 1 END) AS total_esim_activated_900_prev_month,
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(YEAR FROM FROM_TZ(CAST(sol.MODIFIED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_activated_900_year
            FROM SALE_ORDER_LINE sol
            JOIN SALE_ORDER so ON so.ID = sol.SALE_ORDER_ID AND so.ORDER_TYPE = 0
            JOIN org_hierarchy oh ON oh.ID = so.ORG_ID
            GROUP BY oh.ROOT_ORG_CODE, oh.ROOT_ORG_NAME, oh.ORG_CODE, oh.ORG_NAME
        ),
        revenue_stats AS (
            SELECT
                oh.ROOT_ORG_CODE,
                oh.ROOT_ORG_NAME,
                oh.ORG_CODE,
                oh.ORG_NAME,
                -- Doanh thu gói cước (chỉ tính eSIM đã gọi 900 và có gán gói) - từ SALE_ORDER_LINE với pay_status = 1
                NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 THEN sol.PRICE ELSE 0 END), 0) AS total_revenue,
                NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                           AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN sol.PRICE ELSE 0 END), 0) AS total_revenue_today,
                NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN sol.PRICE ELSE 0 END), 0) AS total_revenue_month,
                NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM ADD_MONTHS(TO_DATE(:endDate, 'YYYY-MM-DD'), -1)) THEN sol.PRICE ELSE 0 END), 0) AS total_revenue_prev_month,
                NVL(SUM(CASE WHEN sol.PAY_STATUS = 1 AND EXTRACT(YEAR FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN sol.PRICE ELSE 0 END), 0) AS total_revenue_year
            FROM SALE_ORDER_LINE sol
            JOIN SALE_ORDER so ON so.ID = sol.SALE_ORDER_ID
            JOIN org_hierarchy oh ON oh.ID = so.ORG_ID
            GROUP BY oh.ROOT_ORG_CODE, oh.ROOT_ORG_NAME, oh.ORG_CODE, oh.ORG_NAME
        )
        SELECT
            oh.ROOT_ORG_CODE,
            oh.ROOT_ORG_NAME,
            oh.ORG_CODE,
            oh.ORG_NAME,
            NVL(eo.total_esim_ordered, 0) AS total_esim_ordered,
            NVL(eo.total_esim_ordered_today, 0) AS total_esim_ordered_today,
            NVL(eo.total_esim_ordered_month, 0) AS total_esim_ordered_month,
            ROUND(((eo.total_esim_ordered_month - eo.total_esim_ordered_prev_month) / NULLIF(eo.total_esim_ordered_prev_month, 0)) * 100, 2) AS total_esim_ordered_month_growth,
            NVL(eo.total_esim_ordered_year, 0) AS total_esim_ordered_year,
            NVL(ea.total_esim_activated_900, 0) AS total_esim_activated_900,
            NVL(ea.total_esim_activated_900_today, 0) AS total_esim_activated_900_today,
            NVL(ea.total_esim_activated_900_month, 0) AS total_esim_activated_900_month,
            ROUND(((ea.total_esim_activated_900_month - ea.total_esim_activated_900_prev_month) / NULLIF(ea.total_esim_activated_900_prev_month, 0)) * 100, 2) AS total_esim_activated_900_month_growth,
            NVL(ea.total_esim_activated_900_year, 0) AS total_esim_activated_900_year,
            NVL(r.total_revenue, 0) AS total_revenue,
            NVL(r.total_revenue_today, 0) AS total_revenue_today,
            NVL(r.total_revenue_month, 0) AS total_revenue_month,
            ROUND(((r.total_revenue_month - r.total_revenue_prev_month) / NULLIF(r.total_revenue_prev_month, 0)) * 100, 2) AS total_revenue_month_growth,
            NVL(r.total_revenue_year, 0) AS total_revenue_year
        FROM org_hierarchy oh
        LEFT JOIN esim_ordered_stats eo ON eo.ORG_CODE = oh.ORG_CODE
        LEFT JOIN esim_activated_stats ea ON ea.ORG_CODE = oh.ORG_CODE
        LEFT JOIN revenue_stats r ON r.ORG_CODE = oh.ORG_CODE
        WHERE oh.ORG_TYPE = 'NBO'
        ORDER BY oh.ROOT_ORG_CODE, oh.ORG_CODE
        """,
        countQuery = """
            WITH org_hierarchy AS (
                SELECT
                    ou.ID,
                    ou.ORG_CODE,
                    ou.ORG_NAME,
                    ou.ORG_TYPE,
                    ou.PARENT_ID,
                    CONNECT_BY_ROOT ou.ORG_CODE AS ROOT_ORG_CODE,
                    CONNECT_BY_ROOT ou.ORG_NAME AS ROOT_ORG_NAME
                FROM ORGANIZATION_UNIT ou
                WHERE ou.ORG_TYPE = 'NBO'
                START WITH ou.PARENT_ID IS NULL
                CONNECT BY PRIOR ou.ID = ou.PARENT_ID
            )
            SELECT COUNT(*)
            FROM org_hierarchy oh
            WHERE oh.ORG_TYPE = 'NBO'
            """,
        nativeQuery = true)
    Page<Tuple> getSummaryByOrgReportData(@Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);

    // Báo cáo tổng hợp toàn bộ
    @Query(value = """
        WITH org_hierarchy AS (
            SELECT
                ou.ID,
                ou.ORG_CODE,
                ou.ORG_NAME,
                ou.ORG_TYPE,
                ou.PARENT_ID,
                CONNECT_BY_ROOT ou.ORG_CODE AS ROOT_ORG_CODE,
                CONNECT_BY_ROOT ou.ORG_NAME AS ROOT_ORG_NAME
            FROM ORGANIZATION_UNIT ou
            WHERE ou.ORG_TYPE = 'NBO'
            START WITH ou.PARENT_ID IS NULL
            CONNECT BY PRIOR ou.ID = ou.PARENT_ID
        ),
        org_stats AS (
            SELECT
                oh.ROOT_ORG_CODE,
                oh.ROOT_ORG_NAME,
                oh.ORG_CODE,
                oh.ORG_NAME,
                -- Số lượng thuê bao đặt hàng (tổng từ tất cả tổ chức)
                COUNT(CASE WHEN sol.CREATED_DATE IS NOT NULL THEN 1 END) AS total_esim_ordered,
                COUNT(CASE WHEN TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                           AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_today,
                COUNT(CASE WHEN EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_month,
                COUNT(CASE WHEN EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM ADD_MONTHS(TO_DATE(:endDate, 'YYYY-MM-DD'), -1)) THEN 1 END) AS total_esim_ordered_prev_month,
                COUNT(CASE WHEN EXTRACT(YEAR FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_ordered_year
            FROM SALE_ORDER_LINE sol
            JOIN SALE_ORDER so ON so.ID = sol.SALE_ORDER_ID AND so.ORDER_TYPE = 0
            JOIN org_hierarchy oh ON oh.ID = so.ORG_ID
            GROUP BY oh.ROOT_ORG_CODE, oh.ROOT_ORG_NAME, oh.ORG_CODE, oh.ORG_NAME
        ),
        purchased_stats AS (
            SELECT
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND sol.PRICE <> 0 THEN 1 END) AS total_esim_purchased,
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND sol.PRICE <> 0 AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                           AND TRUNC(FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_purchased_today,
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND sol.PRICE <> 0 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_purchased_month,
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND sol.PRICE <> 0 AND EXTRACT(MONTH FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(MONTH FROM ADD_MONTHS(TO_DATE(:endDate, 'YYYY-MM-DD'), -1)) THEN 1 END) AS total_esim_purchased_prev_month,
                COUNT(CASE WHEN sol.PAY_STATUS = 1 AND sol.PRICE <> 0 AND EXTRACT(YEAR FROM FROM_TZ(CAST(sol.CREATED_DATE AS TIMESTAMP), 'UTC') AT TIME ZONE 'Asia/Ho_Chi_Minh') = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN 1 END) AS total_esim_purchased_year
            FROM SALE_ORDER_LINE sol
        ),
        agent_stats AS (
            SELECT
                -- Số lượng Đại lý phát sinh giao dịch
                COUNT(DISTINCT so.ORG_ID) AS total_agents,
                COUNT(DISTINCT CASE WHEN TRUNC(so.ORDER_DATE) >= TRUNC(TO_DATE(:startDate, 'YYYY-MM-DD'))
                                   AND TRUNC(so.ORDER_DATE) <= TRUNC(TO_DATE(:endDate, 'YYYY-MM-DD')) THEN so.ORG_ID END) AS total_agents_today,
                COUNT(DISTINCT CASE WHEN EXTRACT(MONTH FROM so.ORDER_DATE) = EXTRACT(MONTH FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN so.ORG_ID END) AS total_agents_month,
                COUNT(DISTINCT CASE WHEN EXTRACT(YEAR FROM so.ORDER_DATE) = EXTRACT(YEAR FROM TO_DATE(:endDate, 'YYYY-MM-DD')) THEN so.ORG_ID END) AS total_agents_year
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION er ON er.ORDER_ID = so.ID
                                     AND NVL(er.FAILED_NUMBER, 0) <> NVL(so.QUANTITY, 0)
        )
        SELECT
            NVL(ag.total_agents, 0) AS total_agents,
            NVL(ag.total_agents_today, 0) AS total_agents_today,
            NVL(ag.total_agents_month, 0) AS total_agents_month,
            NVL(ag.total_agents_year, 0) AS total_agents_year,
            -- Số lượng Thuê Bao Đấu Nối = tổng Số lượng thuê bao đặt hàng của tất cả tổ chức
            NVL(SUM(os.total_esim_ordered), 0) AS total_esim_connected,
            NVL(SUM(os.total_esim_ordered_today), 0) AS total_esim_connected_today,
            NVL(SUM(os.total_esim_ordered_month), 0) AS total_esim_connected_month,
            ROUND(((NVL(SUM(os.total_esim_ordered_month), 0) - NVL(SUM(os.total_esim_ordered_prev_month), 0)) / NULLIF(NVL(SUM(os.total_esim_ordered_prev_month), 0), 0)) * 100, 2) AS total_esim_connected_month_growth,
            NVL(SUM(os.total_esim_ordered_year), 0) AS total_esim_connected_year,
            -- Số lượng Thuê Bao Mua Gói = gán gói (order_type = 1)
            NVL(ps.total_esim_purchased, 0) AS total_esim_purchased,
            NVL(ps.total_esim_purchased_today, 0) AS total_esim_purchased_today,
            NVL(ps.total_esim_purchased_month, 0) AS total_esim_purchased_month,
            ROUND(((NVL(ps.total_esim_purchased_month, 0) - NVL(ps.total_esim_purchased_prev_month, 0)) / NULLIF(NVL(ps.total_esim_purchased_prev_month, 0), 0)) * 100, 2) AS total_esim_purchased_month_growth,
            NVL(ps.total_esim_purchased_year, 0) AS total_esim_purchased_year
        FROM agent_stats ag
        CROSS JOIN purchased_stats ps
        LEFT JOIN org_stats os ON 1=1
        GROUP BY ag.total_agents, ag.total_agents_today, ag.total_agents_month, ag.total_agents_year,
                 ps.total_esim_purchased, ps.total_esim_purchased_today, ps.total_esim_purchased_month, ps.total_esim_purchased_prev_month, ps.total_esim_purchased_year
        """, nativeQuery = true)
    Tuple getSummaryAllReportData(@Param("startDate") String startDate, @Param("endDate") String endDate);
}

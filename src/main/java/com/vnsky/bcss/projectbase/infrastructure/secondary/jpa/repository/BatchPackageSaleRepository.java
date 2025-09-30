package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.BatchPackageSaleEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchPackageSaleRepository extends JpaRepository<BatchPackageSaleEntity, String> {

    @Query(value = """
            SELECT DISTINCT
                bps.ID AS ID,
                bps.FILE_URL AS FILE_URL,
                bps.RESULT_FILE_URL AS RESULT_FILE_URL,
                bps.TOTAL_NUMBER AS TOTAL_NUMBER,
                bps.FAILED_NUMBER AS FAILED_NUMBER,
                bps.SUCCEEDED_NUMBER AS SUCCEEDED_NUMBER,
                bps.PAYMENT_TYPE AS PAYMENT_TYPE,
                bps.STATUS AS STATUS,
                bps.FILE_NAME AS FILE_NAME,
                bps.CLIENT_ID AS CLIENT_ID,
                bps.TYPE AS TYPE,
                bps.ORDER_ID AS ORDER_ID,
                bps.CREATED_DATE AS CREATED_DATE,
                bps.CREATED_BY AS CREATED_BY,
                bps.FINISHED_DATE AS FINISHED_DATE,
                LISTAGG(DISTINCT sol.PCK_CODE, ',') WITHIN GROUP (ORDER BY sol.PCK_CODE)
                    OVER (PARTITION BY bps.ID) AS PCK_CODE,
                CASE
                    WHEN bps.FILE_URL IS NULL THEN sol.ISDN
                    ELSE NULL
                END AS ISDN
            FROM BATCH_PACKAGE_SALE bps
            LEFT JOIN SALE_ORDER so ON so.ID = bps.ORDER_ID
            LEFT JOIN SALE_ORDER_LINE sol ON sol.SALE_ORDER_ID = so.ID
            WHERE so.ORG_ID IN (
                 SELECT ou.ID
                 FROM ORGANIZATION_UNIT ou
                 START WITH ou.ID = :orgId
                 CONNECT BY PRIOR ou.ID = ou.PARENT_ID
              )
              AND (:q IS NULL
                   OR LOWER(bps.FILE_NAME) LIKE LOWER('%' || :q || '%')
                   OR LOWER(sol.ISDN) LIKE LOWER('%' || :q || '%'))
              AND (:saleType IS NULL OR bps.TYPE = :saleType)
              AND (:status IS NULL OR bps.STATUS = :status)
              AND (:fromDate IS NULL OR TRUNC(bps.CREATED_DATE) >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
              AND (:toDate IS NULL OR TRUNC(bps.CREATED_DATE) <= TO_DATE(:toDate, 'dd/MM/yyyy'))
            ORDER BY bps.CREATED_DATE DESC
            """,
           countQuery = """
            SELECT COUNT(DISTINCT bps.ID)
            FROM BATCH_PACKAGE_SALE bps
            LEFT JOIN SALE_ORDER so ON so.ID = bps.ORDER_ID
            LEFT JOIN SALE_ORDER_LINE sol ON sol.SALE_ORDER_ID = so.ID
            WHERE so.ORG_ID IN (
                 SELECT ou.ID
                 FROM ORGANIZATION_UNIT ou
                 START WITH ou.ID = :orgId
                 CONNECT BY PRIOR ou.ID = ou.PARENT_ID
              )
              AND (:q IS NULL
                   OR LOWER(bps.FILE_NAME) LIKE LOWER('%' || :q || '%')
                   OR LOWER(sol.ISDN) LIKE LOWER('%' || :q || '%'))
              AND (:saleType IS NULL OR bps.TYPE = :saleType)
              AND (:status IS NULL OR bps.STATUS = :status)
              AND (:fromDate IS NULL OR TRUNC(bps.CREATED_DATE) >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
              AND (:toDate IS NULL OR TRUNC(bps.CREATED_DATE) <= TO_DATE(:toDate, 'dd/MM/yyyy'))
            """,
           nativeQuery = true)
    Page<Tuple> searchBatchPackageSales(
        @Param("q") String q,
        @Param("saleType") Integer saleType,
        @Param("status") Integer status,
        @Param("fromDate") String fromDate,
        @Param("toDate") String toDate,
        @Param("orgId") String orgId,
        Pageable pageable
    );

    @Query(value = """
            SELECT DISTINCT
                bps.ID AS ID,
                bps.FILE_URL AS FILE_URL,
                bps.FILE_NAME AS FILE_NAME,
                CASE
                    WHEN bps.FILE_URL IS NULL THEN sol.ISDN
                    ELSE NULL
                END AS ISDN,
                ou.ORG_CODE AS ORG_CODE,
                ou.ORG_NAME AS ORG_NAME,
                LISTAGG(DISTINCT sol.PCK_CODE, ',') WITHIN GROUP (ORDER BY sol.PCK_CODE)
                    OVER (PARTITION BY bps.ID) AS PCK_CODE,
                so.AMOUNT_TOTAL AS AMOUNT_TOTAL,
                bps.TYPE AS TYPE,
                bps.CREATED_BY AS CREATED_BY,
                so.ORDER_DATE AS ORDER_DATE,
                count (*) over () as total
            FROM BATCH_PACKAGE_SALE bps
            JOIN SALE_ORDER so ON so.ID = bps.ORDER_ID
            JOIN ORGANIZATION_UNIT ou ON ou.ID = so.ORG_ID
            JOIN SALE_ORDER_LINE sol ON sol.SALE_ORDER_ID = so.ID
            WHERE (:q IS NULL
                   OR LOWER(bps.FILE_NAME) LIKE LOWER('%' || :q || '%')
                   OR LOWER(sol.ISDN) LIKE LOWER('%' || :q || '%'))
              AND (:type IS NULL OR bps.TYPE = :type)
              AND bps.STATUS = 2
              AND (:orgCodesIsNull = 1 OR ou.ORG_CODE IN (:orgCodes))
              AND (:startDate IS NULL OR TRUNC(so.ORDER_DATE) >= TO_DATE(:startDate, 'yyyy/MM/dd'))
              AND (:endDate IS NULL OR TRUNC(so.ORDER_DATE) <= TO_DATE(:endDate, 'yyyy/MM/dd'))
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
    List<Tuple> searchPackageReport(@Param("currentOrgCode") String currentOrgCode,
                                    @Param("q") String q,
                                    @Param("orgCodes") List<String> orgCodes,
                                    @Param("orgCodesIsNull") int orgCodesIsNull,
                                    @Param("type") Integer type,
                                    @Param("startDate") String startDate,
                                    @Param("endDate") String endDate,
                                    @Param("offset") long offset,
                                    @Param("pageSize") int pageSize);

    @Query(value = """
            SELECT DISTINCT
                bps.ID AS ID,
                bps.FILE_URL AS FILE_URL,
                CASE
                    WHEN bps.FILE_URL IS NULL THEN sol.ISDN
                    ELSE NULL
                END AS ISDN,
                ou.ORG_CODE AS ORG_CODE,
                ou.ORG_NAME AS ORG_NAME,
                LISTAGG(DISTINCT sol.PCK_CODE, ',') WITHIN GROUP (ORDER BY sol.PCK_CODE)
                    OVER (PARTITION BY bps.ID) AS PCK_CODE,
                so.AMOUNT_TOTAL AS AMOUNT_TOTAL,
                bps.TYPE AS TYPE,
                bps.CREATED_BY AS CREATED_BY,
                so.ORDER_DATE AS ORDER_DATE
            FROM BATCH_PACKAGE_SALE bps
            JOIN SALE_ORDER so ON so.ID = bps.ORDER_ID
            JOIN ORGANIZATION_UNIT ou ON ou.ID = so.ORG_ID
            JOIN SALE_ORDER_LINE sol ON sol.SALE_ORDER_ID = so.ID
            WHERE (:q IS NULL
                   OR LOWER(bps.FILE_NAME) LIKE LOWER('%' || :q || '%')
                   OR LOWER(sol.ISDN) LIKE LOWER('%' || :q || '%'))
              AND (:type IS NULL OR bps.TYPE = :type)
              AND bps.STATUS = 2
              AND (:orgCodesIsNull = 1 OR ou.ORG_CODE IN (:orgCodes))
              AND (:startDate IS NULL OR TRUNC(so.ORDER_DATE) >= TO_DATE(:startDate, 'yyyy/MM/dd'))
              AND (:endDate IS NULL OR TRUNC(so.ORDER_DATE) <= TO_DATE(:endDate, 'yyyy/MM/dd'))
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
    List<Tuple> getPackageReport(@Param("currentOrgCode") String currentOrgCode,
                                 @Param("q") String q,
                                 @Param("orgCodes") List<String> orgCodes,
                                 @Param("orgCodesIsNull") int orgCodesIsNull,
                                 @Param("type") Integer type,
                                 @Param("startDate") String startDate,
                                 @Param("endDate") String endDate);
}

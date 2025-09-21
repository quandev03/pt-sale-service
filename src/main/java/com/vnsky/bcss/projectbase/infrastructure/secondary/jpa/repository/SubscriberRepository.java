package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.SubscriberEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<SubscriberEntity, String> {
    Optional<SubscriberEntity> findBySerialAndStatus(String serial, int status);

    @Query(value = """
        Select * from SUBSCRIBER
        where ISDN = :isdn
        order by CREATED_DATE desc
        fetch first row only
    """, nativeQuery = true)
    Optional<SubscriberEntity> findByLastIsdn(Long isdn);

    @Query(value = """
        Select * from SUBSCRIBER
        where SERIAL = :serial
        order by CREATED_DATE desc
        fetch first row only
        """, nativeQuery = true)
    Optional<SubscriberEntity> findByLastSerial(String serial);

    Optional<SubscriberEntity> findByImsi(Long imsi);

    @Query(value = "SELECT s FROM SubscriberEntity s WHERE s.serial IS NULL ORDER BY s.id ASC")
    List<SubscriberEntity> findSubscriberToBookEsim();

    @Query(
        value = """


            WITH org_tree AS (
              SELECT ou2.ID
              FROM ORGANIZATION_UNIT ou2
              START WITH ou2.ID = :orgId
              CONNECT BY PRIOR ou2.ID = ou2.PARENT_ID   -- lấy toàn bộ cấp dưới của :orgId
            )
            SELECT
              s.ISDN AS ISDN,
              s.SERIAL,
              s.PACK_CODE,
              so.ORDER_NO,
              ou.ORG_CODE,
              ou.ORG_NAME AS ORG_NAME,
              s.STATUS,
              s.STATUS_900,
              s.ACTIVE_STATUS,
              s.MODIFIED_DATE,
              s.GEN_QR_BY,
              s.ID AS SUB_ID,
              CASE
                WHEN s.VERIFIED_STATUS = 1 THEN 3
                WHEN s.STATUS_900      = 1 THEN 2
                WHEN s.BOUGHT_STATUS   = 1 THEN 1
                WHEN s.BOUGHT_STATUS   = 0 THEN 0
                ELSE NULL
              END AS STATUS_SUB,
              ou.ID
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
            JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
            JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
            JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
            WHERE
                so.ORG_ID IN (
                    SELECT ID
                    FROM org_tree
                    WHERE :orgIdSearch IS NULL OR ID = :orgIdSearch
                )
              AND (
                  :subStatus IS NULL
                  OR (:subStatus = 0 AND s.BOUGHT_STATUS   = 0 AND s.STATUS_900      = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 1 AND s.BOUGHT_STATUS   = 1 AND s.STATUS_900      = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 2 AND s.STATUS_900      = 1 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1)
              )
              AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
              AND ( :pckCode IS NULL OR erl.PCK_CODE = :pckCode )
              AND (
                  :textSearch IS NULL
                  OR s.ISDN   LIKE '%' || :textSearch || '%'
                  OR s.SERIAL LIKE '%' || :textSearch || '%'
                  OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
              )
              AND erl.STATUS = 1
              ORDER BY s.MODIFIED_DATE DESC
  """,
        countQuery = """
            WITH org_tree AS (
              SELECT ou2.ID
              FROM ORGANIZATION_UNIT ou2
              START WITH ou2.ID = :orgId
              CONNECT BY PRIOR ou2.ID = ou2.PARENT_ID
            )
            SELECT COUNT(*)
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
            JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
            JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
            JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
            WHERE
              so.ORG_ID IN (
                    SELECT ID
                    FROM org_tree
                    WHERE :orgIdSearch IS NULL OR ID = :orgIdSearch
                )
              AND (
                  :subStatus IS NULL
                  OR (:subStatus = 0 AND s.BOUGHT_STATUS = 0 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 1 AND s.BOUGHT_STATUS = 1 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 2 AND s.STATUS_900    = 1 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1)
              )
              AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
              AND ( :pckCode IS NULL OR erl.PCK_CODE = :pckCode )
              AND (
                  :textSearch IS NULL
                  OR s.ISDN   LIKE '%' || :textSearch || '%'
                  OR s.SERIAL LIKE '%' || :textSearch || '%'
                  OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
              )
              AND erl.STATUS = 1
              ORDER BY s.MODIFIED_DATE DESC
                      """,
        nativeQuery = true
    )
    Page<Tuple> getListEsimInfor(
        @Param("textSearch") String textSearch,
        @Param("subStatus") Integer subStatus,
        @Param("activeStatus") Integer activeStatus,
        @Param("orgId") String orgId,
        @Param("pckCode") String pckCode,
        @Param("orgIdSearch") String orgIdSearch,
        Pageable pageable
    );

    @Query(
        value = """
            SELECT
              s.ISDN AS ISDN,
              s.SERIAL,
              s.PACK_CODE,
              so.ORDER_NO,
              ou.ORG_CODE,
              ou.ORG_NAME AS ORG_NAME,
              s.STATUS,
              s.STATUS_900,
              s.ACTIVE_STATUS,
              s.MODIFIED_DATE,
              s.GEN_QR_BY,
              s.ID AS SUB_ID,
              CASE
                WHEN s.VERIFIED_STATUS = 1 THEN 3
                WHEN s.STATUS_900      = 1 THEN 2
                WHEN s.BOUGHT_STATUS   = 1 THEN 1
                WHEN s.BOUGHT_STATUS   = 0 THEN 0
                ELSE NULL
              END AS STATUS_SUB,
              ou.ID
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
            JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
            JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
            JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
            WHERE ( :orgId IS NULL OR ou.ID = :orgId )
            AND (
                                			      :subStatus IS NULL
                                			      OR ((:subStatus = 0 AND s.BOUGHT_STATUS = 0 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                                			      OR (:subStatus = 1 AND s.BOUGHT_STATUS = 1 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                                			      OR (:subStatus = 2 AND s.STATUS_900    = 1 AND s.VERIFIED_STATUS = 0)
                                			      OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1))
                                			  )
                                			  AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
                                			  AND ( :pckCode IS NULL OR erl.PCK_CODE = :pckCode )
                                			  AND (
                                			      :textSearch IS NULL
                                			      OR s.ISDN   LIKE '%' || :textSearch || '%'
                                			      OR s.SERIAL LIKE '%' || :textSearch || '%'
                                			      OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
                                			  )
                                			  AND erl.STATUS = 1
              ORDER BY s.MODIFIED_DATE DESC
  """,
        countQuery = """
            SELECT COUNT(*)
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
            JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
            JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
            JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
            AND (
            :subStatus IS NULL
                OR ((:subStatus = 0 AND s.BOUGHT_STATUS = 0 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                OR (:subStatus = 1 AND s.BOUGHT_STATUS = 1 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                OR (:subStatus = 2 AND s.STATUS_900    = 1 AND s.VERIFIED_STATUS = 0)
                OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1))
            )
            AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
            AND ( :pckCode IS NULL OR erl.PCK_CODE = :pckCode )
            AND (
                :textSearch IS NULL
                OR s.ISDN   LIKE '%' || :textSearch || '%'
                OR s.SERIAL LIKE '%' || :textSearch || '%'
                OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
            )
            AND erl.STATUS = 1
            ORDER BY s.MODIFIED_DATE DESC
                      """,
        nativeQuery = true
    )
    Page<Tuple> getListEsimInforInternal(
        @Param("textSearch") String textSearch,
        @Param("subStatus") Integer subStatus,
        @Param("activeStatus") Integer activeStatus,
        @Param("orgId") String orgId,
        @Param("pckCode") String pckCode,
        Pageable pageable
    );

    @Query(
        value = """
        SELECT s.FULL_NAME, s.GENDER, s.ID_NO_EXPIRED_DATE, s.DATE_OF_BIRTH , s.ID_NO_ISSUED_PLACE, s.NATIONALITY, s.CUSTOMER_CODE, s.CONTRACT_CODE, s.ID_NUMBER\s
        FROM SUBSCRIBER s
        WHERE s.ID =:subId
    """, nativeQuery = true
    )
    Tuple getEsimDetailById(@Param("subId") String subId);

    @Query(value = """
        SELECT CASE WHEN EXISTS (
            SELECT so.* FROM SUBSCRIBER s
        	JOIN ESIM_REGISTRATION_LINE erl ON erl.ISDN = s.ISDN
        	JOIN ESIM_REGISTRATION er ON er.ID = erl.ESIM_REGISTRATION_ID
        	JOIN SALE_ORDER so ON so.ID = er.ORDER_ID
        	WHERE s.ISDN = :isdn
        	AND erl.STATUS = 1
        	AND so.ORG_ID = :orgId
        ) THEN 1 ELSE 0 END AS result
        FROM dual
    """, nativeQuery = true)
    int isEsimBelongToAgent(@Param("isdn") Long isdn, @Param("orgId") String orgId);


    @Query(
        value = """


            WITH org_tree AS (
              SELECT ou2.ID
              FROM ORGANIZATION_UNIT ou2
              START WITH ou2.ID = :orgId
              CONNECT BY PRIOR ou2.ID = ou2.PARENT_ID   -- lấy toàn bộ cấp dưới của :orgId
            )
            SELECT
              s.ISDN AS ISDN,
              s.SERIAL,
              s.PACK_CODE,
              so.ORDER_NO,
              ou.ORG_CODE,
              ou.ORG_NAME AS ORG_NAME,
              s.STATUS,
              s.STATUS_900,
              s.ACTIVE_STATUS,
              s.MODIFIED_DATE,
              s.GEN_QR_BY,
              s.ID AS SUB_ID,
              CASE
                WHEN s.VERIFIED_STATUS = 1 THEN 3
                WHEN s.STATUS_900      = 1 THEN 2
                WHEN s.BOUGHT_STATUS   = 1 THEN 1
                WHEN s.BOUGHT_STATUS   = 0 THEN 0
                ELSE NULL
              END AS STATUS_SUB,
              ou.ID,
              s.LPA
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
            JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
            JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
            JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
            WHERE
              so.ORG_ID IN (
                    SELECT ID
                    FROM org_tree
                    WHERE :orgIdSearch IS NULL OR ID = :orgIdSearch
                )
              AND (
                  :subStatus IS NULL
                  OR (:subStatus = 0 AND s.BOUGHT_STATUS   = 0 AND s.STATUS_900      = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 1 AND s.BOUGHT_STATUS   = 1 AND s.STATUS_900      = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 2 AND s.STATUS_900      = 1 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1)
              )
              AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
              AND ( :pckCode IS NULL OR erl.PCK_CODE = :pckCode )
              AND (
                  :textSearch IS NULL
                  OR s.ISDN   LIKE '%' || :textSearch || '%'
                  OR s.SERIAL LIKE '%' || :textSearch || '%'
                  OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
              )
              AND erl.STATUS = 1
              ORDER BY s.MODIFIED_DATE DESC
  """, nativeQuery = true
    )
    List<Tuple> getListEsimInforExport(
        @Param("textSearch") String textSearch,
        @Param("subStatus") Integer subStatus,
        @Param("activeStatus") Integer activeStatus,
        @Param("orgId") String orgId,
        @Param("pckCode") String pckCode,
        @Param("orgIdSearch") String orgIdSearch
    );

    @Query(
        value = """
SELECT
              s.ISDN AS ISDN,
              s.SERIAL,
              s.PACK_CODE,
              so.ORDER_NO,
              ou.ORG_CODE,
              ou.ORG_NAME AS ORG_NAME,
              s.STATUS,
              s.STATUS_900,
              s.ACTIVE_STATUS,
              s.MODIFIED_DATE,
              s.GEN_QR_BY,
              s.ID AS SUB_ID,
              CASE
                WHEN s.VERIFIED_STATUS = 1 THEN 3
                WHEN s.STATUS_900      = 1 THEN 2
                WHEN s.BOUGHT_STATUS   = 1 THEN 1
                WHEN s.BOUGHT_STATUS   = 0 THEN 0
                ELSE NULL
              END AS STATUS_SUB,
              ou.ID
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
            JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
            JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
            JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
            WHERE :orgId IS NULL OR ou.ID = :orgId
              AND (
                  :subStatus IS NULL
                  OR (:subStatus = 0 AND s.BOUGHT_STATUS = 0 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 1 AND s.BOUGHT_STATUS = 1 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 2 AND s.STATUS_900    = 1 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1)
              )
              AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
              AND ( :pckCode IS NULL OR erl.PCK_CODE = :pckCode )
              AND (
                  :textSearch IS NULL
                  OR s.ISDN   LIKE '%' || :textSearch || '%'
                  OR s.SERIAL LIKE '%' || :textSearch || '%'
                  OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
              )
              AND erl.STATUS = 1
                ORDER BY s.MODIFIED_DATE DESC
  """, nativeQuery = true
    )
    List<Tuple> getListEsimInforExportInternal(
        @Param("textSearch") String textSearch,
        @Param("subStatus") Integer subStatus,
        @Param("activeStatus") Integer activeStatus,
        @Param("orgId") String orgId,
        @Param("pckCode") String pckCode
    );
}

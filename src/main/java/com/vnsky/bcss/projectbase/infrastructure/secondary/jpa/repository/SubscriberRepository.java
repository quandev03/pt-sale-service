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

    @Query(value = """
        select SUBSCRIBER_CUSTOMER_CODE.nextval as SUBSCRIBER_CUSTOMER_CODE,
        SUBSCRIBER_CONTRACT_CODE.nextval as SUBSCRIBER_CONTRACT_CODE
        from dual
    """, nativeQuery = true)
    Tuple getNextContractCode();

    @Query(value = """
        SELECT count (*) over () as total, s.ID, s.ISDN, s.IMSI, s.SERIAL, s.VERIFIED_STATUS, s.ACTIVE_STATUS, s.STATUS, s.STATUS_900, s.BOUGHT_STATUS, ou.ORG_CODE, ou.ORG_NAME
        FROM SUBSCRIBER s
        LEFT JOIN ORGANIZATION_UNIT ou ON s.ORG_ID = ou.ID
        WHERE (:q IS NULL OR s.ISDN LIKE '%' || :q || '%')
            AND (:status IS NULL OR
                CASE
                     WHEN s.VERIFIED_STATUS = 1 THEN 4
                     WHEN s.STATUS_900      = 1 THEN 3
                     WHEN s.BOUGHT_STATUS   = 1 THEN 2
                     WHEN s.STATUS          = 1 THEN 1
                     WHEN s.ACTIVE_STATUS   = 1 THEN 0
                     ELSE 0
                 END = :status)
            AND (:orgCode IS NULL OR ou.ORG_CODE = :orgCode)
        OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY
        """, nativeQuery = true)
    List<Tuple> searchSubscriber(
        @Param("q") String q,
        @Param("status") Integer status,
        @Param("orgCode") String orgCode,
        @Param("offset") long offset,
        @Param("pageSize") int pageSize
    );

    @Query(value = """
        select
        CASE WHEN
            EXISTS(
                Select id from SUBSCRIBER
                      where (:contractCode is null or CONTRACT_CODE = :contractCode)
                      and (:customerCode is null or CUSTOMER_CODE = :customerCode)
            ) THEN 'true'
            else 'false'
        end
        from dual
    """, nativeQuery = true)
    boolean isExistByContractCodeOrCustomerCode(String contractCode, String customerCode);

    @Query(value = """
        SELECT count (*) over () as total, s.ID, s.ISDN, s.IMSI, s.SERIAL, s.VERIFIED_STATUS, s.ACTIVE_STATUS, s.STATUS, s.STATUS_900, s.BOUGHT_STATUS, ou.ORG_CODE, ou.ORG_NAME
        FROM SUBSCRIBER s
        LEFT JOIN ORGANIZATION_UNIT ou ON s.ORG_ID = ou.ID
        WHERE (:q IS NULL OR s.ISDN LIKE '%' || :q || '%')
            AND (:status IS NULL OR
                CASE
                     WHEN s.VERIFIED_STATUS = 1 THEN 4
                     WHEN s.STATUS_900      = 1 THEN 3
                     WHEN s.BOUGHT_STATUS   = 1 THEN 2
                     WHEN s.STATUS          = 1 THEN 1
                     WHEN s.ACTIVE_STATUS   = 1 THEN 0
                     ELSE 0
                 END = :status)
            AND (:orgCode IS NULL OR ou.ORG_CODE = :orgCode)
        """, nativeQuery = true)
    List<Tuple> getSubscriber(
        @Param("q") String q,
        @Param("status") Integer status,
        @Param("orgCode") String orgCode
    );

    @Query(value = """
                SELECT
                    s.ID AS ID,
                    s.CONTRACT_CODE AS CONTRACT_CODE,
                    s.CUSTOMER_CODE AS CUSTOMER_CODE,
                    s.FULL_NAME AS FULL_NAME,
                    s.ISDN AS ISDN,
                    s.SERIAL AS SERIAL,
                    s.PACK_CODE AS PACK_CODE,
                    s.ID_NUMBER AS ID_NUMBER,
                    s.ACTIVE_STATUS AS ACTIVE_STATUS,
                    s.NATIONALITY AS NATIONALITY,
                    s.GENDER AS GENDER,
                    s.DATE_OF_BIRTH AS DATE_OF_BIRTH,
                    s.UPDATE_INFO_BY AS UPDATE_INFO_BY,
                    s.UPDATE_INFO_DATE AS UPDATE_INFO_DATE,
                    ou.ORG_CODE AS ORG_CODE,
                    ou.ORG_NAME AS ORG_NAME,
                    count (*) over () as total
                FROM
                    SUBSCRIBER s
                JOIN
                    ORGANIZATION_UNIT ou ON s.ORG_ID = ou.ID
                WHERE
                    (:q IS NULL OR LOWER(s.CONTRACT_CODE) LIKE LOWER('%' || :q || '%') OR LOWER(s.CUSTOMER_CODE) LIKE LOWER('%' || :q || '%') OR LOWER(s.ISDN) LIKE LOWER('%' || :q || '%'))
                    AND (:orgCodesIsNull = 1 OR ou.ORG_CODE IN (:orgCodes))
                    AND (:startDate IS NULL OR TRUNC(s.UPDATE_INFO_DATE) >= TO_DATE(:startDate, 'yyyy/MM/dd'))
                    AND (:endDate IS NULL OR TRUNC(s.UPDATE_INFO_DATE) <= TO_DATE(:endDate, 'yyyy/MM/dd'))
                    AND s.VERIFIED_STATUS = 1
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
                ORDER BY s.UPDATE_INFO_DATE DESC
                OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY
        """, nativeQuery = true)
    List<Tuple> searchSubscriberReport(@Param("currentOrgCode") String currentOrgCode,
                                       @Param("q") String q,
                                       @Param("orgCodes") List<String> orgCodes,
                                       @Param("orgCodesIsNull") int orgCodesIsNull,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate,
                                       @Param("offset") long offset,
                                       @Param("pageSize") int pageSize);

    @Query(value = """
                SELECT
                    s.ID AS ID,
                    s.CONTRACT_CODE AS CONTRACT_CODE,
                    s.CUSTOMER_CODE AS CUSTOMER_CODE,
                    s.FULL_NAME AS FULL_NAME,
                    s.ISDN AS ISDN,
                    s.SERIAL AS SERIAL,
                    s.PACK_CODE AS PACK_CODE,
                    s.ID_NUMBER AS ID_NUMBER,
                    s.ACTIVE_STATUS AS ACTIVE_STATUS,
                    s.NATIONALITY AS NATIONALITY,
                    s.GENDER AS GENDER,
                    s.DATE_OF_BIRTH AS DATE_OF_BIRTH,
                    s.UPDATE_INFO_BY AS UPDATE_INFO_BY,
                    s.UPDATE_INFO_DATE AS UPDATE_INFO_DATE,
                    ou.ORG_CODE AS ORG_CODE,
                    ou.ORG_NAME AS ORG_NAME
                FROM
                    SUBSCRIBER s
                JOIN
                    ORGANIZATION_UNIT ou ON s.ORG_ID = ou.ID
                WHERE
                    (:q IS NULL OR LOWER(s.CONTRACT_CODE) LIKE LOWER('%' || :q || '%') OR LOWER(s.CUSTOMER_CODE) LIKE LOWER('%' || :q || '%') OR LOWER(s.ISDN) LIKE LOWER('%' || :q || '%'))
                    AND (:orgCodesIsNull = 1 OR ou.ORG_CODE IN (:orgCodes))
                    AND (:startDate IS NULL OR TRUNC(s.UPDATE_INFO_DATE) >= TO_DATE(:startDate, 'yyyy/MM/dd'))
                    AND (:endDate IS NULL OR TRUNC(s.UPDATE_INFO_DATE) <= TO_DATE(:endDate, 'yyyy/MM/dd'))
                    AND s.VERIFIED_STATUS = 1
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
                ORDER BY s.UPDATE_INFO_DATE DESC
        """, nativeQuery = true)
    List<Tuple> getSubscriberReport(@Param("currentOrgCode") String currentOrgCode,
                                    @Param("q") String q,
                                    @Param("orgCodes") List<String> orgCodes,
                                    @Param("orgCodesIsNull") int orgCodesIsNull,
                                    @Param("startDate") String startDate,
                                    @Param("endDate") String endDate);

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
                          so.CREATED_DATE
                        FROM SALE_ORDER so
                        JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
                        JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
                        JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
                        JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
                        WHERE
                            so.ORG_ID IN (
                                SELECT ID
                                FROM org_tree
                                WHERE :isFilter = 0 OR ID IN (:orgIdSearch)
                            )
                          AND (
                              :subStatus IS NULL
                              OR (:subStatus = 0 AND s.BOUGHT_STATUS   = 0 AND s.STATUS_900      = 0 AND s.VERIFIED_STATUS = 0)
                              OR (:subStatus = 1 AND s.BOUGHT_STATUS   = 1 AND s.STATUS_900      = 0 AND s.VERIFIED_STATUS = 0)
                              OR (:subStatus = 2 AND s.STATUS_900      = 1 AND (s.VERIFIED_STATUS = 0 OR s.VERIFIED_STATUS IS NULL))
                              OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1)
                          )
                          AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
                          AND ( :pckCode IS NULL OR s.PACK_CODE = :pckCode )
                          AND (
                              :textSearch IS NULL
                              OR s.ISDN   LIKE '%' || :textSearch || '%'
                              OR s.SERIAL LIKE '%' || :textSearch || '%'
                              OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
                          )
                          AND erl.STATUS = 1
                          AND (:fromDate IS NULL OR so.CREATED_DATE >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
                          AND (:toDate IS NULL OR so.CREATED_DATE < TO_DATE(:toDate, 'dd/MM/yyyy') + 1)
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
                    WHERE :isFilter = 0 OR ID IN (:orgIdSearch)
            )
              AND (
                  :subStatus IS NULL
                  OR (:subStatus = 0 AND s.BOUGHT_STATUS = 0 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 1 AND s.BOUGHT_STATUS = 1 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 2 AND s.STATUS_900      = 1 AND (s.VERIFIED_STATUS = 0 OR s.VERIFIED_STATUS IS NULL))
                  OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1)
              )
              AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
              AND ( :pckCode IS NULL OR s.PACK_CODE = :pckCode )
              AND (
                  :textSearch IS NULL
                  OR s.ISDN   LIKE '%' || :textSearch || '%'
                  OR s.SERIAL LIKE '%' || :textSearch || '%'
                  OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
              )
              AND erl.STATUS = 1
              AND (:fromDate IS NULL OR so.CREATED_DATE >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
              AND (:toDate IS NULL OR so.CREATED_DATE < TO_DATE(:toDate, 'dd/MM/yyyy') + 1)
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
        @Param("orgIdSearch") List<String> orgIdSearch,
        @Param("isFilter") int  isFilter,
        @Param("fromDate") String fromDate,
        @Param("toDate") String toDate,
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
                          ou.ID,
                          so.CREATED_DATE
                        FROM SALE_ORDER so
                        JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
                        JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
                        JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
                        JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
                        WHERE ( :isFilter = 0 OR ou.ID IN :orgId )
                        AND (
                                            			      :subStatus IS NULL
                                            			      OR ((:subStatus = 0 AND s.BOUGHT_STATUS = 0 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                                            			      OR (:subStatus = 1 AND s.BOUGHT_STATUS = 1 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                                            			      OR (:subStatus = 2 AND s.STATUS_900      = 1 AND (s.VERIFIED_STATUS = 0 OR s.VERIFIED_STATUS IS NULL))
                                            			      OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1))
                                            			  )
                                            			  AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
                                            			  AND ( :pckCode IS NULL OR s.PACK_CODE = :pckCode )
                                            			  AND (
                                            			      :textSearch IS NULL
                                            			      OR s.ISDN   LIKE '%' || :textSearch || '%'
                                            			      OR s.SERIAL LIKE '%' || :textSearch || '%'
                                            			      OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
                                            			  )
                                            			  AND erl.STATUS = 1
                          AND (:fromDate IS NULL OR so.CREATED_DATE >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
                          AND (:toDate IS NULL OR so.CREATED_DATE < TO_DATE(:toDate, 'dd/MM/yyyy') +1)
                          ORDER BY s.MODIFIED_DATE DESC
  """,
        countQuery = """
            SELECT COUNT(*)
            FROM SALE_ORDER so
                        JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
                        JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
                        JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
                        JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
                        WHERE ( :isFilter = 0 OR ou.ID IN :orgId )
                        AND (
                                            			      :subStatus IS NULL
                                            			      OR ((:subStatus = 0 AND s.BOUGHT_STATUS = 0 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                                            			      OR (:subStatus = 1 AND s.BOUGHT_STATUS = 1 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                                            			      OR (:subStatus = 2 AND s.STATUS_900      = 1 AND (s.VERIFIED_STATUS = 0 OR s.VERIFIED_STATUS IS NULL))
                                            			      OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1))
                                            			  )
                                            			  AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
                                            			  AND ( :pckCode IS NULL OR s.PACK_CODE = :pckCode )
                                            			  AND (
                                            			      :textSearch IS NULL
                                            			      OR s.ISDN   LIKE '%' || :textSearch || '%'
                                            			      OR s.SERIAL LIKE '%' || :textSearch || '%'
                                            			      OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
                                            			  )
                                            			  AND erl.STATUS = 1
                          AND (:fromDate IS NULL OR so.CREATED_DATE >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
                          AND (:toDate IS NULL OR so.CREATED_DATE < TO_DATE(:toDate, 'dd/MM/yyyy') + 1)
                          ORDER BY s.MODIFIED_DATE DESC
                      """,
        nativeQuery = true
    )
    Page<Tuple> getListEsimInforInternal(
        @Param("textSearch") String textSearch,
        @Param("subStatus") Integer subStatus,
        @Param("activeStatus") Integer activeStatus,
        @Param("orgId") List<String> orgId,
        @Param("pckCode") String pckCode,
        @Param("isFilter") int isFilter,
        @Param("fromDate") String fromDate,
        @Param("toDate") String toDate,
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
              s.LPA,
             so.CREATED_DATE
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
            JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
            JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
            JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
            WHERE
              so.ORG_ID IN (
                    SELECT ID
                    FROM org_tree
                    WHERE :isFilter = 0 OR ID IN :orgIdSearch
                )
              AND (
                  :subStatus IS NULL
                  OR (:subStatus = 0 AND s.BOUGHT_STATUS   = 0 AND s.STATUS_900      = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 1 AND s.BOUGHT_STATUS   = 1 AND s.STATUS_900      = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 2 AND s.STATUS_900      = 1 AND (s.VERIFIED_STATUS = 0 OR s.VERIFIED_STATUS IS NULL))                  OR (:subStatus = 3 AND s.VERIFIED_STATUS = 1)
              )
              AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
              AND ( :pckCode IS NULL OR s.PACK_CODE = :pckCode )
              AND (
                  :textSearch IS NULL
                  OR s.ISDN   LIKE '%' || :textSearch || '%'
                  OR s.SERIAL LIKE '%' || :textSearch || '%'
                  OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
              )
              AND erl.STATUS = 1
              AND (:fromDate IS NULL OR so.CREATED_DATE >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
              AND (:toDate IS NULL OR so.CREATED_DATE < TO_DATE(:toDate, 'dd/MM/yyyy') + 1)
              ORDER BY s.MODIFIED_DATE DESC
  """, nativeQuery = true
    )
    List<Tuple> getListEsimInforExport(
        @Param("textSearch") String textSearch,
        @Param("subStatus") Integer subStatus,
        @Param("activeStatus") Integer activeStatus,
        @Param("orgId") String orgId,
        @Param("pckCode") String pckCode,
        @Param("orgIdSearch") List<String> orgIdSearch,
        @Param("isFilter") int isFilter,
        @Param("fromDate") String fromDate,
        @Param("toDate") String toDate
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
              ou.ID,
              so.CREATED_DATE
            FROM SALE_ORDER so
            JOIN ESIM_REGISTRATION       er  ON so.ID = er.ORDER_ID
            JOIN ORGANIZATION_UNIT       ou  ON ou.ID = so.ORG_ID
            JOIN ESIM_REGISTRATION_LINE  erl ON er.ID = erl.ESIM_REGISTRATION_ID
            JOIN SUBSCRIBER              s   ON s.ISDN = erl.ISDN
            WHERE (:isFilter = 0 OR ou.ID IN :orgId)
              AND (
                  :subStatus IS NULL
                  OR (:subStatus = 0 AND s.BOUGHT_STATUS = 0 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 1 AND s.BOUGHT_STATUS = 1 AND s.STATUS_900 = 0 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 2 AND s.STATUS_900    = 1 AND s.VERIFIED_STATUS = 0)
                  OR (:subStatus = 2 AND s.STATUS_900      = 1 AND (s.VERIFIED_STATUS = 0 OR s.VERIFIED_STATUS IS NULL))
              )
              AND ( :activeStatus IS NULL OR s.ACTIVE_STATUS = :activeStatus )
              AND ( :pckCode IS NULL OR s.PACK_CODE = :pckCode )
              AND (
                  :textSearch IS NULL
                  OR s.ISDN   LIKE '%' || :textSearch || '%'
                  OR s.SERIAL LIKE '%' || :textSearch || '%'
                  OR TO_CHAR(so.ORDER_NO) LIKE '%' || :textSearch || '%'
              )
              AND (:fromDate IS NULL OR so.CREATED_DATE >= TO_DATE(:fromDate, 'dd/MM/yyyy'))
              AND (:toDate IS NULL OR so.CREATED_DATE < TO_DATE(:toDate, 'dd/MM/yyyy') + 1)
              AND erl.STATUS = 1
                ORDER BY s.MODIFIED_DATE DESC
  """, nativeQuery = true
    )
    List<Tuple> getListEsimInforExportInternal(
        @Param("textSearch") String textSearch,
        @Param("subStatus") Integer subStatus,
        @Param("activeStatus") Integer activeStatus,
        @Param("orgId") List<String> orgId,
        @Param("pckCode") String pckCode,
        @Param("isFilter") int isFilter,
        @Param("fromDate") String fromDate,
        @Param("toDate") String toDate
    );

    @Query(value = """
        SELECT pp.PCK_CODE, pp.PCK_NAME, s.ISDN, s.SERIAL, s.LPA,   s.ID  FROM SUBSCRIBER s\s
        JOIN PACKAGE_PROFILE pp ON pp.PCK_CODE = s.PACK_CODE
        WHERE s.ID IN :subIds
    """, nativeQuery = true)
    List<Tuple> getListEsimExportQr(@Param("subIds") List<String> subIds);

    List<SubscriberEntity> findByIdIn(List<String> subIds);
}

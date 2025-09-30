package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.StockIsdnEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StockIsdnRepository extends JpaRepository<StockIsdnEntity, String> {

    @Query(value = "SELECT s FROM StockIsdnEntity s WHERE s.status = 1 AND s.activeDatetime is null ORDER BY s.id ASC")
    List<StockIsdnEntity> findAvailableIsdns();

    Optional<StockIsdnEntity> findByIsdn(Long isdn);

    Optional<StockIsdnEntity> findBySerial(Long serial);

    @Modifying(flushAutomatically = true)
    @Query(value = """
            update STOCK_ISDN set TRANSFER_STATUS = :transferStatus, TRANSFER_TYPE = :transferType where ISDN in (:isdnList)
        """, nativeQuery = true)
    void updateTransferStatusIsdnIn(@Param("transferStatus") Integer transferStatus,
                                    @Param("transferType") Integer transferType,
                                    @Param("isdnList") List<Long> isdnList);

    @Modifying(flushAutomatically = true)
    @Query(value = """
            update STOCK_ISDN set ID = :stockId where ISDN in (:isdnList)
        """, nativeQuery = true)
    void createStockIsdnIn(@Param("isdnList") List<Long> isdnList);

    List<StockIsdnEntity> findByIsdnIn(List<Long> isdns);

    List<StockIsdnEntity> findByIsdnIn(Set<Long> isdns);

    @Query(value = """
    SELECT COUNT(*) OVER() AS total,
           si.ID as ID, si.ISDN as ISDN, ou.ORG_CODE as ORG_CODE, ou.ORG_NAME as ORG_NAME,
           MAX(CASE
                   WHEN s.VERIFIED_STATUS = 1 THEN 5
                   WHEN s.STATUS_900 = 1      THEN 4
                   WHEN s.BOUGHT_STATUS = 1   THEN 3
                   WHEN s.BOUGHT_STATUS = 0   THEN 2
                   WHEN si.STATUS = 1         THEN 1
                   ELSE 1
               END) AS UI_STATUS
    FROM STOCK_ISDN si
    LEFT JOIN SUBSCRIBER s ON s.ISDN = si.ISDN
    LEFT JOIN ORGANIZATION_UNIT ou ON ou.ORG_TYPE = 'NBO' AND ou.ID = s.ORG_ID
    WHERE (:q IS NULL OR ('0'||TO_CHAR(si.ISDN)) LIKE '%'||:q||'%')
      AND (:orgCode IS NULL OR ou.ORG_CODE = :orgCode)
    GROUP BY si.ID, si.ISDN, ou.ORG_CODE, ou.ORG_NAME
    HAVING (:status IS NULL OR MAX(CASE
                                      WHEN s.VERIFIED_STATUS = 1 THEN 5
                                      WHEN s.STATUS_900 = 1 THEN 4
                                      WHEN s.BOUGHT_STATUS = 1 THEN 3
                                      WHEN s.BOUGHT_STATUS = 0 THEN 2
                                      WHEN si.STATUS = 1 THEN 1
                                      ELSE 1
                                  END) = :status)
    OFFSET :offset ROWS FETCH NEXT :pageSize ROWS ONLY
""", nativeQuery = true)
    List<Tuple> search(@Param("q") String q,
                       @Param("status") Integer status,
                       @Param("orgCode") String orgCode,
                       @Param("offset") long offset,
                       @Param("pageSize") int pageSize);

    @Query(value = """
        SELECT
           si.ID as ID, si.ISDN as ISDN, ou.ORG_CODE as ORG_CODE, ou.ORG_NAME as ORG_NAME,
           MAX(CASE
                   WHEN s.VERIFIED_STATUS = 1 THEN 5
                   WHEN s.STATUS_900 = 1      THEN 4
                   WHEN s.BOUGHT_STATUS = 1   THEN 3
                   WHEN s.BOUGHT_STATUS = 0   THEN 2
                   WHEN si.STATUS = 1         THEN 1
                   ELSE 1
               END) AS UI_STATUS
    FROM STOCK_ISDN si
    LEFT JOIN SUBSCRIBER s ON s.ISDN = si.ISDN
    LEFT JOIN ORGANIZATION_UNIT ou ON ou.ORG_TYPE = 'NBO' AND ou.ID = s.ORG_ID
    WHERE (:q IS NULL OR ('0'||TO_CHAR(si.ISDN)) LIKE '%'||:q||'%')
      AND (:orgCode IS NULL OR ou.ORG_CODE = :orgCode)
    GROUP BY si.ID, si.ISDN, ou.ORG_CODE, ou.ORG_NAME
    HAVING (:status IS NULL OR MAX(CASE
                                      WHEN s.VERIFIED_STATUS = 1 THEN 5
                                      WHEN s.STATUS_900 = 1      THEN 4
                                      WHEN s.BOUGHT_STATUS = 1   THEN 3
                                      WHEN s.BOUGHT_STATUS = 0   THEN 2
                                      WHEN si.STATUS = 1         THEN 1
                                      ELSE 1
                                  END) = :status)
        """, nativeQuery = true)
    List<Tuple> getSubscriber(
        @Param("q") String q,
        @Param("status") Integer status,
        @Param("orgCode") String orgCode
    );

    @Query(value = """
    SELECT t.ID, pp.PCK_PRICE
    FROM (
        SELECT s.ID, s.PACK_CODE,
               MAX(CASE
                     WHEN s.VERIFIED_STATUS = 1 THEN 5
                     WHEN s.STATUS_900 = 1      THEN 4
                     WHEN s.BOUGHT_STATUS = 1   THEN 3
                     WHEN s.BOUGHT_STATUS = 0   THEN 2
                     ELSE 1
                   END) AS status_rank
        FROM SUBSCRIBER s
        JOIN ORGANIZATION_UNIT ouh\s
             ON ouh.ID = s.ORG_ID AND ouh.ORG_TYPE = 'NBO'
        WHERE (:orgCode IS NULL
            OR  ouh.ID IN (
                 SELECT ou.ID
                 FROM ORGANIZATION_UNIT ou
                 WHERE ou.ORG_TYPE = 'NBO'
                 START WITH ou.ORG_CODE = :orgCode
                 CONNECT BY PRIOR ou.ID = ou.PARENT_ID
            )
        )
        GROUP BY s.ID, s.PACK_CODE
    ) t
    LEFT JOIN PACKAGE_PROFILE pp ON pp.PCK_CODE = t.PACK_CODE
    WHERE (:status IS NULL OR t.status_rank >= :status)
    """, nativeQuery = true)
    List<Tuple> totalEsim(@Param("status") Integer status, @Param("orgCode") String orgCode);
}

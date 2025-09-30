package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.IsdnTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface IsdnTransactionRepository extends BaseJPARepository<IsdnTransactionEntity, String>{

    @Query("SELECT t FROM IsdnTransactionEntity t WHERE (:fromTime IS NULL OR t.createdDate >= :fromTime) AND (:toTime IS NULL OR t.createdDate < :toTime) ORDER BY t.createdDate DESC")
    Page<IsdnTransactionEntity> findByTimeAndTransType(@Param("fromTime") LocalDateTime fromTime, @Param("toTime") LocalDateTime toTime, Pageable pageable);

    @Modifying(flushAutomatically = true)
    @Query(value = """
        UPDATE ISDN_TRANSACTION SET METADATA = JSON_MERGEPATCH(METADATA, '{"checkProgress":' || :progressPercentage || '}') WHERE ID = :transId
        """, nativeQuery = true)
    void updateCheckProgress(@Param("transId") String transId, @Param("progressPercentage") Integer progressPercentage);

    @Modifying(flushAutomatically = true)
    @Query(value = """
        UPDATE ISDN_TRANSACTION SET METADATA = JSON_MERGEPATCH(METADATA, '{"errorStacks":"' || :errorStacks || '"}'),
        TRANS_STATUS = :transStatus, UPLOAD_STATUS = :uploadStatus
        WHERE ID = :transId
        """, nativeQuery = true)
    void updateErrorStacks(@Param("transId") String transId, @Param("transStatus") Integer transStatus,
                           @Param("uploadStatus") Integer uploadStatus, @Param("errorStacks") String errorStacks);
}

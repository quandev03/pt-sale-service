package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.IsdnTransactionLineEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IsdnTransactionLineRepository extends BaseJPARepository<IsdnTransactionLineEntity, String> {
    @Query(value = """
                    SELECT itl.*
                    FROM ISDN_TRANSACTION_LINE itl
                    LEFT JOIN STOCK_ISDN si ON itl.ISDN = si.ISDN
                    WHERE itl.ISDN_TRANS_ID = :isdnTransId
        """, nativeQuery = true)
    List<Tuple> getAllByIsdnTransId(@Param("isdnTransId") String isdnTransId);

    @Query(value = """
                    SELECT itl.*
                    FROM ISDN_TRANSACTION_LINE itl
                    LEFT JOIN STOCK_ISDN si ON itl.ISDN = si.ISDN
                    WHERE itl.ISDN_TRANS_ID = :isdnTransId
                    ORDER BY itl.ID
        """,
        countQuery = """
                    SELECT COUNT(itl.ID)
                    FROM ISDN_TRANSACTION_LINE itl
                    LEFT JOIN STOCK_ISDN si ON itl.ISDN = si.ISDN
                    WHERE itl.ISDN_TRANS_ID = :isdnTransId
        """, nativeQuery = true)
    Page<Tuple> getByIsdnTransId(String isdnTransId, Pageable pageable);
}

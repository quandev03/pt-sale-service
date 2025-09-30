package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.EsimRegistrationLineEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsimRegistrationLineRepository extends JpaRepository<EsimRegistrationLineEntity, String> {

    @Query(value = """
        SELECT so.ID as ID,
               erl.PCK_CODE as PCK_CODE,
               COUNT(*) as QUANTITY
        FROM SALE_ORDER so
        JOIN ESIM_REGISTRATION er ON so.ID = er.ORDER_ID
        JOIN ESIM_REGISTRATION_LINE erl ON er.ID = erl.ESIM_REGISTRATION_ID
        WHERE so.ID = :saleOrderId
        GROUP BY so.ID, erl.PCK_CODE
        ORDER BY erl.PCK_CODE
        """, nativeQuery = true)
    List<Tuple> findBookEsimDetailLineItemsBySaleOrderId(@Param("saleOrderId") String saleOrderId);

    @Query(value = """
        SELECT erl.*
        FROM ESIM_REGISTRATION_LINE erl
        WHERE erl.STATUS IS NULL OR erl.STATUS <> 1
    """, nativeQuery = true)
    List<Tuple> findIncompleteRegistrationLines();

    @Query(value = """
        SELECT erl.*
        FROM ESIM_REGISTRATION_LINE erl
        WHERE erl.ESIM_REGISTRATION_ID = :esimRegistrationId 
        AND erl.SERIAL IS NULL
    """, nativeQuery = true)
    List<Tuple> findByEsimRegistrationIdAndSerialIsNull(@Param("esimRegistrationId") String esimRegistrationId);
}

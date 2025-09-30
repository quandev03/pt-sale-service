package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.EsimRegistrationEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EsimRegistrationRepository extends BaseJPARepository<EsimRegistrationEntity, String> {

    @Query(value = """
    WITH total AS (
        SELECT ber.ESIM_REGISTRATION_ID, ber.PCK_CODE, SUM(ber.QUANTITY) AS QUANTITY
        FROM BOOK_ESIM_REQUEST ber
        WHERE ber.ESIM_REGISTRATION_ID IN (
            SELECT er.ID
            FROM ESIM_REGISTRATION er
            WHERE (er.FAILED_NUMBER IS NULL AND er.SUCCESSED_NUMBER IS NULL) OR (er.FAILED_NUMBER >= 0 AND er.SUCCESSED_NUMBER = 0)
        )
        GROUP BY ber.ESIM_REGISTRATION_ID, ber.PCK_CODE
    ), completed AS (
        SELECT erl.ESIM_REGISTRATION_ID, erl.PCK_CODE, COUNT(*) AS QUANTITY
        FROM ESIM_REGISTRATION_LINE erl
        GROUP BY erl.ESIM_REGISTRATION_ID, erl.PCK_CODE
    )
    SELECT t.ESIM_REGISTRATION_ID, t.PCK_CODE, COALESCE(t.QUANTITY, 0) - COALESCE(c.QUANTITY, 0) AS QUANTITY
    FROM total t
    LEFT JOIN completed c ON c.ESIM_REGISTRATION_ID = t.ESIM_REGISTRATION_ID AND c.PCK_CODE = t.PCK_CODE
    WHERE  COALESCE(t.QUANTITY, 0) - COALESCE(c.QUANTITY, 0) > 0
    """, nativeQuery = true)
    List<Tuple> findIncompleteRegistrations();
}

package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.AgentDebitDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AgentDebitRepoPort {
    AgentDebitDTO save(AgentDebitDTO agentDebitDTO);
    boolean existsByPaymentId(String paymentId, String orgId);
    Page<AgentDebitDTO> search(String q, String type, String orgId, LocalDate startDate, LocalDate endDate, Pageable pageable);
    List<AgentDebitDTO> findAll(String orgId);
}

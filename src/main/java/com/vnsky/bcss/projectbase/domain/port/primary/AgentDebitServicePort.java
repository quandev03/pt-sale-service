package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.AgentDebitDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.TotalAmountResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AgentDebitServicePort {
    Page<AgentDebitDTO> search(String q, String type, LocalDate startDate, LocalDate endDate, Pageable pageable);
    AgentDebitDTO addAgentDebit(String voucherCode, String voucherType);
    ByteArrayResource export(String q, String type, LocalDate startDate, LocalDate endDate, Pageable pageable);
    TotalAmountResponse getTotalAmount();
}

package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.AgentDebitDTO;
import com.vnsky.bcss.projectbase.domain.entity.AgentDebitEntity;
import com.vnsky.bcss.projectbase.domain.mapper.AgentDebitMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.AgentDebitRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.AgentDebitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
public class AgentDebitAdapter extends BaseJPAAdapterVer2<AgentDebitEntity, AgentDebitDTO, String, AgentDebitMapper, AgentDebitRepository> implements AgentDebitRepoPort {
    public AgentDebitAdapter(AgentDebitRepository repository, AgentDebitMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public boolean existsByPaymentId(String paymentId, String orgId) {
        return this.repository.existsByPaymentId(paymentId, orgId);
    }

    @Override
    public Page<AgentDebitDTO> search(String q, String type, String orgId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime start = startDate.atTime(LocalTime.MIN);
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        return this.repository.search(q, type, orgId, start, end, pageable).map(mapper::toDto);
    }

    @Override
    public List<AgentDebitDTO> findAll(String orgId) {
        return this.mapper.toListDto(this.repository.findAll(orgId));
    }
}

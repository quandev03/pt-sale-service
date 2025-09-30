package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.AgentDebitDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.AgentDebitServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.AgentDebitRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.AgentDebitOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
public class AgentDebitRest implements AgentDebitOperation {
    private final AgentDebitServicePort agentDebitServicePort;

    @Override
    public ResponseEntity<Object> addAgentDebit(AgentDebitRequest data) {
        return ResponseEntity.ok(this.agentDebitServicePort.addAgentDebit(data.getVoucherCode(), data.getVoucherType()));
    }

    @Override
    public ResponseEntity<Page<AgentDebitDTO>> getAgentDebit(String q, String type, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return ResponseEntity.ok(this.agentDebitServicePort.search(q, type, startDate, endDate, pageable));
    }

    @Override
    public ResponseEntity<Object> export(String q, String type, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
        String filename = String.format("Danh_sach_giao_dich_nap_tien_BHTT_%s.xlsx", timestamp);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(this.agentDebitServicePort.export(q, type, startDate, endDate, pageable));
    }

    @Override
    public ResponseEntity<Object> getTotalAmount() {
        return ResponseEntity.ok(this.agentDebitServicePort.getTotalAmount());
    }
}

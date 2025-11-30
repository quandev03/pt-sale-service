package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.ContractDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ContractRepoPort {
    ContractDTO save(ContractDTO dto);

    Optional<ContractDTO> findById(String id);

    Page<ContractDTO> search(String ownerName, String tenantName, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}


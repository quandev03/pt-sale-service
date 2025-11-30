package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ContractResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ContractServicePort {
    ContractResponse createContract(CreateContractRequest request);

    Resource genContract(CreateContractRequest request) throws Exception;

    Page<ContractResponse> listContracts(String ownerName, String tenantName, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);
}


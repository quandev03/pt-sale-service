package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.BatchPackageSaleDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.PackageReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BatchPackageSaleRepoPort {
    Page<BatchPackageSaleDTO> searchBatchPackageSales(
        String q,
        Integer saleType,
        Integer status,
        String fromDate,
        String toDate,
        String orgId,
        Pageable pageable
    );

    BatchPackageSaleDTO findById(String id);

    BatchPackageSaleDTO saveAndFlush(BatchPackageSaleDTO batchPackageSaleDTO);

    BatchPackageSaleDTO save(BatchPackageSaleDTO batchPackageSaleDTO);

    Page<PackageReportResponse> searchPackageReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable);

    List<PackageReportResponse> getPackageReport(String currentOrgCode, SearchRevenueReportRequest request);
}

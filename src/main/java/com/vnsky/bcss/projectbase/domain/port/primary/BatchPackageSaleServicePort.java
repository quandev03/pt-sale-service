package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.BatchPackageSaleDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.PackageReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

public interface BatchPackageSaleServicePort {
    Page<BatchPackageSaleDTO> searchBatchPackageSales(
        String q,
        Integer saleType,
        Integer status,
        String fromDate,
        String toDate,
        Pageable pageable
    );

    BatchPackageSaleDTO findById(String id);

    Page<PackageReportResponse> searchPackageReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable);

    ByteArrayOutputStream exportPackageReport(String currentOrgCode, SearchRevenueReportRequest request);

}

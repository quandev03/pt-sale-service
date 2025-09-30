package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.BatchPackageSaleDTO;
import com.vnsky.bcss.projectbase.domain.entity.BatchPackageSaleEntity;
import com.vnsky.bcss.projectbase.domain.mapper.BatchPackageSaleMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.BatchPackageSaleRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.PackageReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.BatchPackageSaleRepository;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class BatchPackageSaleAdapter extends BaseJPAAdapterVer2<BatchPackageSaleEntity, BatchPackageSaleDTO, String, BatchPackageSaleMapper, BatchPackageSaleRepository>
    implements BatchPackageSaleRepoPort {

    private final DbMapper dbMapper;

    @Autowired
    public BatchPackageSaleAdapter(BatchPackageSaleRepository repository, BatchPackageSaleMapper mapper, DbMapper dbMapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public Page<BatchPackageSaleDTO> searchBatchPackageSales(
        String q,
        Integer saleType,
        Integer status,
        String fromDate,
        String toDate,
        String orgId,
        Pageable pageable
    ) {
        log.debug("Searching batch package sales with filters: q={}, saleType={}, status={}, fromDate={}, toDate={}",
            q, saleType, status, fromDate, toDate);

        Page<Tuple> tuples = repository.searchBatchPackageSales(
            q, saleType, status, fromDate, toDate, orgId, pageable);
        List<BatchPackageSaleDTO> mapped = dbMapper.castSqlResult(tuples.getContent(), BatchPackageSaleDTO.class);
        return new PageImpl<>(mapped, pageable, tuples.getTotalElements());
    }

    @Override
    public BatchPackageSaleDTO findById(String id) {
        log.debug("Finding batch package sale by ID: {}", id);

        return repository.findById(id)
            .map(mapper::toDto)
            .orElse(null);
    }

    @Override
    public Page<PackageReportResponse> searchPackageReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable) {
        int orgCodesIsNull = Objects.isNull(request.getOrgCodes()) ? 1 : 0;
        Integer typeValue = null;
        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            try {
                typeValue = Integer.parseInt(request.getType());
            } catch (NumberFormatException e) {
                log.warn("Invalid type format: {}", request.getType());
            }
        }
        List<Tuple> results = this.repository.searchPackageReport(currentOrgCode, request.getQ(), request.getOrgCodes(), orgCodesIsNull, typeValue, request.getStartDate(), request.getEndDate(), pageable.getOffset(), pageable.getPageSize());
        long total = CollectionUtils.isEmpty(results) ? 0 : dbMapper.getLongSafe(results.get(0), "total");
        return new PageImpl<>(dbMapper.castSqlResult(results, PackageReportResponse.class), pageable, total);
    }

    @Override
    public List<PackageReportResponse> getPackageReport(String currentOrgCode, SearchRevenueReportRequest request) {
        int orgCodesIsNull = Objects.isNull(request.getOrgCodes()) ? 1 : 0;
        Integer typeValue = null;
        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            try {
                typeValue = Integer.parseInt(request.getType());
            } catch (NumberFormatException e) {
                log.warn("Invalid type format: {}", request.getType());
            }
        }
        List<Tuple> tuples = this.repository.getPackageReport(currentOrgCode, request.getQ(), request.getOrgCodes(), orgCodesIsNull, typeValue, request.getStartDate(), request.getEndDate());
        return dbMapper.castSqlResult(tuples, PackageReportResponse.class);
    }
}

package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryByOrgReportDTO;
import com.vnsky.bcss.projectbase.domain.dto.SummaryAllReportDTO;
import com.vnsky.bcss.projectbase.domain.entity.SaleOrderEntity;
import com.vnsky.bcss.projectbase.domain.mapper.SaleOrderMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.SaleOrderRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrderRevenueReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.ListBookEsimResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticOrgResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.SaleOrderRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import com.vnsky.bcss.projectbase.shared.utils.StringUtilsOCR;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Component
@Slf4j
public class SaleOrderAdapter extends BaseJPAAdapterVer2<SaleOrderEntity, SaleOrderDTO, String, SaleOrderMapper, SaleOrderRepository> implements SaleOrderRepoPort {
    private final DbMapper dbMapper;

    public SaleOrderAdapter(SaleOrderMapper mapper, SaleOrderRepository repository, DbMapper dbMapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public Page<SaleOrderDTO> getListBookEsimFreeByOrgId(Pageable pageable, String orgId,  String toDate, String fromDate, String textSearch, int isFree) {
        return repository.findBookFreeByOrgId(pageable, orgId, toDate, fromDate, textSearch, isFree).map(entity -> mapper.toDto(entity));
    }

    @Override
    public Page<ListBookEsimResponse> getListBookEsimByOrgId(Pageable pageable, String orgId, String toDate, String fromDate, String textSearch) {
        Page<Tuple> results = repository.findBookByOrgId(pageable, orgId, toDate, fromDate, StringUtilsOCR.buildLikeOperator(textSearch));
        List<ListBookEsimResponse> mappedResults = dbMapper.castSqlResult(results.getContent(), ListBookEsimResponse.class);
        return new PageImpl<>(mappedResults, pageable, results.getTotalElements());
    }

    @Override
    public List<ListBookEsimResponse> getListBookEsimByOrgIdExport(String orgId, String toDate, String fromDate, String textSearch) {
        List<Tuple> results = repository.findBookByOrgIdExport(orgId, toDate, fromDate, StringUtilsOCR.buildLikeOperator(textSearch));
        return results.stream().map(result -> dbMapper.castSqlResult(result, ListBookEsimResponse.class)).toList();
    }

    @Override
    public Optional<SaleOrderDTO> findById(String id) {
        return repository.findById(id).map(entity -> mapper.toDto(entity));
    }

    @Override
    public Page<OrderRevenueReportResponse> searchOrderRevenueReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable) {
        int orgCodesIsNull = Objects.isNull(request.getOrgCodes()) ? 1 : 0;
        List<Tuple> results = this.repository.searchOrderRevenueReport(currentOrgCode, request.getQ(), request.getOrgCodes(), request.getStartDate(), request.getEndDate(), pageable.getOffset(), pageable.getPageSize(), orgCodesIsNull);
        long total = CollectionUtils.isEmpty(results) ? 0 : dbMapper.getLongSafe(results.get(0), "total");
        return new PageImpl<>(dbMapper.castSqlResult(results, OrderRevenueReportResponse.class), pageable, total);
    }

    @Override
    public List<OrderRevenueReportResponse> getOrderRevenueReport(String currentOrgCode, SearchRevenueReportRequest request) {
        int orgCodesIsNull = Objects.isNull(request.getOrgCodes()) ? 1 : 0;
        List<Tuple> tuples = this.repository.getOrderRevenueReport(currentOrgCode, request.getQ(), request.getOrgCodes(), request.getStartDate(), request.getEndDate(), orgCodesIsNull);
        return dbMapper.castSqlResult(tuples, OrderRevenueReportResponse.class);
    }

    @Override
    public List<StatisticResponse> statisticEsimSold(String orgCode, String startDate, String endDate, int granularity) {
        List<Tuple> results = repository.statisticEsimSold(orgCode, startDate, endDate, granularity);
        return results.stream().map(result -> dbMapper.castSqlResult(result, StatisticResponse.class)).toList();
    }

    @Override
    public List<StatisticOrgResponse> statisticEsimSoldOrg(String orgCode, String startDate, String endDate) {
        List<Tuple> results = repository.statisticEsimSoldOrg(orgCode, startDate, endDate);
        return results.stream().map(result -> dbMapper.castSqlResult(result, StatisticOrgResponse.class)).toList();
    }

    @Override
    public BigDecimal revenue(String orgCode) {
        return repository.revenue(orgCode);
    }

    @Override
    public Page<SummaryByOrgReportDTO> getSummaryByOrgReport(String startDate, String endDate, Pageable pageable) {
        return this.dbMapper.castSqlResult(repository.getSummaryByOrgReportData(startDate, endDate, pageable), SummaryByOrgReportDTO.class);
    }

    @Override
    public List<SummaryByOrgReportDTO> getSummaryByOrgReportAll(String startDate, String endDate) {
        List<Tuple> results = repository.getSummaryByOrgReportDataAll(startDate, endDate);
        return results.stream().map(result -> dbMapper.castSqlResult(result, SummaryByOrgReportDTO.class)).toList();
    }

    @Override
    public SummaryAllReportDTO getSummaryAllReport(String startDate, String endDate) {
        Tuple result = repository.getSummaryAllReportData(startDate, endDate);
        return result != null ? dbMapper.castSqlResult(result, SummaryAllReportDTO.class) : null;
    }
}

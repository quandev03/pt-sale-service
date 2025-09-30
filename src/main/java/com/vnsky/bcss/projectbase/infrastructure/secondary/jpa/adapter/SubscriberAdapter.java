package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.ExportQrEsimExcel;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.entity.SubscriberEntity;
import com.vnsky.bcss.projectbase.domain.mapper.SubscriberMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.SearchRevenueReportRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchSubscriberResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SubscriberReportResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.SubscriberRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
public class SubscriberAdapter extends BaseJPAAdapterVer2<SubscriberEntity, SubscriberDTO, String, SubscriberMapper, SubscriberRepository>
implements SubscriberRepoPort {
    private final DbMapper dbMapper;

    public SubscriberAdapter(DbMapper dbMapper,
                             SubscriberMapper mapper,
                             SubscriberRepository repository) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public Optional<SubscriberDTO> findByLastIsdn(Long isdn) {
        return repository.findByLastIsdn(isdn).map(mapper::toDto);
    }

    @Override
    public Optional<SubscriberDTO> findByLastSerial(String serial) {
        return repository.findByLastSerial(serial).map(mapper::toDto);
    }

    @Override
    public Page<SearchSubscriberResponse> searchSubscriber(String q, Integer status, String orgCode, Pageable page) {
        List<Tuple> results = this.repository.searchSubscriber(q, status, orgCode, page.getOffset(), page.getPageSize());
        long total = CollectionUtils.isEmpty(results) ? 0 : dbMapper.getLongSafe(results.get(0), "total");
        return new PageImpl<>(dbMapper.castSqlResult(results, SearchSubscriberResponse.class), page, total);
    }

    @Override
    public List<SearchSubscriberResponse> getSubscriber(String q, Integer status, String orgCode) {
        List<Tuple> tuples = this.repository.getSubscriber(q, status, orgCode);
        return dbMapper.castSqlResult(tuples, SearchSubscriberResponse.class);
    }

    @Override
    public Page<EsimInforDTO> getListEsimInfor(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, List<String> orgIdSearch, String fromDate, String toDate, Pageable pageable) {
        int isFilter = ( Objects.isNull(orgIdSearch) ||  orgIdSearch.isEmpty() )? 0: 1;
        Page<Tuple> tuples = repository.getListEsimInfor(textSearch, subStatus, activeStatus, orgId, pckCode, orgIdSearch, isFilter, fromDate, toDate,pageable);
        return new PageImpl<>(tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, EsimInforDTO.class)).toList(), pageable, tuples.getTotalElements());
    }

    @Override
    public Page<EsimInforDTO> getListEsimInforInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate, Pageable pageable) {
        int isFilter =  ( Objects.isNull(orgId) ||  orgId.isEmpty() )? 0: 1;
        Page<Tuple> tuples = repository.getListEsimInforInternal(textSearch, subStatus, activeStatus, orgId, pckCode, isFilter, fromDate, toDate,pageable);
        return new PageImpl<>(tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, EsimInforDTO.class)).toList(), pageable, tuples.getTotalElements());
    }

    @Override
    public Optional<SubscriberDTO> findById(String subId) {
        return repository.findById(subId).map(mapper::toDto);
    }

    @Override
    public boolean isExistByContractCodeOrCustomerCode(String contractCode, String customerCode) {
        return repository.isExistByContractCodeOrCustomerCode(contractCode, customerCode);
    }

    @Override
    public Page<SubscriberReportResponse> searchSubscriberReport(String currentOrgCode, SearchRevenueReportRequest request, Pageable pageable) {
        int orgCodesIsNull = Objects.isNull(request.getOrgCodes()) ? 1 : 0;
        List<Tuple> results = this.repository.searchSubscriberReport(currentOrgCode, request.getQ(), request.getOrgCodes(), orgCodesIsNull, request.getStartDate(), request.getEndDate(), pageable.getOffset(), pageable.getPageSize());
        long total = CollectionUtils.isEmpty(results) ? 0 : dbMapper.getLongSafe(results.get(0), "total");
        return new PageImpl<>(dbMapper.castSqlResult(results, SubscriberReportResponse.class), pageable, total);
    }

    @Override
    public List<SubscriberReportResponse> getSubscriberReport(String currentOrgCode, SearchRevenueReportRequest request) {
        int orgCodesIsNull = Objects.isNull(request.getOrgCodes()) ? 1 : 0;
        List<Tuple> tuples = this.repository.getSubscriberReport(currentOrgCode, request.getQ(), request.getOrgCodes(), orgCodesIsNull, request.getStartDate(), request.getEndDate());
        return dbMapper.castSqlResult(tuples, SubscriberReportResponse.class);
    }

    @Override
    public ESimDetailResponse findEsimDetailById(String subId) {
        return dbMapper.castSqlResult(repository.getEsimDetailById(subId), ESimDetailResponse.class);
    }

    @Override
    public int isEsimBelongToAgent(Long isdn, String agentId) {
        return repository.isEsimBelongToAgent(isdn, agentId);
    }

    public Optional<SubscriberDTO> findByImsi(Long imsi) {
        return repository.findByImsi(imsi).map(mapper::toDto);
    }

    @Override
    public List<EsimInforDTO> getListEsimInforExport(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, List<String> orgIdSearch, String fromDate, String toDate) {
        int isFilter =  ( Objects.isNull(orgIdSearch) ||  orgIdSearch.isEmpty() )? 0: 1;
        List<Tuple> tuples = repository.getListEsimInforExport(textSearch, subStatus, activeStatus, orgId, pckCode, orgIdSearch, isFilter, fromDate, toDate);
        return tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, EsimInforDTO.class)).toList();
    }

    @Override
    public List<EsimInforDTO> getListEsimInforExportInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, List<String> orgId, String fromDate, String toDate) {
        int isFilter =  ( Objects.isNull(orgId) ||  orgId.isEmpty() )? 0: 1;
        List<Tuple> tuples = repository.getListEsimInforExportInternal(textSearch, subStatus, activeStatus, orgId, pckCode, isFilter, fromDate, toDate);
        return tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, EsimInforDTO.class)).toList();
    }

    @Override
    public List<ExportQrEsimExcel> getListEsimQrCode(List<String> subIds) {
        List<Tuple> tuples = repository.getListEsimExportQr(subIds);
        return tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, ExportQrEsimExcel.class)).toList();
    }

    @Override
    public List<SubscriberDTO> findByIds(List<String> subIds) {
        List<SubscriberEntity> entities = repository.findByIdIn(subIds);
        return mapper.toListDto(entities);
    }
}

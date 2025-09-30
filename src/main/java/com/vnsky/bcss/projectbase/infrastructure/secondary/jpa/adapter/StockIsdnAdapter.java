package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.StockIsdnDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberPackageDTO;
import com.vnsky.bcss.projectbase.domain.entity.StockIsdnEntity;
import com.vnsky.bcss.projectbase.domain.mapper.StockIsdnMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.StockIsdnRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchStockIsdnResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.StockIsdnRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.StockIsdnStatus;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import com.vnsky.common.utils.DataUtils;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StockIsdnAdapter extends BaseJPAAdapterVer2<StockIsdnEntity, StockIsdnDTO, String, StockIsdnMapper, StockIsdnRepository>
        implements StockIsdnRepoPort {

    private final DbMapper dbMapper;

    public StockIsdnAdapter(StockIsdnMapper mapper, StockIsdnRepository repository, DbMapper dbMapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public List<StockIsdnDTO> findAvailableIsdns(int limit) {
        return repository.findAvailableIsdns().stream()
                .limit(limit)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Optional<StockIsdnDTO> findByIsdn(Long isdn) {
        return repository.findByIsdn(isdn).map(mapper::toDto);
    }

    @Override
    public Optional<StockIsdnDTO> findBySerial(Long serial) {
        return repository.findBySerial(serial).map(mapper::toDto);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateTransferStatusIsdnIn(Integer transferStatus, Integer transferType, List<Long> isdnList) {
        DataUtils.batchProcess(isdnList,
            isdnBatchList -> this.repository.updateTransferStatusIsdnIn(transferStatus, transferType, isdnBatchList));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createStockIsdnIn(List<Long> isdnList) {
        List<StockIsdnEntity> entities = isdnList.stream()
            .map(isdn -> StockIsdnEntity.builder()
                .isdn(isdn)
                .status(StockIsdnStatus.UNUSED.getValue())
                .build()
            )
            .collect(Collectors.toList());
        repository.saveAll(entities);
    }

    @Override
    public List<StockIsdnDTO> findByIsdnIn(List<Long> isdns) {
        return mapper.toListDto(repository.findByIsdnIn(isdns));
    }

    @Override
    public List<StockIsdnDTO> findByIsdnIn(Set<Long> isdns) {
        return mapper.toListDto(repository.findByIsdnIn(isdns));
    }

    @Override
    public Page<SearchStockIsdnResponse> search(String q, Integer status, String orgCode, Pageable page) {
        List<Tuple> results = this.repository.search(q, status, orgCode, page.getOffset(), page.getPageSize());
        long total = CollectionUtils.isEmpty(results) ? 0 : dbMapper.getLongSafe(results.get(0), "total");
        return new PageImpl<>(dbMapper.castSqlResult(results, SearchStockIsdnResponse.class), page, total);
    }

    @Override
    public List<SearchStockIsdnResponse> getSubscriber(String q, Integer status, String orgCode) {
        List<Tuple> tuples = this.repository.getSubscriber(q, status, orgCode);
        return dbMapper.castSqlResult(tuples, SearchStockIsdnResponse.class);
    }

    @Override
    public Long totalEsim(Integer status, String orgCode) {
        List<Tuple> results = this.repository.totalEsim(status, orgCode);
        List<SubscriberPackageDTO> dto = dbMapper.castSqlResult(results, SubscriberPackageDTO.class);
        return (long) dto.size();
    }

    @Override
    public Long revenusEsimCalled900(Integer status, String orgCode) {
        List<Tuple> results = this.repository.totalEsim(status, orgCode);
        List<SubscriberPackageDTO> dto = dbMapper.castSqlResult(results, SubscriberPackageDTO.class);
        return dto.stream().mapToLong(SubscriberPackageDTO::getPackagePrice).sum();
    }
}

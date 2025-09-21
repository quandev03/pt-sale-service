package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.entity.SubscriberEntity;
import com.vnsky.bcss.projectbase.domain.mapper.SubscriberMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ESimDetailResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.SubscriberRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.util.List;
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
    public Optional<SubscriberDTO> findBySerialAndStatus(String serial, int status) {
        return repository.findBySerialAndStatus(serial, status).map(mapper::toDto);
    }

    @Override
    public Optional<SubscriberDTO> findByLastIsdn(Long isdn) {
        return repository.findByLastIsdn(isdn).map(mapper::toDto);
    }

    @Override
    public Optional<SubscriberDTO> findBySerialLastSerial(String serial) {
        return repository.findByLastSerial(serial).map(mapper::toDto);
    }

    @Override
    public List<SubscriberDTO> findSubscriberToBookEsim(int limit) {
        return repository.findSubscriberToBookEsim().stream()
                .limit(limit)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Page<EsimInforDTO> getListEsimInfor(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, String orgIdSearch, Pageable pageable) {
        Page<Tuple> tuples = repository.getListEsimInfor(textSearch, subStatus, activeStatus, orgId, pckCode, orgIdSearch,pageable);
        return new PageImpl<>(tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, EsimInforDTO.class)).toList(), pageable, tuples.getTotalElements());
    }

    @Override
    public Page<EsimInforDTO> getListEsimInforInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, Pageable pageable) {
        Page<Tuple> tuples = repository.getListEsimInforInternal(textSearch, subStatus, activeStatus, orgId, pckCode,pageable);
        return new PageImpl<>(tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, EsimInforDTO.class)).toList(), pageable, tuples.getTotalElements());
    }

    @Override
    public Optional<SubscriberDTO> findById(String subId) {
        return repository.findById(subId).map(mapper::toDto);
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
    public List<EsimInforDTO> getListEsimInforExport(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId, String orgIdSearch) {
        List<Tuple> tuples = repository.getListEsimInforExport(textSearch, subStatus, activeStatus, orgId, pckCode, orgIdSearch);
        return tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, EsimInforDTO.class)).toList();
    }

    @Override
    public List<EsimInforDTO> getListEsimInforExportInternal(String textSearch, Integer subStatus, Integer activeStatus, String pckCode, String orgId) {
        List<Tuple> tuples = repository.getListEsimInforExportInternal(textSearch, subStatus, activeStatus, orgId, pckCode);
        return tuples.stream().map(tuple -> dbMapper.castSqlResult(tuple, EsimInforDTO.class)).toList();
    }
}

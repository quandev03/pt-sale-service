package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionLineDTO;
import com.vnsky.bcss.projectbase.domain.entity.IsdnTransactionLineEntity;
import com.vnsky.bcss.projectbase.domain.mapper.IsdnTransactionLineMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.IsdnTransactionLineRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.IsdnTransactionLineRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class IsdnTransactionLineAdapter extends BaseJPAAdapterVer2<IsdnTransactionLineEntity, IsdnTransactionLineDTO, String, IsdnTransactionLineMapper, IsdnTransactionLineRepository> implements IsdnTransactionLineRepoPort {
    private final DbMapper dbMapper;

    public IsdnTransactionLineAdapter(IsdnTransactionLineRepository repository, IsdnTransactionLineMapper mapper, DbMapper dbMapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public List<IsdnTransactionLineDTO> findAllByIsdnTransId(String recordId) {
        return this.dbMapper.castSqlResult(this.repository.getAllByIsdnTransId(recordId), IsdnTransactionLineDTO.class);
    }

    @Override
    public Page<IsdnTransactionLineDTO> findByIsdnTransId(String recordId, Pageable pageable) {
        return this.dbMapper.castSqlResult(this.repository.getByIsdnTransId(recordId, pageable), IsdnTransactionLineDTO.class);
    }
}

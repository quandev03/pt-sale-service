package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionDTO;
import com.vnsky.bcss.projectbase.domain.entity.IsdnTransactionEntity;
import com.vnsky.bcss.projectbase.domain.mapper.IsdnTransactionMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.IsdnTransactionRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.IsdnTransactionRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.NumberTransactionStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.NumberUploadStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
public class IsdnTransactionAdapter extends BaseJPAAdapterVer2<IsdnTransactionEntity, IsdnTransactionDTO, String, IsdnTransactionMapper, IsdnTransactionRepository> implements IsdnTransactionRepoPort {

    public IsdnTransactionAdapter(IsdnTransactionRepository repository, IsdnTransactionMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public Page<IsdnTransactionDTO> findUploadByTime(LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable) {
        return repository.findByTimeAndTransType(fromTime, toTime, pageable)
            .map(mapper::toDto);
    }

    @Override
    public IsdnTransactionDTO getById(String recordId) {
        return this.repository.findById(recordId)
            .map(this.mapper::toDto)
            .orElse(null);
    }

    @Override
    @Transactional
    public void updateCheckProgress(String transId, Integer checkPercentage) {
        this.repository.updateCheckProgress(transId, checkPercentage);
    }

    @Override
    public void markCrashedTransaction(String transId, Exception ex) {
        this.repository.updateErrorStacks(transId, NumberTransactionStatus.PRE_START.getValue(), NumberUploadStatus.FAILURE.getValue(), ex.getMessage());
    }
}

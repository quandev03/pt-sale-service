package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionDTO;
import com.vnsky.common.utils.TemporaryFileResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IsdnTransactionRepoPort {

    Page<IsdnTransactionDTO> findUploadByTime(LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable);

    IsdnTransactionDTO getById(String recordId);

    IsdnTransactionDTO saveAndFlush(IsdnTransactionDTO transaction);

    void updateCheckProgress(String transId, Integer checkPercentage);

    void markCrashedTransaction(String transId, Exception ex);
}

package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionLineDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IsdnTransactionLineRepoPort {

    List<IsdnTransactionLineDTO> saveAllAndFlush(List<IsdnTransactionLineDTO> entities);

    List<IsdnTransactionLineDTO> findAllByIsdnTransId(String recordId);

    Page<IsdnTransactionLineDTO> findByIsdnTransId(String recordId, Pageable pageable);
}

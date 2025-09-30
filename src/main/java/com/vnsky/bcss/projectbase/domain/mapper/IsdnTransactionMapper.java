package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionDTO;
import com.vnsky.bcss.projectbase.domain.entity.IsdnTransactionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IsdnTransactionMapper extends BaseMapper<IsdnTransactionEntity, IsdnTransactionDTO> {
}

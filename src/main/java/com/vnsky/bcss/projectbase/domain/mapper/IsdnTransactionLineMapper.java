package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionLineDTO;
import com.vnsky.bcss.projectbase.domain.entity.IsdnTransactionLineEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IsdnTransactionLineMapper extends BaseMapper<IsdnTransactionLineEntity, IsdnTransactionLineDTO>  {
}

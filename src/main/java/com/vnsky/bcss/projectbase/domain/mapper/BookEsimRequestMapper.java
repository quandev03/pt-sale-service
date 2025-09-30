package com.vnsky.bcss.projectbase.domain.mapper;

import com.vnsky.bcss.projectbase.domain.dto.BookEsimRequestDTO;
import com.vnsky.bcss.projectbase.domain.entity.BookEsimRequestEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookEsimRequestMapper extends BaseMapper<BookEsimRequestEntity, BookEsimRequestDTO> {
}

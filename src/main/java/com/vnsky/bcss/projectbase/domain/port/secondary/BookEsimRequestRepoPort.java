package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.BookEsimRequestDTO;

import java.util.List;

public interface BookEsimRequestRepoPort {
    BookEsimRequestDTO save(BookEsimRequestDTO dto);
    List<BookEsimRequestDTO> saveAllAndFlush(List<BookEsimRequestDTO> dto);
}

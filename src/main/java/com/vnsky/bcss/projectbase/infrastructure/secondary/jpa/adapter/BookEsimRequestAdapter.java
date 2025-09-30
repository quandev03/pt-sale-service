package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.BookEsimRequestDTO;
import com.vnsky.bcss.projectbase.domain.entity.BookEsimRequestEntity;
import com.vnsky.bcss.projectbase.domain.mapper.BookEsimRequestMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.BookEsimRequestRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.BookEsimRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookEsimRequestAdapter extends BaseJPAAdapterVer2<BookEsimRequestEntity, BookEsimRequestDTO, String, BookEsimRequestMapper, BookEsimRequestRepository> implements BookEsimRequestRepoPort {
    protected BookEsimRequestAdapter(BookEsimRequestRepository repository, BookEsimRequestMapper mapper) {
        super(repository, mapper);
    }
}

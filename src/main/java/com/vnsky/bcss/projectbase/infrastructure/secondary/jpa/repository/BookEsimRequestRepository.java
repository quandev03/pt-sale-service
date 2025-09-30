package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository;

import com.vnsky.bcss.projectbase.domain.entity.BookEsimRequestEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface BookEsimRequestRepository extends BaseJPARepository<BookEsimRequestEntity, String> {
}

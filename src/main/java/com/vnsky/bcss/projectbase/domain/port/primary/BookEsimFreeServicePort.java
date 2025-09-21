package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookEsimFreeServicePort {
    BookEsimResponse bookEsim(BookEsimRequest request);
    Page<SaleOrderDTO> getListBookEsimFree(Pageable pageable);
}

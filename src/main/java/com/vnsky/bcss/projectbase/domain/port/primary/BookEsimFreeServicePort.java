package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookEsimFreeServicePort {
    List<BookEsimResponse> bookEsim(List<BookEsimRequest> requests);
    Page<SaleOrderDTO> getListBookEsimFree(Pageable pageable, String toDate, String fromDate, String textSearch, int isFree);
}

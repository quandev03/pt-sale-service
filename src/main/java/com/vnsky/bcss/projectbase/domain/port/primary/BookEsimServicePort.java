package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.IncompleteRegistrationDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.ListBookEsimResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimDetailResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookEsimServicePort {
    List<BookEsimResponse> bookEsim(List<BookEsimRequest> requests, String note);
    Page<ListBookEsimResponse> searchBookEsimList(Pageable pageable, String toDate, String fromDate, String textSearch);
    Resource export(String toDate, String fromDate, String textSearch);
    BookEsimDetailResponse findById(String id);
    List<IncompleteRegistrationDTO> findIncompleteRegistrations();
}

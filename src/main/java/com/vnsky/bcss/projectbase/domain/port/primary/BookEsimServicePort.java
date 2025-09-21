package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimResponse;

public interface BookEsimServicePort {
    BookEsimResponse bookEsim(BookEsimRequest request);
}

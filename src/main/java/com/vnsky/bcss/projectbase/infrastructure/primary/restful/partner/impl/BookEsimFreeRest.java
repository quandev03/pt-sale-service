package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimFreeServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.BookEsimFreeOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookEsimFreeRest implements BookEsimFreeOperation {

    private final BookEsimFreeServicePort bookEsimServicePort;
    private final PackageManagerServicePort packageManagerServicePort;

    @Override
    public ResponseEntity<Object> bookEsim(@Validated @RequestBody List<BookEsimRequest> requests) {
            return ResponseEntity.ok(bookEsimServicePort.bookEsim(requests));
    }

    @Override
    public ResponseEntity<Page<SaleOrderDTO>> getBookEsimFree(Pageable pageable, String dateFrom, String dateTo, Integer status, String textSearch, Integer isFree) {
        return ResponseEntity.ok(bookEsimServicePort.getListBookEsimFree(pageable, dateTo, dateFrom, textSearch, isFree));
    }

    @Override
    public ResponseEntity<Object> freePackageProfile() {
        return ResponseEntity.ok(packageManagerServicePort.getListPackageProfile());
    }
}

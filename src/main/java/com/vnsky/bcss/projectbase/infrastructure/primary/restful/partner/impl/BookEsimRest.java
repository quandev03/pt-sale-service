package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.EsimBookingRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.BookEsimOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookEsimRest implements BookEsimOperation {

    private final BookEsimServicePort bookEsimServicePort;
    private final PackageManagerServicePort packageManagerServicePort;

    @Override
    public ResponseEntity<Object> bookEsim(@Validated @RequestBody EsimBookingRequest bookingRequest) {
        return ResponseEntity.ok(bookEsimServicePort.bookEsim(bookingRequest.getRequests(), bookingRequest.getNote()));
    }

    @Override
    public ResponseEntity<Object> searchBookEsimList(Pageable pageable, String dateFrom, String dateTo, Integer status, String textSearch) {
        return ResponseEntity.ok(bookEsimServicePort.searchBookEsimList(pageable, dateTo, dateFrom, textSearch));
    }

    @Override
    public ResponseEntity<Object> findById(String id) {
        return ResponseEntity.ok(bookEsimServicePort.findById(id));
    }

    @Override
    public ResponseEntity<Object> freePackageProfile() {
        return ResponseEntity.ok(packageManagerServicePort.getListPackageProfile());
    }

    @Override
    public ResponseEntity<Object> export( String dateFrom, String dateTo, Integer status, String textSearch) {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("danh_sach_dat_hang_esim.xlsx").build().toString())
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
            .body(bookEsimServicePort.export(dateTo, dateFrom, textSearch));
    }
}

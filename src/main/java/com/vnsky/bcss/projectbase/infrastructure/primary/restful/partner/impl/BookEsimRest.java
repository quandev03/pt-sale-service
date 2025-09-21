package com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.partner.BookEsimOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookEsimRest implements BookEsimOperation {

    private final BookEsimServicePort bookEsimServicePort;

    @Override
    public ResponseEntity<Object> bookEsim(@Validated @RequestBody BookEsimRequest request) {
            return ResponseEntity.ok(bookEsimServicePort.bookEsim(request));
    }
}

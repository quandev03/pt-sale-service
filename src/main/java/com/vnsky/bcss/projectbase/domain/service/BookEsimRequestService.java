package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimRequestServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.BookEsimRequestRepoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookEsimRequestService implements BookEsimRequestServicePort {
    private final BookEsimRequestRepoPort bookEsimRequestRepoPort;
}

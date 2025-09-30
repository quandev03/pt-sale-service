package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class IncompleteRegistrationDTO {
    private String registrationId;
    private List<BookEsimRequest> request;
}

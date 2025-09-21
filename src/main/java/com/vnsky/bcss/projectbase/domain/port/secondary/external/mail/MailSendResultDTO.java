package com.vnsky.bcss.projectbase.domain.port.secondary.external.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MailSendResultDTO {
    private int success;
}

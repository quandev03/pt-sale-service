package com.vnsky.bcss.projectbase.domain.port.secondary.external;


import com.vnsky.bcss.projectbase.domain.port.secondary.external.mail.MailInfoDTO;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.mail.MailSendResultDTO;

import java.util.List;
import java.util.concurrent.Future;

public interface MailServicePort {

    Future<MailSendResultDTO> sendMail(MailInfoDTO mailInfo, String template);

    Future<MailSendResultDTO> sendMultipleMails(List<MailInfoDTO> lsMailInfo, String template);

}

package com.vnsky.bcss.projectbase.infrastructure.secondary.external.adapter;

import com.vnsky.bcss.projectbase.domain.port.secondary.external.MailServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.mail.MailInfoDTO;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.mail.MailSendResultDTO;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Log4j2
@Service
@RequiredArgsConstructor
public class MailServiceAdapter implements MailServicePort {

    private final ISpringTemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final MinioOperations minioOperations;
    @Value("${spring.mail.from}")
    private String mailFrom;

    @Value("${spring.mail.properties.mail-sender-display-name}")
    private String mailFromDisplay;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.protocol}")
    private String mailProtocol;

    @Async
    @Override
    @SneakyThrows
    public Future<MailSendResultDTO> sendMail(MailInfoDTO mailInfo, String template) {
        try (Transport transport = this.getTransport((JavaMailSenderImpl) javaMailSender)) {
            MimeMessage message;
            int success = 0;
            message = this.buildMimeMessage(mailInfo, template);
            try {
                transport.sendMessage(message, message.getAllRecipients());
                log.debug("[MAIL] send to {} SUCCESS", mailInfo.getTo());
                success++;
            } catch (Exception ex) {
                log.error("Exception when sending mail", ex);
                log.error("[MAIL] send to {} FAIL", mailInfo.getTo());
            }
            log.debug("[MAIL] result: success={}, fail={}", success, 1 - success);
            return CompletableFuture.completedFuture(MailSendResultDTO.builder().success(success).build());
        }
    }

    @Async
    @Override
    @SneakyThrows
    public Future<MailSendResultDTO> sendMultipleMails(List<MailInfoDTO> lsMailInfo, String template) {
        try (Transport transport = this.getTransport((JavaMailSenderImpl) javaMailSender)) {
            MimeMessage message;
            int success = 0;
            for (MailInfoDTO e : lsMailInfo) {
                message = this.buildMimeMessage(e, template);
                try {
                    transport.sendMessage(message, message.getAllRecipients());
                    log.debug("[MAIL] send to {} SUCCESS", e.getTo());
                    success++;
                } catch (Exception ex) {
                    log.error("[MAIL] send to {} FAIL", e.getTo());
                }
            }
            log.debug("[MAIL] result: success={}, fail={}", success, lsMailInfo.size() - success);
            return CompletableFuture.completedFuture(MailSendResultDTO.builder().success(success).build());
        }
    }

    @SneakyThrows
    private MimeMessage buildMimeMessage(MailInfoDTO mailInfo, String template) {
        MimeMessage message = javaMailSender.createMimeMessage();
        Context context = this.buildMailContext(mailInfo);
        String content = this.templateEngine.process(template, context);
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setFrom(mailFrom, mailFromDisplay);
        helper.setTo(mailInfo.getTo());
        helper.setText(content, true);
        helper.setSubject(mailInfo.getSubject());
        addImageCid(mailInfo.getImageCids(), helper);
        return message;
    }

    @SneakyThrows
    private void addImageCid(List<MailInfoDTO.FileCid> lsImageCid, MimeMessageHelper helper) {
        if (!ObjectUtils.isEmpty(lsImageCid)) {
            ByteArrayResource resource;
            for (MailInfoDTO.FileCid e : lsImageCid) {
                DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
                    .uri(e.getPath())
                    .isPublic(false)
                    .build();
                try (InputStream is = minioOperations.download(downloadOptionDTO).getInputStream()) {
                    resource = new ByteArrayResource(is.readAllBytes());
                    helper.addInline(e.getContentId(), resource, e.getContentType());
                }
            }
        }
    }

    @SneakyThrows
    private Context buildMailContext(MailInfoDTO mailInfo) {
        Context context = new Context();
        Class<MailInfoDTO> clazz = MailInfoDTO.class;
        Field[] fields = clazz.getDeclaredFields();
        Method method;
        for (Field field : fields) {
            method = clazz.getMethod("get" + StringUtils.capitalize(field.getName()));
            context.setVariable(field.getName(), method.invoke(mailInfo));
        }
        return context;
    }

    private Transport getTransport(JavaMailSenderImpl mailSender) {
        Session session = mailSender.getSession();
        Transport smtpTransport = null;
        try {
            smtpTransport = session.getTransport(mailProtocol);
            smtpTransport.connect(mailHost, mailPort, mailUsername, mailPassword);
        } catch (IllegalStateException e) {
            log.debug("Already connected");
        } catch (Exception e) {
            log.error("Get mail transport error");
        }
        return smtpTransport;
    }
}

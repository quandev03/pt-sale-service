package com.vnsky.bcss.projectbase.shared.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@SuppressWarnings("all")
public class MessageSourceUtils {

    private static MessageSource messages;

    @Autowired
    public MessageSourceUtils(@Qualifier("applicationErrorMessageSource") MessageSource messages) {
        Locale.setDefault(Locale.ENGLISH);
        MessageSourceUtils.messages = messages;
    }

    public static String getMessage(String key) {
        return messages.getMessage(key, null, locale());
    }

    private static Locale locale() {
        return LocaleContextHolder.getLocale();
    }

    public static String getMessageDetail(String key) {
        return getMessage(builDetail(key));
    }

    public static String builDetail(String key) {
        return "error." + key + ".detail";
    }

}

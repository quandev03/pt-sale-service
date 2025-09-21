package com.vnsky.bcss.projectbase.wire.security;

import com.vnsky.security.SecurityUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

import java.util.Optional;

public class CustomAuditorAware implements AuditorAware<String> {

    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityUtil.getCurrentPreferredUsername());
    }

}

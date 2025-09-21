package com.vnsky.bcss.projectbase.wire.security;

import com.vnsky.security.SecurityIgnoreCustomizer;
import com.vnsky.security.SecurityRuleCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SecurityConfigCustomerService {

    @Value("${application.path.base.public}")
    private String requestMatcherPublic;


    @Bean
    public SecurityIgnoreCustomizer securityIgnoreCustomizer() {
        log.info("SecurityIgnoreCustomizer");
        return ignoreConfigurer ->
            ignoreConfigurer
                .requestMatchers("/actuator/**")
                .requestMatchers("/public/api/v1/landing-page/active-subscriber/**")
                .requestMatchers("/public/api/v1/esim-manager/esim-qr");
    }

    @Bean
    public SecurityRuleCustomizer securityRuleCustomizer() {
        log.info("SecurityRuleCustomizer");
        return ruleConfigurer ->
            ruleConfigurer
                .requestMatchers(
                    "/actuator/**",
                    "/actuator/health/**")
                .permitAll();
    }

}

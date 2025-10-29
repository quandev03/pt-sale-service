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
                .requestMatchers("/public/api/v1/hook/**")
                .requestMatchers("/private/api/v1/organization-unit/check-org-parent")
                .requestMatchers("/public/api/v1/update-subscriber-information/sign-contract")
                .requestMatchers("/public/api/v1/update-subscriber-information/preview-confirm-contract/**")
                .requestMatchers("/public/api/v1/update-subscriber-information/preview-confirm-contract-png/**")
                .requestMatchers("/private/api/v1/organization-user")
                .requestMatchers("/private/api/v1/organization-user/unit/**")
                .requestMatchers("/private/api/v1/organization-unit/get-org-name/**")
                .requestMatchers("/private/api/v1/organization-unit/get-info-org-unit/**")
                .requestMatchers("/private/api/v1/organization-user-private")
                .requestMatchers("/public/api/v1/esim-manager/esim-qr")
                .requestMatchers("/private/api/v1/update-subscriber-information/sign-contract")
                .requestMatchers("/private/api/v1/update-subscriber-information/preview-confirm-contract/**")
                .requestMatchers("/private/api/v1/update-subscriber-information/preview-confirm-contract-png/**")
                .requestMatchers("/public/api/v1/address/**")
            ;
    }

    @Bean
    public SecurityRuleCustomizer securityRuleCustomizer() {
        log.info("SecurityRuleCustomizer");
        return ruleConfigurer ->
            ruleConfigurer.requestMatchers("/public/api/v1/landing-page/active-subscriber/**")
                .permitAll()
                .requestMatchers(
                    "/actuator/**",
                    "/actuator/health/**")
                .permitAll()
                .requestMatchers("/public/api/v1/hook/**")
                .permitAll();
    }

}

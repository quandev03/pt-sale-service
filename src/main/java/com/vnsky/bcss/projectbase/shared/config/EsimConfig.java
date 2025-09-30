package com.vnsky.bcss.projectbase.shared.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class EsimConfig {

    @Value("${third-party.mobifone.user-mobifone}")
    private String userMobifone;

    @Value("${third-party.mobifone.hlrgw-username-book-esim}")
    private String hlrgwUsernameBookEsim;

    @Value("${third-party.mobifone.book-esim-profile-type}")
    private String bookEsimProfileType;

    @Value("${third-party.mobifone.bhm}")
    private String bhm;

    @Value("${third-party.mobifone.default-package}")
    private String defaultPackage;

    @Value("${third-party.mobifone.shop-code}")
    private String shopCode;

    @Value("${third-party.mobifone.employee}")
    private String employee;

    @Value("${third-party.mobifone.reason-code}")
    private String reasonCode;

    @Value("${third-party.mobifone.qlkh-username}")
    private String qlkhUsername;

    @Value("${third-party.mobifone.qlkh-password}")
    private String qlkhPassword;

    @Value("${third-party.mobifone.mobi-sub-type}")
    private String mobiSubType;

    @Value("${third-party.mobifone.book-esim-thread-pool-size}")
    private Integer bookEsimThreadPoolSize;
}

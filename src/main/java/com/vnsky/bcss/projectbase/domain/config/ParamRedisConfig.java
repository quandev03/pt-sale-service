package com.vnsky.bcss.projectbase.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redis")
@Data
public class ParamRedisConfig {
    private Key key;

    @Data
    public static class Key {
        private String prefix;
        private String query;
        private Decree13 decree13;
        private String activeSub;
        private String otp;
        private String c06;
        private String frequency;
        private String checkSum;
        private long expiryTime;
        private String timeUnit;

        @Data
        public static class Decree13 {
            private String data;
            private String check;
        }
    }

}

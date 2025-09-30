package com.vnsky.bcss.projectbase.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "param")
@Data
public class ParamContractConfig {

    private Template template;

    private Contract contract;

    private Image image;

    private UserSignature userSignature;

    @Data
    public static class UserSignature {
        private String userSignatureFolder;
        private String userSignatureType;
    }

    @Data
    public static class Template {

        private String contractSimActiveFolder;
        private String contractSimActiveFile;
        private String decree13confirmationRecord;
        private String countryCode;
    }

    @Data
    public static class Contract {
        private SimActive simActive;

        @Data
        public static class SimActive {

            private CSKH cskh;

            private Signature signature;

            private String finalContractType;

            private String finalContractFolder;

            private String customerCodeFormat;

            private String contractCodeFormat;

            private Map<String, String> tags;

            private ContractTmp contractTmp;

            @Data
            public static class ContractTmp {

                private String folder;
                private String fileName;
                private String fileType;
                private long expireTime;
                private String timeUnit;


            }

            @Data
            public static class CSKH {
                private String signatureFolder;
                private String signatureFileName;
                private int height;
                private int width;
            }

            @Data
            public static class Signature {
                private String folder;
                private String fileType;
            }
        }
    }

    @Data
    public static class Image {
        private Prefix prefix;
        private Suffix suffix;

        @Data
        public static class Prefix {
            private String front;
            private String back;
            private String portrait;
            private String contract;
        }

        @Data
        public static class Suffix {
            private String version1;
        }
    }
}

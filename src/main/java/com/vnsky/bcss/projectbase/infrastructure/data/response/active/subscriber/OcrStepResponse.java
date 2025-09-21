package com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OcrStepResponse {
    private String message;
    private String errCode;

    @JsonProperty("data_ocr")
    private DataOcr dataOcr;

    @JsonProperty("id_ekyc")
    private String idEkyc;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class DataOcr{
        private OcrPassport ocrPassport;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class OcrPassport{
        private Boolean validateDocument;
        private Integer docClass;
        private Float probClass;
        private Infors infors;
        private ProbInfors probInfors;
        private ValidateInfors validateInfors;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Infors{
        private String idNumber;
        private String fullname;
        private String nicNumber;
        private String dob;
        private String placeOfBirth;
        private String expiredDate;
        private String nationality;
        private String gender;
        private String mrz;
        private String issuedPlace;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ProbInfors{
        private String idNumber;
        private String fullname;
        private String nicNumber;
        private String dob;
        private String expiredDate;
        private String nationality;
        private String gender;
        private String mrz;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ValidateInfors{
        private Boolean idNumber;
        private Boolean fullname;
        private Boolean dob;
        private Boolean placeOfBirth;
        private Boolean expiredDate;
        private Boolean nationality;
        private Boolean gender;
        private Boolean issuedDate;
        private Boolean issuedPlace;
        private Boolean nicNumber;
    }
}

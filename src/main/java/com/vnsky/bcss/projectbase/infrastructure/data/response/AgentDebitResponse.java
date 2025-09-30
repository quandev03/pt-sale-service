package com.vnsky.bcss.projectbase.infrastructure.data.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentDebitResponse {
    private String code;
    private String message;
    private BodyAgentDebit body;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BodyAgentDebit {
        private String voucherCode;
        private String voucherType;
        private String voucherDate;
        private String description;
        private String amount;
        private String amountDebit;
    }
}


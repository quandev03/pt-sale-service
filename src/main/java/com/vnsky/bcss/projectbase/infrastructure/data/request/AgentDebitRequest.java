package com.vnsky.bcss.projectbase.infrastructure.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentDebitRequest implements Serializable {
    private String shopCode;
    private String voucherCode;
    private String voucherType;
}

package com.vnsky.bcss.projectbase.infrastructure.data.response.partner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebtRevenueResponse {
    private Long debtLimit;
    private Long debtLimitMbf;
    private Long revenues;
}

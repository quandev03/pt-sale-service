package com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber;

import com.vnsky.bcss.projectbase.domain.dto.AgreeDegree13DTO;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GenActiveSubscriberContractRequest {
    @NotEmpty(message = Constant.REQUIRED_FIELD)
    private String transactionId;

    @Valid
    private AgreeDegree13DTO agreeDegree13;
}

package com.vnsky.bcss.projectbase.infrastructure.primary.restful.payment.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionPaymentServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.PartnerPackageSubscriptionPaymentReturnResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.payment.VNPayPaymentOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VNPayPaymentRest implements VNPayPaymentOperation {

    private final PartnerPackageSubscriptionPaymentServicePort paymentServicePort;

    @Override
    public ResponseEntity<Object> handleReturn(Map<String, String> params) {
        var result = paymentServicePort.handleReturn(params);
        var response = PartnerPackageSubscriptionPaymentReturnResponse.builder()
            .subscriptionId(result.getSubscriptionId())
            .txnRef(result.getTxnRef())
            .responseCode(result.getResponseCode())
            .message(result.getMessage())
            .status(result.getStatus())
            .build();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> handleIpn(Map<String, String> params) {
        return ResponseEntity.ok(paymentServicePort.handleIpn(params));
    }
}


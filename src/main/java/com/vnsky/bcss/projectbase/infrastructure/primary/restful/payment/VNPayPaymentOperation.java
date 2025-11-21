package com.vnsky.bcss.projectbase.infrastructure.primary.restful.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "VNPay Payment", description = "Xử lý callback và IPN từ VNPay")
@RequestMapping("${application.path.base.public}/payment/vnpay")
public interface VNPayPaymentOperation {

    @Operation(summary = "VNPay return URL")
    @GetMapping("/return")
    ResponseEntity<Object> handleReturn(@RequestParam Map<String, String> params);

    @Operation(summary = "VNPay IPN endpoint")
    @PostMapping("/ipn")
    ResponseEntity<Object> handleIpn(@RequestParam Map<String, String> params);
}


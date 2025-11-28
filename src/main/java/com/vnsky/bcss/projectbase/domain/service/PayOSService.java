package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionPaymentServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PayOSServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionRepoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.Webhook;
import vn.payos.model.webhooks.WebhookData;


@Slf4j
@RequiredArgsConstructor
@Service
public class PayOSService implements PayOSServicePort {

    @Value("${payment.payos.api-key}")
    private String apiKey;

    @Value("${payment.payos.client-id}")
    private String clientID;

    @Value("${payment.payos.checksum-key}")
    private String checksum;

    private final PartnerPackageSubscriptionRepoPort subscriptionRepoPort;


    public CreatePaymentLinkResponse createPayOS(String orderId, Long orderAmount){
        PayOS payOS = new PayOS(apiKey, clientID, checksum);
        if(orderId == null || orderAmount == null){
            throw  new IllegalArgumentException("orderId and orderAmount must not be null");
        }
        CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
            .orderCode(System.currentTimeMillis() / 1000)
            .description(orderId)
            .amount(orderAmount)
            .description("Thanh toán đơn hàng")
            .cancelUrl("https://your-domain.com/cancel")
            .returnUrl("https://your-domain.com/success")
            .build();

        return payOS.paymentRequests().create(paymentRequest);
    }

    @Override
    public Object webhookPayOS(Webhook webhook) {
        PayOS payOS = new PayOS(apiKey, clientID, checksum);
        try {
            WebhookData data = payOS.webhooks().verify(webhook);
            log.info("Thanh toán thành công: {}" , data.getOrderCode());
            subscriptionRepoPort.updateStatusActive(data.getDescription());

            return "OK";
        } catch (Exception e) {
            log.error("Webhook không hợp lệ: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid webhook");
        }
    }






}

package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.BuyPackageResponseDTO;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.Webhook;

public interface PayOSServicePort {
    CreatePaymentLinkResponse createPayOS(String orderId, Long orderAmount);
    Object webhookPayOS(Webhook webhook);
}

package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.*;

import java.util.Map;

public interface PartnerPackageSubscriptionPaymentServicePort {

    PartnerPackageSubscriptionPaymentInitResult initiatePayment(PartnerPackageSubscriptionPaymentCommand command);

    PartnerPackageSubscriptionPaymentReturnResult handleReturn(Map<String, String> params);

    VNPayIpnResponse handleIpn(Map<String, String> params);
}


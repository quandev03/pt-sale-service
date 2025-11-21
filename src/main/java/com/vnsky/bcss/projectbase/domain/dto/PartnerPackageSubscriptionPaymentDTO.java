package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PartnerPackageSubscriptionPaymentDTO extends AbstractAuditDTO {

    private String id;
    private String subscriptionId;
    private String txnRef;
    private Long amount;
    private String orderInfo;
    private String bankCode;
    private String bankTranNo;
    private String cardType;
    private LocalDateTime payDate;
    private String responseCode;
    private String transactionNo;
    private String terminalCode;
    private String secureHash;
    private PartnerPackageSubscriptionPaymentStatus paymentStatus;
    private String requestIp;
    private String payUrl;
    private String requestRaw;
    private String rawResponse;
    private LocalDateTime callbackAt;
}


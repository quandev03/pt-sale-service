package com.vnsky.bcss.projectbase.domain.entity;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "PARTNER_PACKAGE_SUBSCRIPTION_PAYMENT")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PartnerPackageSubscriptionPaymentEntity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "SUBSCRIPTION_ID", nullable = false)
    private String subscriptionId;

    @Column(name = "TXN_REF", nullable = false, unique = true)
    private String txnRef;

    @Column(name = "AMOUNT", nullable = false)
    private Long amount;

    @Column(name = "ORDER_INFO")
    private String orderInfo;

    @Column(name = "BANK_CODE")
    private String bankCode;

    @Column(name = "BANK_TRAN_NO")
    private String bankTranNo;

    @Column(name = "CARD_TYPE")
    private String cardType;

    @Column(name = "PAY_DATE")
    private LocalDateTime payDate;

    @Column(name = "RESPONSE_CODE")
    private String responseCode;

    @Column(name = "TRANSACTION_NO")
    private String transactionNo;

    @Column(name = "TERMINAL_CODE")
    private String terminalCode;

    @Column(name = "SECURE_HASH")
    private String secureHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private PartnerPackageSubscriptionPaymentStatus paymentStatus;

    @Column(name = "REQUEST_IP")
    private String requestIp;

    @Column(name = "PAY_URL")
    private String payUrl;

    @Lob
    @Column(name = "REQUEST_RAW")
    private String requestRaw;

    @Lob
    @Column(name = "RESPONSE_RAW")
    private String rawResponse;

    @Column(name = "CALLBACK_AT")
    private LocalDateTime callbackAt;
}


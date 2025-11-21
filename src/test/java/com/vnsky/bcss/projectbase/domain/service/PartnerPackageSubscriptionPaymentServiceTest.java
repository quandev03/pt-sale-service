package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionPaymentRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionRepoPort;
import com.vnsky.bcss.projectbase.shared.config.VNPayProperties;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;
import com.vnsky.bcss.projectbase.shared.utils.VNPaySignatureUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerPackageSubscriptionPaymentServiceTest {

    @Mock
    private PartnerPackageSubscriptionRepoPort subscriptionRepoPort;
    @Mock
    private PartnerPackageSubscriptionPaymentRepoPort paymentRepoPort;
    @Mock
    private OrganizationUnitRepoPort organizationUnitRepoPort;
    @Mock
    private PackageProfileRepoPort packageProfileRepoPort;

    private VNPayProperties vnPayProperties;
    private ObjectMapper objectMapper;

    private PartnerPackageSubscriptionPaymentService service;

    @BeforeEach
    void setUp() {
        vnPayProperties = new VNPayProperties();
        vnPayProperties.setTmnCode("TESTCODE");
        vnPayProperties.setHashSecret("SECRET1234567890SECRET1234567890");
        vnPayProperties.setUrl("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        vnPayProperties.setReturnUrl("https://example.com/return");
        vnPayProperties.setIpnUrl("https://example.com/ipn");
        vnPayProperties.setVersion("2.1.0");
        vnPayProperties.setCommand("pay");
        vnPayProperties.setOrderType("test");
        vnPayProperties.setCurrency("VND");
        vnPayProperties.setLocale("vn");
        objectMapper = new ObjectMapper();

        service = new PartnerPackageSubscriptionPaymentService(
            subscriptionRepoPort,
            paymentRepoPort,
            organizationUnitRepoPort,
            packageProfileRepoPort,
            vnPayProperties,
            objectMapper
        );
    }

    @Test
    void initiatePayment_shouldPersistPendingPaymentAndReturnRedirect() {
        OrganizationUnitDTO organizationUnit = OrganizationUnitDTO.builder()
            .id("ORG-1")
            .orgName("Org Test")
            .status(Status.ACTIVE.getValue())
            .contractDate(LocalDate.now())
            .build();
        PackageProfileDTO packageProfile = PackageProfileDTO.builder()
            .id("PKG-1")
            .pckName("Goi Test")
            .packagePrice(1_000_000L)
            .cycleUnit(0)
            .cycleValue(30)
            .status(Status.ACTIVE.getValue())
            .build();

        when(organizationUnitRepoPort.findById("ORG-1")).thenReturn(Optional.of(organizationUnit));
        when(packageProfileRepoPort.findById("PKG-1")).thenReturn(packageProfile);
        when(subscriptionRepoPort.findByOrgUnitAndPackageAndStatuses(anyString(), anyString(), anyList()))
            .thenReturn(Optional.empty());
        when(subscriptionRepoPort.saveAndFlush(any())).thenAnswer(invocation -> {
            PartnerPackageSubscriptionDTO dto = invocation.getArgument(0);
            dto.setId("SUB-1");
            return dto;
        });
        when(paymentRepoPort.saveAndFlush(any())).thenAnswer(invocation -> {
            PartnerPackageSubscriptionPaymentDTO dto = invocation.getArgument(0);
            dto.setId("PAY-1");
            return dto;
        });

        var command = PartnerPackageSubscriptionPaymentCommand.builder()
            .organizationUnitId("ORG-1")
            .packageProfileId("PKG-1")
            .clientIp("10.0.0.1")
            .build();

        PartnerPackageSubscriptionPaymentInitResult result = service.initiatePayment(command);

        assertThat(result.getSubscriptionId()).isEqualTo("SUB-1");
        assertThat(result.getPaymentId()).isEqualTo("PAY-1");
        assertThat(result.getStatus()).isEqualTo(PartnerPackageSubscriptionPaymentStatus.PENDING);
        assertThat(result.getPaymentUrl()).startsWith(vnPayProperties.getUrl() + "?");

        ArgumentCaptor<PartnerPackageSubscriptionPaymentDTO> paymentCaptor = ArgumentCaptor.forClass(PartnerPackageSubscriptionPaymentDTO.class);
        verify(paymentRepoPort).saveAndFlush(paymentCaptor.capture());
        PartnerPackageSubscriptionPaymentDTO savedPayment = paymentCaptor.getValue();
        assertThat(savedPayment.getPaymentStatus()).isEqualTo(PartnerPackageSubscriptionPaymentStatus.PENDING);
        assertThat(savedPayment.getRequestIp()).isEqualTo("10.0.0.1");
        assertThat(savedPayment.getPayUrl()).isEqualTo(result.getPaymentUrl());
    }

    @Test
    void handleReturn_shouldActivateSubscriptionOnSuccess() {
        PartnerPackageSubscriptionPaymentDTO payment = PartnerPackageSubscriptionPaymentDTO.builder()
            .id("PAY-1")
            .subscriptionId("SUB-1")
            .txnRef("TXN123")
            .amount(500_000L)
            .paymentStatus(PartnerPackageSubscriptionPaymentStatus.PENDING)
            .build();
        PartnerPackageSubscriptionDTO subscription = PartnerPackageSubscriptionDTO.builder()
            .id("SUB-1")
            .organizationUnitId("ORG-1")
            .packageProfileId("PKG-1")
            .status(PartnerPackageSubscriptionStatus.PENDING_PAYMENT)
            .build();
        PackageProfileDTO packageProfile = PackageProfileDTO.builder()
            .id("PKG-1")
            .cycleUnit(0)
            .cycleValue(30)
            .status(Status.ACTIVE.getValue())
            .build();

        when(paymentRepoPort.findByTxnRef("TXN123")).thenReturn(Optional.of(payment));
        when(subscriptionRepoPort.findById("SUB-1")).thenReturn(Optional.of(subscription));
        when(packageProfileRepoPort.findById("PKG-1")).thenReturn(packageProfile);
        when(paymentRepoPort.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(subscriptionRepoPort.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", "TXN123");
        params.put("vnp_Amount", String.valueOf(payment.getAmount() * 100));
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionStatus", "00");
        params.put("vnp_PayDate", "20250101120000");
        params.put("vnp_BankCode", "NCB");
        params.put("vnp_CardType", "ATM");
        params.put("vnp_BankTranNo", "BANK123");
        params.put("vnp_TransactionNo", "TRANS001");
        params.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        String secureHash = VNPaySignatureUtils.generateSignature(params, vnPayProperties.getHashSecret());
        params.put("vnp_SecureHash", secureHash);

        var result = service.handleReturn(params);

        assertThat(result.getStatus()).isEqualTo(PartnerPackageSubscriptionPaymentStatus.SUCCESS);
        assertThat(result.getResponseCode()).isEqualTo("00");
        assertThat(result.getMessage()).contains("thành công");

        ArgumentCaptor<PartnerPackageSubscriptionDTO> subCaptor = ArgumentCaptor.forClass(PartnerPackageSubscriptionDTO.class);
        verify(subscriptionRepoPort).saveAndFlush(subCaptor.capture());
        assertThat(subCaptor.getValue().getStatus()).isEqualTo(PartnerPackageSubscriptionStatus.ACTIVE);
        assertThat(subCaptor.getValue().getStartTime()).isNotNull();
    }

    @Test
    void handleIpn_shouldReturnInvalidSignatureWhenHashMismatch() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", "TXN999");
        params.put("vnp_Amount", "10000");
        params.put("vnp_SecureHash", "INVALID");

        VNPayIpnResponse response = service.handleIpn(params);

        assertThat(response.getRspCode()).isEqualTo("97");
        assertThat(response.getMessage()).isEqualTo("Invalid signature");
        verifyNoInteractions(paymentRepoPort, subscriptionRepoPort);
    }
}



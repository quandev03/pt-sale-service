package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.PartnerPackageSubscriptionPaymentServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionPaymentRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionRepoPort;
import com.vnsky.bcss.projectbase.shared.config.VNPayProperties;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;
import com.vnsky.bcss.projectbase.shared.utils.VNPaySignatureUtils;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartnerPackageSubscriptionPaymentService implements PartnerPackageSubscriptionPaymentServicePort {

    private static final DateTimeFormatter VNP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String SUCCESS_CODE = "00";
    private static final String DEFAULT_IP = "127.0.0.1";

    private final PartnerPackageSubscriptionRepoPort subscriptionRepoPort;
    private final PartnerPackageSubscriptionPaymentRepoPort paymentRepoPort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;
    private final PackageProfileRepoPort packageProfileRepoPort;
    private final VNPayProperties vnPayProperties;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public PartnerPackageSubscriptionPaymentInitResult initiatePayment(PartnerPackageSubscriptionPaymentCommand command) {
        OrganizationUnitDTO organizationUnit = organizationUnitRepoPort.findById(command.getOrganizationUnitId())
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED).build());

        if (!Objects.equals(organizationUnit.getStatus(), Status.ACTIVE.getValue())) {
            throw BaseException.badRequest(ErrorCode.ORG_NOT_EXISTED)
                .message("Đơn vị không hoạt động hoặc không tồn tại")
                .build();
        }

        PackageProfileDTO packageProfile = Optional.ofNullable(packageProfileRepoPort.findById(command.getPackageProfileId()))
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.PACKAGE_NOT_EXISTS).build());

        if (!Objects.equals(packageProfile.getStatus(), Status.ACTIVE.getValue())) {
            throw BaseException.badRequest(ErrorCode.PACKAGE_PROFILE_INVALID)
                .message("Gói cước chưa được bật trạng thái hoạt động")
                .build();
        }

        if (packageProfile.getPackagePrice() == null || packageProfile.getPackagePrice() <= 0) {
            throw BaseException.badRequest(ErrorCode.PACKAGE_PROFILE_INVALID)
                .message("Giá gói dịch vụ không hợp lệ")
                .build();
        }

        subscriptionRepoPort.findByOrgUnitAndPackageAndStatuses(
                organizationUnit.getId(),
                packageProfile.getId(),
                List.of(PartnerPackageSubscriptionStatus.ACTIVE, PartnerPackageSubscriptionStatus.PENDING_PAYMENT)
            )
            .ifPresent(existing -> {
                throw BaseException.badRequest(ErrorCode.PARTNER_PACKAGE_SUBSCRIPTION_DUPLICATED)
                    .message("Đối tác đã có gói này đang hoạt động hoặc chờ thanh toán")
                    .build();
            });

        LocalDateTime startTime = Optional.ofNullable(command.getStartTime()).orElse(LocalDateTime.now());
        LocalDateTime endTime = calculateEndTime(startTime, packageProfile.getCycleValue(), packageProfile.getCycleUnit());

        PartnerPackageSubscriptionDTO subscription = PartnerPackageSubscriptionDTO.builder()
            .organizationUnitId(organizationUnit.getId())
            .packageProfileId(packageProfile.getId())
            .startTime(startTime)
            .endTime(endTime)
            .status(PartnerPackageSubscriptionStatus.PENDING_PAYMENT)
            .build();

        subscription = subscriptionRepoPort.saveAndFlush(subscription);

        String txnRef = generateTxnRef();
        Long amount = packageProfile.getPackagePrice();
        String orderInfo = String.format("Thanh toan goi %s cho %s", packageProfile.getPckName(), organizationUnit.getOrgName());

        Map<String, String> params = buildVNPayParams(command, txnRef, amount, orderInfo);
        String paymentUrl = VNPaySignatureUtils.buildSignedUrl(vnPayProperties.getUrl(), params, vnPayProperties.getHashSecret());
        String secureHash = VNPaySignatureUtils.generateSignature(params, vnPayProperties.getHashSecret());
        String clientIp = StringUtils.hasText(command.getClientIp()) ? command.getClientIp() : DEFAULT_IP;

        PartnerPackageSubscriptionPaymentDTO payment = PartnerPackageSubscriptionPaymentDTO.builder()
            .subscriptionId(subscription.getId())
            .txnRef(txnRef)
            .amount(amount)
            .orderInfo(orderInfo)
            .terminalCode(vnPayProperties.getTmnCode())
            .paymentStatus(PartnerPackageSubscriptionPaymentStatus.PENDING)
            .secureHash(secureHash)
            .requestIp(clientIp)
            .payUrl(paymentUrl)
            .requestRaw(serializePayload(params))
            .build();

        payment = paymentRepoPort.saveAndFlush(payment);

        return PartnerPackageSubscriptionPaymentInitResult.builder()
            .subscriptionId(subscription.getId())
            .paymentId(payment.getId())
            .txnRef(txnRef)
            .amount(amount)
            .orderInfo(orderInfo)
            .paymentUrl(paymentUrl)
            .status(PartnerPackageSubscriptionPaymentStatus.PENDING)
            .build();
    }

    @Override
    @Transactional
    public PartnerPackageSubscriptionPaymentReturnResult handleReturn(Map<String, String> params) {
        CallbackResult result = processCallback(params);
        return PartnerPackageSubscriptionPaymentReturnResult.builder()
            .subscriptionId(result.subscriptionId)
            .txnRef(result.txnRef)
            .responseCode(result.responseCode)
            .message(result.message)
            .status(result.status)
            .build();
    }

    @Override
    @Transactional
    public VNPayIpnResponse handleIpn(Map<String, String> params) {
        CallbackResult result = processCallback(params);
        return VNPayIpnResponse.builder()
            .rspCode(result.rspCode)
            .message(result.message)
            .build();
    }

    private CallbackResult processCallback(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return CallbackResult.fail("99", "Invalid parameters");
        }

        if (!VNPaySignatureUtils.validateSignature(params, vnPayProperties.getHashSecret())) {
            return CallbackResult.fail("97", "Invalid signature");
        }

        String txnRef = params.get("vnp_TxnRef");
        if (!StringUtils.hasText(txnRef)) {
            return CallbackResult.fail("01", "Order not found");
        }

        PartnerPackageSubscriptionPaymentDTO payment = paymentRepoPort.findByTxnRef(txnRef)
            .orElse(null);

        if (payment == null) {
            return CallbackResult.fail("01", "Order not found");
        }

        PartnerPackageSubscriptionDTO subscription = subscriptionRepoPort.findById(payment.getSubscriptionId())
            .orElse(null);

        if (subscription == null) {
            return CallbackResult.fail("01", "Subscription not found");
        }

        if (PartnerPackageSubscriptionPaymentStatus.SUCCESS.equals(payment.getPaymentStatus())) {
            return CallbackResult.success(
                txnRef,
                subscription.getId(),
                "02",
                payment.getResponseCode(),
                "Order already confirmed",
                PartnerPackageSubscriptionPaymentStatus.SUCCESS
            );
        }

        Long expectedAmount = payment.getAmount();
        Long paidAmount = parseAmount(params.get("vnp_Amount"));
        if (paidAmount == null || !Objects.equals(expectedAmount, paidAmount)) {
            return CallbackResult.fail("04", "Invalid amount");
        }

        String responseCode = params.getOrDefault("vnp_ResponseCode", "00");
        String transactionStatus = params.getOrDefault("vnp_TransactionStatus", responseCode);
        boolean success = SUCCESS_CODE.equals(responseCode) && SUCCESS_CODE.equals(transactionStatus);
        PartnerPackageSubscriptionPaymentStatus targetStatus = success ? PartnerPackageSubscriptionPaymentStatus.SUCCESS : PartnerPackageSubscriptionPaymentStatus.FAILED;

        payment.setBankCode(params.get("vnp_BankCode"));
        payment.setBankTranNo(params.get("vnp_BankTranNo"));
        payment.setCardType(params.get("vnp_CardType"));
        payment.setResponseCode(responseCode);
        payment.setTransactionNo(params.get("vnp_TransactionNo"));
        payment.setTerminalCode(params.get("vnp_TmnCode"));
        payment.setSecureHash(params.get("vnp_SecureHash"));
        payment.setPaymentStatus(targetStatus);
        payment.setRawResponse(serializePayload(params));
        LocalDateTime callbackAt = LocalDateTime.now();
        payment.setCallbackAt(callbackAt);
        LocalDateTime payDate = parseDate(params.get("vnp_PayDate"));
        payment.setPayDate(payDate);
        paymentRepoPort.saveAndFlush(payment);

        if (success) {
            LocalDateTime activatedAt = Optional.ofNullable(payDate).orElse(callbackAt);
            subscription.setStatus(PartnerPackageSubscriptionStatus.ACTIVE);
            subscription.setStartTime(activatedAt);
            PackageProfileDTO refreshedPackage = Optional.ofNullable(packageProfileRepoPort.findById(subscription.getPackageProfileId()))
                .orElse(null);
            if (refreshedPackage != null) {
                subscription.setEndTime(calculateEndTime(activatedAt, refreshedPackage.getCycleValue(), refreshedPackage.getCycleUnit()));
            }
        } else {
            subscription.setStatus(PartnerPackageSubscriptionStatus.FAILED);
            subscription.setEndTime(callbackAt);
        }
        subscriptionRepoPort.saveAndFlush(subscription);

        String message = success ? "Thanh toán thành công" : "Thanh toán thất bại";

        return CallbackResult.success(
            txnRef,
            subscription.getId(),
            "00",
            responseCode,
            message,
            targetStatus
        );
    }

    private Map<String, String> buildVNPayParams(PartnerPackageSubscriptionPaymentCommand command,
                                                 String txnRef,
                                                 Long amount,
                                                 String orderInfo) {
        Map<String, String> params = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        params.put("vnp_Version", vnPayProperties.getVersion());
        params.put("vnp_Command", vnPayProperties.getCommand());
        params.put("vnp_TmnCode", vnPayProperties.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_CurrCode", vnPayProperties.getCurrency());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", vnPayProperties.getOrderType());
        params.put("vnp_Locale", vnPayProperties.getLocale());
        params.put("vnp_ReturnUrl", StringUtils.hasText(command.getReturnUrl()) ? command.getReturnUrl() : vnPayProperties.getReturnUrl());
        params.put("vnp_IpAddr", StringUtils.hasText(command.getClientIp()) ? command.getClientIp() : DEFAULT_IP);
        params.put("vnp_CreateDate", now.format(VNP_DATE_FORMAT));
        params.put("vnp_ExpireDate", now.plusMinutes(15).format(VNP_DATE_FORMAT));
        params.put("vnp_SecureHashType", "HmacSHA512");
        return params;
    }

    private String generateTxnRef() {
        return VNP_DATE_FORMAT.format(LocalDateTime.now()) + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }

    private LocalDateTime calculateEndTime(LocalDateTime startTime, Integer cycleValue, Integer cycleUnit) {
        if (cycleValue == null || cycleUnit == null || cycleValue <= 0) {
            throw BaseException.badRequest(ErrorCode.PARTNER_PACKAGE_SUBSCRIPTION_INVALID_CYCLE)
                .message("Chu kỳ gói cước không hợp lệ")
                .build();
        }

        return switch (cycleUnit) {
            case 0 -> startTime.plusDays(cycleValue.longValue());
            case 1 -> startTime.plusMonths(cycleValue.longValue());
            default -> throw BaseException.badRequest(ErrorCode.PARTNER_PACKAGE_SUBSCRIPTION_INVALID_CYCLE)
                .message("Đơn vị chu kỳ không hỗ trợ")
                .build();
        };
    }

    private Long parseAmount(String amount) {
        if (!StringUtils.hasText(amount)) {
            return null;
        }
        try {
            long value = Long.parseLong(amount);
            return value / 100;
        } catch (NumberFormatException ex) {
            log.warn("Cannot parse VNPay amount: {}", amount);
            return null;
        }
    }

    private LocalDateTime parseDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, VNP_DATE_FORMAT);
        } catch (Exception ex) {
            log.warn("Cannot parse VNPay date: {}", value);
            return null;
        }
    }

    private String serializePayload(Map<String, String> params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            return params.toString();
        }
    }

    private record CallbackResult(String rspCode,
                                  String responseCode,
                                  String message,
                                  String subscriptionId,
                                  String txnRef,
                                  PartnerPackageSubscriptionPaymentStatus status) {

        static CallbackResult fail(String rspCode, String message) {
            return new CallbackResult(rspCode, rspCode, message, null, null, PartnerPackageSubscriptionPaymentStatus.FAILED);
        }

        static CallbackResult success(String txnRef,
                                      String subscriptionId,
                                      String rspCode,
                                      String responseCode,
                                      String message,
                                      PartnerPackageSubscriptionPaymentStatus status) {
            return new CallbackResult(rspCode, responseCode, message, subscriptionId, txnRef, status);
        }
    }
}


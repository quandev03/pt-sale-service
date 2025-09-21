package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.EsimBookingResult;
import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.StockIsdnRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SaleOrderRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.EsimRegistrationLineRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SaleOrderLineRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.dto.SaleOrderDTO;
import com.vnsky.bcss.projectbase.domain.dto.EsimRegistrationLineDTO;
import com.vnsky.bcss.projectbase.domain.dto.SaleOrderLineDTO;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.external.BookEsimMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.external.CraftKitMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.external.RegisterPackageMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.BookESimMbfResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.CraftKitMbfResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.RegisterPackageMbfResponse;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.shared.config.EsimConfig;
import com.vnsky.bcss.projectbase.shared.constant.IntegrationConstant;
import com.vnsky.bcss.projectbase.shared.constant.EsimBookingConstant;
import com.vnsky.bcss.projectbase.shared.utils.RegisterPackageErrorHandler;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.dto.StockIsdnDTO;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import com.vnsky.bcss.projectbase.domain.dto.EsimErrorInfo;
import com.vnsky.bcss.projectbase.domain.dto.EsimProcessingResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookEsimService implements BookEsimServicePort {

    private final IntegrationPort integrationPort;
    private final SubscriberRepoPort subscriberRepoPort;
    private final StockIsdnRepoPort stockIsdnRepoPort;
    private final OrganizationUserRepoPort organizationUserRepoPort;
    private final SaleOrderRepoPort saleOrderRepoPort;
    private final EsimRegistrationLineRepoPort esimRegistrationLineRepoPort;
    private final SaleOrderLineRepoPort saleOrderLineRepoPort;
    private final PackageProfileRepoPort packageProfileRepoPort;
    private final EsimConfig esimConfig;

    private static final int RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 500;
    private static final long TEST_ISDN = 10_000;
    private static final int BOOKED_STATUS = 4;
    private static final int NOT_BLOCKING = 1;
    private static final int THREAD_POOL_SIZE = 10;

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private <T> T retry(Callable<T> action) {
        int attempt = 0;
        long delayMs = RETRY_DELAY_MS;

        while (true) {
            try {
                return action.call();
            } catch (Exception e) {
                attempt++;
                if (attempt >= RETRY_ATTEMPTS) {
                    log.error("[RETRY] {} failed after {} attempts", "Book eSIM from external API", attempt, e);
                    throw new RuntimeException("[RETRY] " + "Book eSIM from external API" + " failed after " + attempt + " attempts: " + e.getMessage(), e);
                }
                log.warn("[RETRY] {} failed on attempt {}/{}. Retrying in {}ms. Error: {}", "Book eSIM from external API", attempt, RETRY_ATTEMPTS, delayMs, e.getMessage());

                try {
                    Thread.sleep(delayMs);
                    delayMs = Math.min(delayMs * 2, 2000); // Cap at 2 seconds
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Operation interrupted: " + "Book eSIM from external API", ie);
                }
            }
        }
    }

    @Override
    @Transactional
    public BookEsimResponse bookEsim(BookEsimRequest request) {
        log.info("Starting eSIM booking process for quantity: {}, package: {}",
                request.getQuantity(), request.getPackageCode());

        String currentUserId = SecurityUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw BaseException.badRequest(ErrorCode.INVALID_USER).build();
        }

        OrganizationUserDTO organizationUser = organizationUserRepoPort.findByUserId(currentUserId)
                .orElseThrow(() -> BaseException.badRequest(ErrorCode.INVALID_USER).build());

        if (request.getPackageCode() != null && !request.getPackageCode().isEmpty()) {
            Optional<PackageProfileDTO> packageProfile = packageProfileRepoPort.findByPckCode(request.getPackageCode());
            if (packageProfile.isEmpty()) {
                log.error("Package code not found in database: {}", request.getPackageCode());
                throw BaseException.badRequest(ErrorCode.INVALID_PACKAGE_CODE).build();
            }
        }

        SaleOrderDTO saleOrder = saleOrderRepoPort.saveAndFlush(SaleOrderDTO.builder()
                .orgId(organizationUser.getOrgId())
                .orderType(EsimBookingConstant.BOOK_ESIM)
                .quantity(BigDecimal.valueOf(request.getQuantity()))
                .bookEsimStatus(EsimBookingConstant.PROCESSING)
                .pckCode(request.getPackageCode())
                .orderDate(LocalDateTime.now())
                .createdBy("SYSTEM")
                .createdDate(LocalDateTime.now())
                .build());

        List<StockIsdnDTO> availableIsdns = stockIsdnRepoPort.findAvailableIsdns(request.getQuantity());
        if (availableIsdns.size() < request.getQuantity()) {
            log.error("Not enough available ISDNs in stock. Requested: {}, Found: {}", request.getQuantity(), availableIsdns.size());
            throw BaseException.badRequest(ErrorCode.INSUFFICIENT_ELIGIBLE_SUBSCRIBERS).build();
        }

        List<EsimRegistrationLineDTO> registrationLines = availableIsdns.stream()
                .<EsimRegistrationLineDTO>map(stockIsdn -> EsimRegistrationLineDTO.builder()
                        .isdn(stockIsdn.getIsdn())
                        .saleOrderId(saleOrder.getId())
                        .status(null)
                        .createdBy("SYSTEM")
                        .createdDate(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        esimRegistrationLineRepoPort.saveAllAndFlush(registrationLines);

        Map<String, String> errors = new ConcurrentHashMap<>();
        List<EsimBookingResult> successfulResults = new ArrayList<>();
        List<EsimErrorInfo> errorInfos = new ArrayList<>();

        try {
            // Process external API calls in parallel but collect results for database operations
            List<CompletableFuture<EsimProcessingResult>> bookingFutures = IntStream.range(0, request.getQuantity())
                .mapToObj(i -> {
                    StockIsdnDTO stockIsdn = availableIsdns.get(i);
                    EsimRegistrationLineDTO registrationLine = registrationLines.get(i);
                    return CompletableFuture.supplyAsync(
                        () -> processExternalApiCalls(request.getPackageCode(), stockIsdn, registrationLine, errors),
                        executorService
                    );
                })
                .toList();

            // Wait for all external API calls to complete
            CompletableFuture<Void> allBookings = CompletableFuture.allOf(
                bookingFutures.toArray(new CompletableFuture[0])
            );

            // Collect results and process database operations in main transaction
            List<EsimProcessingResult> results = allBookings.thenApply(v ->
                bookingFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList())
            ).join();

            // Process database operations in main transaction
            for (int i = 0; i < results.size(); i++) {
                EsimProcessingResult processingResult = results.get(i);

                // Handle successful results
                if (!processingResult.getSuccessfulResults().isEmpty()) {
                    EsimBookingResult result = processingResult.getSuccessfulResults().get(0);
                    successfulResults.add(result);
                    EsimRegistrationLineDTO updatedRegistrationLine = EsimRegistrationLineDTO.builder()
                        .id(registrationLines.get(i).getId())
                        .isdn(registrationLines.get(i).getIsdn())
                        .saleOrderId(registrationLines.get(i).getSaleOrderId())
                        .serial(result.getSerial())
                        .imsi(Long.parseLong(result.getRegistration().getSerial()))
                        .lpa(result.getQrCode())
                        .createdBy(registrationLines.get(i).getCreatedBy())
                        .createdDate(registrationLines.get(i).getCreatedDate())
                        .modifiedBy("SYSTEM")
                        .modifiedDate(LocalDateTime.now())
                        .build();
                    esimRegistrationLineRepoPort.saveAndFlush(updatedRegistrationLine);
                }

                // Handle error results
                if (!processingResult.getErrorInfos().isEmpty()) {
                    errorInfos.addAll(processingResult.getErrorInfos());
                }
            }

            if (!successfulResults.isEmpty()) {
                List<SubscriberDTO> registrations = successfulResults.stream()
                    .map(EsimBookingResult::getRegistration)
                    .toList();
                subscriberRepoPort.saveAllAndFlush(new ArrayList<>(registrations));

                PackageProfileDTO packageProfile = packageProfileRepoPort.findByPckCode(request.getPackageCode())
                    .orElse(null);
                Long price = packageProfile != null ? packageProfile.getPackagePrice() : null;

                List<SaleOrderLineDTO> saleOrderLines = successfulResults.stream()
                    .map(result -> SaleOrderLineDTO.builder()
                        .saleOrderId(saleOrder.getId())
                        .subId(result.getRegistration().getId())
                        .price(price)
                        .createdBy("SYSTEM")
                        .createdDate(LocalDateTime.now())
                        .build())
                    .collect(Collectors.toList());
                saleOrderLineRepoPort.saveAllAndFlush(saleOrderLines);
            }

            List<StockIsdnDTO> updatedStockIsdns = availableIsdns.stream()
                .map(stockIsdn -> StockIsdnDTO.builder()
                    .id(stockIsdn.getId())
                    .isdn(stockIsdn.getIsdn())
                    .serial(stockIsdn.getSerial())
                    .imsi(stockIsdn.getImsi())
                    .status(BOOKED_STATUS)
                    .activeDatetime(LocalDateTime.now())
                    .deleteDatetime(stockIsdn.getDeleteDatetime())
                    .description(stockIsdn.getDescription())
                    .modifiedBy("SYSTEM")
                    .modifiedDate(LocalDateTime.now())
                    .createdBy(stockIsdn.getCreatedBy())
                    .createdDate(stockIsdn.getCreatedDate())
                    .build())
                .collect(Collectors.toList());

            stockIsdnRepoPort.saveAllAndFlush(updatedStockIsdns);

            for (EsimErrorInfo errorInfo : errorInfos) {
                try {
                    EsimRegistrationLineDTO errorRegistrationLine = EsimRegistrationLineDTO.builder()
                        .id(errorInfo.getRegistrationLine().getId())
                        .serial(errorInfo.getRegistrationLine().getSerial())
                        .isdn(errorInfo.getRegistrationLine().getIsdn())
                        .status(errorInfo.getErrorStatus())
                        .imsi(errorInfo.getRegistrationLine().getImsi())
                        .saleOrderId(errorInfo.getRegistrationLine().getSaleOrderId())
                        .lpa(errorInfo.getRegistrationLine().getLpa())
                        .modifiedBy("SYSTEM")
                        .modifiedDate(LocalDateTime.now())
                        .createdBy(errorInfo.getRegistrationLine().getCreatedBy())
                        .createdDate(errorInfo.getRegistrationLine().getCreatedDate())
                        .build();
                    esimRegistrationLineRepoPort.saveAndFlush(errorRegistrationLine);
                } catch (Exception updateError) {
                    log.error("Failed to update registration line with error status for ISDN: {}", errorInfo.getIsdn(), updateError);
                }
            }

            int succeededCount = successfulResults.size();
            int failedCount = request.getQuantity() - succeededCount;

            SaleOrderDTO updatedSaleOrder = SaleOrderDTO.builder()
                .id(saleOrder.getId())
                .orgId(saleOrder.getOrgId())
                .orderNo(saleOrder.getOrderNo())
                .amountTotal(saleOrder.getAmountTotal())
                .status(saleOrder.getStatus())
                .reasonId(saleOrder.getReasonId())
                .description(saleOrder.getDescription())
                .note(saleOrder.getNote())
                .orderType(saleOrder.getOrderType())
                .customerEmail(saleOrder.getCustomerEmail())
                .quantity(saleOrder.getQuantity())
                .cancelReason(saleOrder.getCancelReason())
                .succeededNumber(succeededCount)
                .failedNumber(failedCount)
                .finishedDate(LocalDateTime.now())
                .bookEsimStatus(EsimBookingConstant.DONE)
                .pckCode(saleOrder.getPckCode())
                .orderDate(saleOrder.getOrderDate())
                .modifiedBy("SYSTEM")
                .modifiedDate(LocalDateTime.now())
                .createdBy(saleOrder.getCreatedBy())
                .createdDate(saleOrder.getCreatedDate())
                .build();

            saleOrderRepoPort.saveAndFlush(updatedSaleOrder);

            if (!errors.isEmpty()) {
                log.error("Errors occurred during eSIM booking process: {}", errors);
            }

            log.info("Successfully completed eSIM booking for {} quantities. Succeeded: {}, Failed: {}",
                    request.getQuantity(), succeededCount, failedCount);

            List<String> serials = successfulResults.stream()
                .map(EsimBookingResult::getSerial)
                .collect(Collectors.toList());

            List<String> qrCodes = successfulResults.stream()
                .map(EsimBookingResult::getQrCode)
                .collect(Collectors.toList());

            return BookEsimResponse.builder()
                .serials(serials)
                .qrCodes(qrCodes)
                .status("SUCCESS")
                .message("eSIM booking completed successfully. Succeeded: " + succeededCount + ", Failed: " + failedCount)
                .createdDate(LocalDateTime.now())
                .build();

        } catch (Exception e) {
            log.error("Error during eSIM booking process", e);
            throw new RuntimeException("Failed to book eSIM: " + e.getMessage());
        }
    }

    private EsimProcessingResult processExternalApiCalls(String packageCode, StockIsdnDTO stockIsdn,
                                                        EsimRegistrationLineDTO registrationLine,
                                                        Map<String, String> errors) {
        try {
            BookESimMbfResponse bookResponse = retry(this::bookEsimFromExternalAPI);
            BookESimMbfResponse.BookEsimInfo bookInfo = getBookEsimInfo(bookResponse);
            String serial = bookInfo.getSerial();
            String qrCode = bookInfo.getQr();
            Long imsi = Long.parseLong(bookInfo.getImsi());
            String esimGwId = bookInfo.getEsimGwId();

            // Call external APIs in parallel
            craftKitForSerial(serial);
            registerPackageForSerial(serial, packageCode);

            // Create subscriber registration
            SubscriberDTO registration = SubscriberDTO.builder()
                .serial(serial)
                .isdn(stockIsdn.getIsdn())
                .imsi(imsi)
                .lpa(qrCode)
                .packCode(packageCode != null && !packageCode.isEmpty() ? packageCode : esimConfig.getDefaultPackage())
                .activeStatus(NOT_BLOCKING)
                .regDate(LocalDate.now())
                .esimGwId(esimGwId)
                .modifiedBy("SYSTEM")
                .modifiedDate(LocalDateTime.now())
                .createdBy("SYSTEM")
                .createdDate(LocalDateTime.now())
                .build();

            return EsimProcessingResult.builder()
                .successfulResults(List.of(new EsimBookingResult(serial, qrCode, registration)))
                .errorInfos(new ArrayList<>())
                .build();

        } catch (Exception e) {
            String errorKey = "ISDN_" + stockIsdn.getIsdn();
            String errorMessage = "Failed to process eSIM booking: " + e.getMessage();
            errors.put(errorKey, errorMessage);
            log.error("Error processing eSIM booking for ISDN {}: {}", stockIsdn.getIsdn(), e.getMessage(), e);

            int errorStatus = determineErrorStatus(e);
            EsimErrorInfo errorInfo = EsimErrorInfo.builder()
                .isdn(stockIsdn.getIsdn().toString())
                .errorStatus(errorStatus)
                .errorMessage(errorMessage)
                .registrationLine(registrationLine)
                .build();

            return EsimProcessingResult.builder()
                .successfulResults(new ArrayList<>())
                .errorInfos(List.of(errorInfo))
                .build();
        }
    }

    private int determineErrorStatus(Exception e) {
        String errorMessage = e.getMessage();
        if (errorMessage.contains("Book eSIM from external API")) {
            return EsimBookingConstant.BOOK_ESIM_FAILED;
        } else if (errorMessage.contains("Craft kit for serial")) {
            return EsimBookingConstant.CRAFT_KIT_FAILED;
        } else if (errorMessage.contains("Register package for serial")) {
            return EsimBookingConstant.REG_PACKAGE_FAILED;
        } else {
            return EsimBookingConstant.BOOK_ESIM_FAILED; // Default to booking failed
        }
    }

    private static BookESimMbfResponse.BookEsimInfo getBookEsimInfo(BookESimMbfResponse bookResponse) {
        if (bookResponse == null || bookResponse.getData() == null || bookResponse.getData().isEmpty()) {
            throw new RuntimeException("Failed to book eSIM from external API - invalid response structure");
        }

        BookESimMbfResponse.BookEsimResponseItem responseItem = bookResponse.getData().get(0);
        if (responseItem == null || responseItem.getData() == null) {
            throw new RuntimeException("Failed to book eSIM from external API - invalid response item structure");
        }

        return responseItem.getData();
    }

    private BookESimMbfResponse bookEsimFromExternalAPI() {
        BookEsimMbfRequest request = BookEsimMbfRequest.builder()
            .profileType(esimConfig.getBookEsimProfileType())
            .getSuccess(true)
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD, IntegrationConstant.BOOK_ESIM_TYPE, null, request);

        return integrationPort.executeRequest(integrationRequest, BookESimMbfResponse.class);
    }

    private void craftKitForSerial(String serial) {
        CraftKitMbfRequest request = CraftKitMbfRequest.builder()
            .serial(serial)
            .isdn(String.valueOf(TEST_ISDN))
            .bhm(esimConfig.getDefaultPackage())
            .user(esimConfig.getUserMobifone())
            .getSuccess(true)
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD, IntegrationConstant.CRAFT_KIT_TYPE, null, request);

        CraftKitMbfResponse response = integrationPort.executeRequest(integrationRequest, CraftKitMbfResponse.class);

        if (response == null || response.getData() == null || response.getData().isEmpty()
            || !"SUCCESS".equals(response.getCode())) {
            throw new RuntimeException("Failed to craft kit for serial: " + serial);
        }
    }

    private void registerPackageForSerial(String serial, String packageCode) {
        RegisterPackageMbfRequest request = RegisterPackageMbfRequest.builder()
            .strMobiSubType(esimConfig.getMobiSubType())
            .strIsdn(String.valueOf(TEST_ISDN))
            .strShopCode(esimConfig.getShopCode())
            .strEmployee(esimConfig.getEmployee())
            .strReasonCode(esimConfig.getReasonCode())
            .qlkhUsername(esimConfig.getQlkhUsername())
            .qlkhPassword(esimConfig.getQlkhPassword())
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD, IntegrationConstant.REGISTER_PACKAGE_TYPE, null, request);

        RegisterPackageMbfResponse response = integrationPort.executeRequest(integrationRequest, RegisterPackageMbfResponse.class);

        if (response == null) {
            String errorMessage = "Failed to register package " + packageCode + " for serial: " + serial + ". Response is null";
            RegisterPackageErrorHandler.logError("NULL_RESPONSE", serial, packageCode, errorMessage);
            throw new RuntimeException(errorMessage);
        }

        String responseCode = response.getCode();
        String responseDescription = response.getDescription();

        log.info("Register package response for serial: {}, package: {}, code: {}, description: {}",
                serial, packageCode, responseCode, responseDescription);

        if (RegisterPackageErrorHandler.isSuccess(responseCode)) {
            log.info("Successfully registered package {} for serial {}", packageCode, serial);
            return;
        }

        String errorMessage = RegisterPackageErrorHandler.getErrorMessage(responseCode, packageCode);

        RegisterPackageErrorHandler.logError(responseCode, serial, packageCode, responseDescription);

        if (RegisterPackageErrorHandler.isRetryableError(responseCode)) {
            log.warn("Retryable error occurred for serial: {}, package: {}, code: {}. Will retry.",
                    serial, packageCode, responseCode);
            throw new RuntimeException("Retryable error: " + errorMessage);
        }

        switch (responseCode) {
            case IntegrationConstant.PACKAGE_ALREADY_REGISTERED_CODE:
                log.warn("Package {} already registered for serial {}", packageCode, serial);
                return;

            case IntegrationConstant.SUBSCRIBER_NOT_FOUND_CODE:
            case IntegrationConstant.SUBSCRIBER_INACTIVE_CODE:
                throw new RuntimeException("Subscriber error: " + errorMessage);

            case IntegrationConstant.PACKAGE_NOT_EXIST_CODE:
                throw new RuntimeException("Package not found: " + errorMessage);

            case IntegrationConstant.INVALID_MOBI_SUB_TYPE_CODE:
            case IntegrationConstant.MISSING_PACKAGE_CODE:
            case IntegrationConstant.MISSING_SUBSCRIBER_TYPE_CODE:
            case IntegrationConstant.MISSING_SUBSCRIBER_NUMBER_CODE:
            case IntegrationConstant.MISSING_SHOP_TYPE_CODE:
            case IntegrationConstant.MISSING_EMPLOYEE_CODE:
            case IntegrationConstant.MISSING_REASON_CODE:
                throw new RuntimeException("Configuration error: " + errorMessage);

            case IntegrationConstant.INVALID_NUMBER_FORMAT_CODE:
            case IntegrationConstant.INVALID_IMEI_CODE:
                throw new RuntimeException("Invalid format error: " + errorMessage);

            default:
                throw new RuntimeException("Registration failed: " + errorMessage);
        }
    }
}


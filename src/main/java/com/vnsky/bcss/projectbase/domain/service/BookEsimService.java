package com.vnsky.bcss.projectbase.domain.service;

import com.github.f4b6a3.ulid.UlidCreator;
import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.dto.BookEsimDetailLineItemDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.BookEsimServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.*;
import com.vnsky.bcss.projectbase.domain.port.primary.SubscriberServicePort;
import com.vnsky.bcss.projectbase.domain.mapper.VerifyInfoParamsMapper;
import com.vnsky.bcss.projectbase.infrastructure.data.ListBookEsimResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.BookEsimRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.external.BookEsimMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.external.CraftKitMbfRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.BaseIntegrationRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.BookESimMbfResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.external.CraftKitMbfResponse;
import com.vnsky.bcss.projectbase.domain.port.secondary.external.IntegrationPort;
import com.vnsky.bcss.projectbase.shared.config.EsimConfig;
import com.vnsky.bcss.projectbase.shared.constant.IntegrationConstant;
import com.vnsky.bcss.projectbase.shared.constant.SaleOrderConstant;
import com.vnsky.bcss.projectbase.shared.utils.XlsxUtils;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ActionHistoryActionCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.excel.dto.ExcelData;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.time.LocalDate;
import java.math.BigDecimal;

import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.BookEsimDetailResponse;
import java.util.function.Supplier;

import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.ModifyInfoCorpRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.ModifyInfoCorpResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.request.active.subscriber.SubmitDecree13Request;
import com.vnsky.bcss.projectbase.infrastructure.data.response.active.subscriber.SubmitDecree13Response;
import com.vnsky.bcss.projectbase.domain.dto.VerifyInfoParams;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import java.util.Objects;
import org.springframework.util.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookEsimService implements BookEsimServicePort {

    private final IntegrationPort integrationPort;
    private final StockIsdnRepoPort stockIsdnRepoPort;
    private final OrganizationUserRepoPort organizationUserRepoPort;
    private final SaleOrderRepoPort saleOrderRepoPort;
    private final EsimRegistrationRepoPort esimRegistrationRepoPort;
    private final EsimRegistrationLineRepoPort esimRegistrationLineRepoPort;
    private final SaleOrderLineRepoPort saleOrderLineRepoPort;
    private final PackageProfileRepoPort packageProfileRepoPort;
    private final ActionHistoryRepoPort actionHistoryRepoPort;
    private final SubscriberRepoPort subscriberRepoPort;
    private final EsimConfig esimConfig;
    private final BookEsimRequestRepoPort bookEsimRequestRepoPort;
    private final SubscriberServicePort subscriberServicePort;
    private final ApplicationConfigRepoPort applicationConfigRepoPort;
    private final VerifyInfoParamsMapper verifyInfoParamsMapper;
    private final ApplicationContext applicationContext;
    private final TaskExecutor taskExecutor;
    private final MinioOperations minioOperations;

    private static final int BOOKED_STATUS = 4;
    private static final int NOT_BLOCKING = 1;
    private static final int NOT_ACTIVE = 0;
    private static final int NOT_BOUGHT = 0;
    private static final int NOT_VERIFIED = 0;
    private static final int NOT_CALL = 0;
    private static final int TEMPORY_PAYMENT = 0;
    private static final String SYSTEM_USER = "SYSTEM";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_ERROR = "ERROR";
    private static final String SUB_DOCUMENT_CONFIG_TYPE = "SUB_DOCUMENT_ND13";
    private static final String PREFIX_MESSAGE = " for ISDN: ";
    private final OrganizationUnitServicePort organizationUnitServicePort;

    private OrganizationUserDTO getCurrentOrganizationUserOrThrow(String currentUserId) {
        if (currentUserId == null) {
            throw BaseException.badRequest(ErrorCode.INVALID_USER).build();
        }
        return organizationUserRepoPort.findByUserId(currentUserId)
            .orElseThrow(() -> BaseException.badRequest(ErrorCode.INVALID_USER).build());
    }

    private void validatePackageCodeExistsIfProvided(String packageCode) {
        if (packageCode == null || packageCode.isEmpty()) {
            return;
        }

        Optional<PackageProfileDTO> packageProfile = packageProfileRepoPort.findByPckCode(packageCode);
        if (packageProfile.isEmpty()) {
            log.error("Package code not found in database: {}", packageCode);
            throw BaseException.badRequest(ErrorCode.INVALID_PACKAGE_CODE).build();
        }
    }

    private List<EsimRegistrationLineDTO> buildRegistrationLines(List<StockIsdnDTO> isdns, String esimRegistrationId, String packageCode) {
        List<EsimRegistrationLineDTO> lines = isdns.stream()
            .<EsimRegistrationLineDTO>map(stockIsdn -> EsimRegistrationLineDTO.builder()
                .isdn(stockIsdn.getIsdn())
                .esimRegistrationId(esimRegistrationId)
                .status(null)
                .pckCode(packageCode)
                .createdBy(SYSTEM_USER)
                .createdDate(LocalDateTime.now())
                .build())
            .toList();

        BookEsimService selfTx = applicationContext.getBean(BookEsimService.class);
        return selfTx.saveRegistrationLinesNewTx(lines);
    }

    private void updateRegistrationLineSuccess(EsimRegistrationLineDTO original, EsimBookingResult result, String createdByUsername) {
        Optional<EsimRegistrationLineDTO> existingOpt = esimRegistrationLineRepoPort.findById(original.getId());
        if (existingOpt.isPresent()) {
            EsimRegistrationLineDTO existing = existingOpt.get();
            existing.setStatus(SaleOrderConstant.BOOK_ESIM_SUCCESS);
            existing.setSerial(result.getSerial());
            existing.setImsi(Long.parseLong(result.getRegistration().getSerial()));
            existing.setLpa(result.getQrCode());
            existing.setPckCode(result.getRegistration().getPackCode());
            existing.setModifiedBy(createdByUsername);
            existing.setModifiedDate(LocalDateTime.now());
            esimRegistrationLineRepoPort.saveAndFlush(existing);
        }

        subscriberRepoPort.saveAndFlush(result.getRegistration());

        log.info("Successfully saved subscriber to database for serial: {}, ISDN: {}",
            result.getSerial(), result.getRegistration().getIsdn());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveRegistrationLineAndSubscriberNewTx(EsimRegistrationLineDTO original, EsimBookingResult result, String createdByUsername) {
        updateRegistrationLineSuccess(original, result, createdByUsername);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateRegistrationLineBookedNewTx(EsimRegistrationLineDTO registrationLine, String serial, Long imsi, String lpa, String packageCode) {
        Optional<EsimRegistrationLineDTO> existingOpt = esimRegistrationLineRepoPort.findById(registrationLine.getId());
        if (existingOpt.isPresent()) {
            EsimRegistrationLineDTO existing = existingOpt.get();
            existing.setSerial(serial);
            existing.setStatus(SaleOrderConstant.CRAFT_KIT_FAILED);
            existing.setImsi(imsi);
            existing.setLpa(lpa);
            existing.setPckCode(packageCode);
            existing.setModifiedBy(SYSTEM_USER);
            existing.setModifiedDate(LocalDateTime.now());
            esimRegistrationLineRepoPort.saveAndFlush(existing);
        }
    }

    private void updateRegistrationLineError(EsimErrorInfo errorInfo) {
        if (errorInfo.getRegistrationLine() != null) {
            String id = errorInfo.getRegistrationLine().getId();
            Optional<EsimRegistrationLineDTO> existingOpt = esimRegistrationLineRepoPort.findById(id);
            if (existingOpt.isPresent()) {
                EsimRegistrationLineDTO existing = existingOpt.get();
                existing.setStatus(errorInfo.getErrorStatus());
                existing.setModifiedBy(SYSTEM_USER);
                existing.setModifiedDate(LocalDateTime.now());
                esimRegistrationLineRepoPort.saveAndFlush(existing);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorRegistrationLineNewTx(EsimErrorInfo errorInfo) {
        updateRegistrationLineError(errorInfo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<StockIsdnDTO> reserveStockIsdnsForRequests(List<BookEsimRequest> requests) {
        // Calculate total quantity needed
        int totalQuantity = requests.stream()
            .mapToInt(BookEsimRequest::getQuantity)
            .sum();

        // Find and reserve all ISDNs at once
        List<StockIsdnDTO> availableIsdns = stockIsdnRepoPort.findAvailableIsdns(totalQuantity);

        // Reserve ISDNs immediately
        List<StockIsdnDTO> reservedIsdns = availableIsdns.stream()
            .<StockIsdnDTO>map(stockIsdn -> StockIsdnDTO.builder()
                .id(stockIsdn.getId())
                .isdn(stockIsdn.getIsdn())
                .serial(stockIsdn.getSerial())
                .imsi(stockIsdn.getImsi())
                .status(BOOKED_STATUS)
                .activeDatetime(LocalDateTime.now())
                .description(stockIsdn.getDescription())
                .modifiedBy(SYSTEM_USER)
                .modifiedDate(LocalDateTime.now())
                .createdBy(stockIsdn.getCreatedBy())
                .createdDate(stockIsdn.getCreatedDate())
                .build())
            .toList();

        // Save reserved ISDNs
        stockIsdnRepoPort.saveAllAndFlush(reservedIsdns);

        log.info("Reserved {} ISDNs for {} requests with total quantity: {}",
            reservedIsdns.size(), requests.size(), totalQuantity);

        return reservedIsdns;
    }

    @Override
    public List<BookEsimResponse> bookEsim(List<BookEsimRequest> requests, String note) {
        log.info("Received eSIM booking request for {} items - validating and scheduling async processing", requests.size());
        BookEsimService self = applicationContext.getBean(BookEsimService.class);

        // Validate upfront and return immediately
        validateBeforeCreatingSaleOrder(requests);

        String currentUserId = SecurityUtil.getCurrentUserId();
        String currentClientId = SecurityUtil.getCurrentClientId();
        String currentUsername = SecurityUtil.getCurrentUsername();
        OrganizationUserDTO organizationUser = getCurrentOrganizationUserOrThrow(currentUserId);
        String organizationId = organizationUser.getOrgId();


        // Check client debit limit before creating sale orders/processing
        Long estimatedDebit = estimateTotalDebit(requests);
        checkOrganizationUnitLimit(estimatedDebit, organizationId);

        // Create initial records synchronously BEFORE returning
        SaleOrderDTO saleOrder = self.createGeneralSaleOrder(organizationUser, requests, note, currentUsername);
        EsimRegistrationDTO esimRegistration = self.createEsimRegistration(saleOrder.getId(), currentUsername);
        self.recordSaleOrderActionHistory(saleOrder.getId(), currentUsername);
        self.saveBookEsimRequest(requests, esimRegistration.getId());

        // Reserve stock ISDNs after creating sale order
        List<StockIsdnDTO> reservedIsdns = self.reserveStockIsdnsForRequests(requests);

        // Schedule heavy per-ISDN processing using TaskExecutor with security context propagation + locale copy
        taskExecutor.execute(new DelegatingSecurityContextRunnable(runWithLocale(() -> {
            try {
                this.processExistingRegistrationInBackground(esimRegistration.getId(), requests, currentClientId, currentUsername, organizationId, reservedIsdns);
            } catch (Exception ex) {
                log.error("Async eSIM booking processing failed: {}", ex.getMessage(), ex);
            }
        })));

        BookEsimResponse ack = BookEsimResponse.builder()
            .serials(Collections.emptyList())
            .qrCodes(Collections.emptyList())
            .status(STATUS_SUCCESS)
            .message("Booking request accepted. Processing asynchronously.")
            .createdDate(LocalDateTime.now())
            .build();

        return Collections.singletonList(ack);
    }

    @Transactional
    public void saveBookEsimRequest(List<BookEsimRequest> requests, String esimRegistrationId) {
        this.bookEsimRequestRepoPort.saveAllAndFlush(requests.stream().map(
            request -> BookEsimRequestDTO.builder()
                .esimRegistrationId(esimRegistrationId)
                .quantity(request.getQuantity().longValue())
                .pckCode(request.getPackageCode())
                .build()).toList());
    }

    public void processExistingRegistrationInBackground(String esimRegistrationId,
                                                        List<BookEsimRequest> requests,
                                                        String currentClientId,
                                                        String createdByUsername,
                                                        String organizationId,
                                                        List<StockIsdnDTO> reservedIsdns) {
        log.info("Starting async eSIM booking process for {} requests (existing registration {})", requests.size(), esimRegistrationId);

        int totalSucceeded = 0;
        int totalFailed = 0;
        BigDecimal totalDebitAmount = BigDecimal.ZERO;

        for (BookEsimRequest request : requests) {
            int successCount = 0;
            try {
                BookEsimService self = applicationContext.getBean(BookEsimService.class);

                // Use the pre-reserved ISDNs directly - they are already reserved and won't be used by other requests
                BookEsimResponse response = self.processSingleEsimRequest(request, esimRegistrationId, currentClientId, createdByUsername, organizationId, reservedIsdns);

                if (STATUS_SUCCESS.equals(response.getStatus())) {
                    successCount = response.getSerials().size();
                    totalSucceeded += successCount;
                    String effectivePackageCode = (request.getPackageCode() == null || request.getPackageCode().isEmpty())
                        ? esimConfig.getDefaultPackage()
                        : request.getPackageCode();

                    Optional<PackageProfileDTO> packageProfile = packageProfileRepoPort.findByPckCode(effectivePackageCode);
                    if (packageProfile.isPresent() && packageProfile.get().getPackagePrice() != null) {
                        Long price = packageProfile.get().getPackagePrice();
                        BigDecimal amountForRequest = BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(successCount));
                        totalDebitAmount = totalDebitAmount.add(amountForRequest);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing eSIM request asynchronously: {}", request, e);
            }
            totalFailed += request.getQuantity() - successCount;
        }

        BookEsimService self = applicationContext.getBean(BookEsimService.class);
        self.updateEsimRegistrationFinalStatus(organizationId, esimRegistrationId, totalSucceeded, totalFailed, totalDebitAmount, createdByUsername);
        log.info("Async eSIM booking finished. Total succeeded: {}, Total failed: {}", totalSucceeded, totalFailed);
    }

    public void validateBeforeCreatingSaleOrder(List<BookEsimRequest> requests) {
        // Validate package codes if provided
        for (BookEsimRequest request : requests) {
            validatePackageCodeExistsIfProvided(request.getPackageCode());
        }

        // Validate there are enough available ISDNs for the total requested quantity
        long totalRequestedQuantity = requests.stream()
            .mapToLong(BookEsimRequest::getQuantity)
            .sum();
        List<StockIsdnDTO> precheckIsdns = stockIsdnRepoPort.findAvailableIsdns((int) totalRequestedQuantity);
        if (precheckIsdns.size() < totalRequestedQuantity) {
            throw BaseException.badRequest(ErrorCode.INSUFFICIENT_ELIGIBLE_SUBSCRIBERS)
                .message("Insufficient ISDNs available. Requested: " + totalRequestedQuantity + ", Found: " + precheckIsdns.size())
                .build();
        }
    }

    public Long estimateTotalDebit(List<BookEsimRequest> requests) {
        try {
            BigDecimal total = BigDecimal.ZERO;
            for (BookEsimRequest request : requests) {
                String effectivePackageCode = (request.getPackageCode() == null || request.getPackageCode().isEmpty())
                    ? esimConfig.getDefaultPackage()
                    : request.getPackageCode();

                if (effectivePackageCode == null || effectivePackageCode.isEmpty()) {
                    continue;
                }

                Optional<PackageProfileDTO> packageProfile = packageProfileRepoPort.findByPckCode(effectivePackageCode);
                if (packageProfile.isPresent() && packageProfile.get().getPackagePrice() != null) {
                    Long price = packageProfile.get().getPackagePrice();
                    BigDecimal amountForRequest = BigDecimal.valueOf(price)
                        .multiply(BigDecimal.valueOf(request.getQuantity()));
                    total = total.add(amountForRequest);
                }
            }
            return total.longValue();
        } catch (Exception e) {
            log.warn("Failed to estimate total debit amount: {}", e.getMessage());
            return 0L;
        }
    }

    public void checkOrganizationUnitLimit(Long debitLimit, String orgId) {
        OrganizationUnitDTO orgCurrent = organizationUnitServicePort.get(orgId);
        Long orgCurrentDebtLimit = Optional.ofNullable(orgCurrent.getDebtLimit()).orElse(0L);
        Long orgCurrentDebtLimitMbf = Optional.ofNullable(orgCurrent.getDebtLimitMbf()).orElse(0L);
        log.debug("OrganizationUnit - debitLimit: {}, debitLimitMbf: {}", orgCurrentDebtLimit, orgCurrentDebtLimitMbf);
        if (debitLimit - orgCurrentDebtLimit > 0) {
            throw BaseException.badRequest(ErrorCode.CLIENT_LIMIT_EXCEEDED).build();
        }
    }

    private void updateOrganizationUnitDebit(String organizationId, Long debitLimit, Long debitLimitMbf) {
        OrganizationUnitDTO organizationUnitDTO = organizationUnitServicePort.get(organizationId);
        organizationUnitDTO.setDebtLimit(organizationUnitDTO.getDebtLimit() - debitLimit);
        organizationUnitDTO.setDebtLimitMbf(organizationUnitDTO.getDebtLimitMbf() - debitLimitMbf);
        this.organizationUnitServicePort.save(organizationUnitDTO, organizationUnitDTO.getId(), false);
    }

    @Override
    public Page<ListBookEsimResponse> searchBookEsimList(Pageable pageable, String toDate, String fromDate, String textSearch) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        OrganizationUserDTO organizationUser = getCurrentOrganizationUserOrThrow(currentUserId);
        return saleOrderRepoPort.getListBookEsimByOrgId(pageable, organizationUser.getOrgId(), toDate, fromDate, textSearch);
    }

    @Override
    public Resource export( String toDate, String fromDate, String textSearch) {
        String currentUserId = SecurityUtil.getCurrentUserId();
        OrganizationUserDTO organizationUser = getCurrentOrganizationUserOrThrow(currentUserId);
        List<ListBookEsimResponse> responses = saleOrderRepoPort.getListBookEsimByOrgIdExport(organizationUser.getOrgId(), toDate, fromDate, textSearch);
        List<ExportListBookEsimDTO> listBookEsimDTOs = new ArrayList<>();
        responses.forEach(response -> {
            String status;
            if (Objects.isNull(response.getStatus())) {
                status = "Đang xử lý";
            }else status = convertStatusOrder(response.getStatus());

            ExportListBookEsimDTO  exportListBookEsimDTO = ExportListBookEsimDTO.builder()
                .orderNo(response.getOrderNo())
                .packageCodes(response.getPackageCodes())
                .amountTotal(response.getAmountTotal())
                .failedNumber(response.getFailedNumber())
                .successedNumber(response.getSuccessedNumber())
                .orderDate(response.getOrderDate())
                .createdBy(response.getCreatedBy())
                .createdDate(response.getCreatedDate())
                .quantity(response.getQuantity())
                .status(status)
                .build();

            listBookEsimDTOs.add(exportListBookEsimDTO);
        });
        return XlsxUtils.writeExcel(new ExcelData<>(new HashMap<>(), listBookEsimDTOs, true), ExportListBookEsimDTO.class, false, null, null);

    }

    @Override
    public BookEsimDetailResponse findById(String id) {
        Optional<SaleOrderDTO> saleOrder = saleOrderRepoPort.findById(id);
        if (saleOrder.isEmpty()) {
            throw BaseException.notFoundError(ErrorCode.SALE_ORDER_NOT_FOUND)
                .message("id")
                .build();
        }

        List<BookEsimDetailLineItemDTO> bookEsimDetailLineItems = esimRegistrationLineRepoPort.findBookEsimDetailLineItemsBySaleOrderId(id);

        List<BookEsimDetailResponse.SaleOrderLineItem> lineItems = bookEsimDetailLineItems.stream()
            .map(item -> BookEsimDetailResponse.SaleOrderLineItem.builder()
                .id(item.getId())
                .pckCode(item.getPckCode())
                .quantity(item.getQuantity())
                .build())
            .toList();

        return BookEsimDetailResponse.builder()
            .saleOrderLines(lineItems)
            .note(saleOrder.get().getNote())
            .build();
    }

    @Override
    public List<IncompleteRegistrationDTO> findIncompleteRegistrations() {
        List<IncompleteRegistrationRowDTO> rows = esimRegistrationRepoPort.findIncompleteRegistrations();

        Map<String, List<BookEsimRequest>> grouped = rows.stream()
            .collect(Collectors.groupingBy(
                IncompleteRegistrationRowDTO::getRegistrationId,
                Collectors.mapping(
                    row -> {
                        BookEsimRequest req = new BookEsimRequest();
                        req.setQuantity(row.getQuantity());
                        req.setPackageCode(row.getPckCode());
                        return req;
                    },
                    Collectors.toList()
                )
            ));

        return grouped.entrySet().stream()
            .map(entry -> new IncompleteRegistrationDTO(entry.getKey(), entry.getValue()))
            .toList();
    }

    public BookEsimResponse processSingleEsimRequest(BookEsimRequest request, String esimRegistrationId, String currentClientId, String createdByUsername, String organizationId, List<StockIsdnDTO> reservedIsdns) {
        log.info("Processing eSIM booking for quantity: {}, package: {}",
            request.getQuantity(), request.getPackageCode());

        // Take only the required quantity from the pre-reserved ISDNs
        List<StockIsdnDTO> availableIsdns = reservedIsdns.stream()
            .limit(request.getQuantity())
            .toList();

        List<EsimRegistrationLineDTO> registrationLines = buildRegistrationLines(availableIsdns, esimRegistrationId, request.getPackageCode());

        Map<String, String> errors = new ConcurrentHashMap<>();
        List<EsimProcessingResult> results = processBookingFutures(request, availableIsdns, registrationLines, errors, currentClientId, organizationId, createdByUsername);

        ProcessingResult processingResult = processResults(results, registrationLines, createdByUsername);

        return buildResponse(processingResult, request, errors);
    }

    private List<EsimProcessingResult> processBookingFutures(BookEsimRequest request, List<StockIsdnDTO> availableIsdns,
                                                             List<EsimRegistrationLineDTO> registrationLines, Map<String, String> errors,
                                                             String currentClientId, String organizationId, String createdByUsername) {
            BookEsimService self = applicationContext.getBean(BookEsimService.class);
            ExecutorService bookingPool = Executors.newFixedThreadPool(esimConfig.getBookEsimThreadPoolSize());
            Executor securityContextExecutor = new DelegatingSecurityContextExecutorService(bookingPool);

            List<CompletableFuture<EsimProcessingResult>> bookingFutures = IntStream.range(0, request.getQuantity())
                .mapToObj(i -> {
                    StockIsdnDTO stockIsdn = availableIsdns.get(i);
                    EsimRegistrationLineDTO registrationLine = registrationLines.get(i);
                    return CompletableFuture.supplyAsync(
                        supplyWithLocale(() -> self.processExternalApiCalls(request.getPackageCode(), stockIsdn, registrationLine, errors, currentClientId, organizationId, createdByUsername)),
                        securityContextExecutor
                    );
                })
                .toList();

        CompletableFuture<Void> allBookings = CompletableFuture.allOf(bookingFutures.toArray(new CompletableFuture[0]));
            List<EsimProcessingResult> results = allBookings.thenApply(v ->
            bookingFutures.stream().map(CompletableFuture::join).toList()
            ).join();

            bookingPool.shutdown();
        return results;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<EsimRegistrationLineDTO> saveRegistrationLinesNewTx(List<EsimRegistrationLineDTO> registrationLines) {
        if (registrationLines == null || registrationLines.isEmpty()) {
            return registrationLines;
        }
        return esimRegistrationLineRepoPort.saveAllAndFlush(registrationLines);
    }

    private ProcessingResult processResults(List<EsimProcessingResult> results, List<EsimRegistrationLineDTO> registrationLines,
                                          String createdByUsername) {
        List<EsimBookingResult> successfulResults = new ArrayList<>();
        List<EsimErrorInfo> errorInfos = new ArrayList<>();

            for (int i = 0; i < results.size(); i++) {
                EsimProcessingResult processingResult = results.get(i);
            processSuccessfulResult(processingResult, registrationLines.get(i), createdByUsername, successfulResults);
            processErrorInfos(processingResult, errorInfos);
        }

        updateErrorRegistrationLines(errorInfos);

        return new ProcessingResult(successfulResults, errorInfos);
    }

    private void processSuccessfulResult(EsimProcessingResult processingResult, EsimRegistrationLineDTO registrationLine,
                                       String createdByUsername, List<EsimBookingResult> successfulResults) {
        if (processingResult.getSuccessfulResults().isEmpty()) {
            return;
        }

                    EsimBookingResult result = processingResult.getSuccessfulResults().get(0);
                    successfulResults.add(result);
        BookEsimService selfTx = applicationContext.getBean(BookEsimService.class);
        selfTx.saveRegistrationLineAndSubscriberNewTx(registrationLine, result, createdByUsername);
        selfTx.saveActionHistoriesNewTx(processingResult.getActionHistory());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveActionHistoriesNewTx(List<ActionHistoryDTO> actionHistories) {
        if (actionHistories == null || actionHistories.isEmpty()) {
            return;
        }
        for (ActionHistoryDTO actionHistory : actionHistories) {
            actionHistoryRepoPort.saveAndFlush(actionHistory);
        }
    }

    private void processErrorInfos(EsimProcessingResult processingResult, List<EsimErrorInfo> errorInfos) {
        if (!processingResult.getErrorInfos().isEmpty()) {
            errorInfos.addAll(processingResult.getErrorInfos());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveStockIsdnsNewTx(List<StockIsdnDTO> stockIsdns) {
        if (stockIsdns == null || stockIsdns.isEmpty()) {
            return;
        }
        stockIsdnRepoPort.saveAllAndFlush(stockIsdns);
    }

    private void updateErrorRegistrationLines(List<EsimErrorInfo> errorInfos) {
            BookEsimService selfTx = applicationContext.getBean(BookEsimService.class);
            for (EsimErrorInfo errorInfo : errorInfos) {
                selfTx.saveErrorRegistrationLineNewTx(errorInfo);
        }
            }

    private BookEsimResponse buildResponse(ProcessingResult processingResult, BookEsimRequest request, Map<String, String> errors) {
        int succeededCount = processingResult.successfulResults().size();
            int failedCount = request.getQuantity() - succeededCount;

            if (!errors.isEmpty()) {
                log.error("Errors occurred during eSIM booking process: {}", errors);
            }

            log.info("Completed eSIM booking for {} quantities. Succeeded: {}, Failed: {}",
                request.getQuantity(), succeededCount, failedCount);

            List<String> serials = processingResult.successfulResults().stream()
                .map(EsimBookingResult::getSerial)
                .toList();

            List<String> qrCodes = processingResult.successfulResults().stream()
                .map(EsimBookingResult::getQrCode)
                .toList();

            String status = succeededCount > 0 ? STATUS_SUCCESS : STATUS_ERROR;
            String message = succeededCount > 0
                ? "eSIM booking completed. Succeeded: " + succeededCount + ", Failed: " + failedCount
                : "eSIM booking failed for all quantities. Errors: " + String.join("; ", errors.values());

            return BookEsimResponse.builder()
                .serials(serials)
                .qrCodes(qrCodes)
                .status(status)
                .message(message)
                .createdDate(LocalDateTime.now())
                .build();
    }

    private record ProcessingResult(List<EsimBookingResult> successfulResults, List<EsimErrorInfo> errorInfos) {}

    public EsimProcessingResult processExternalApiCalls(String packageCode, StockIsdnDTO stockIsdn,
                                                        EsimRegistrationLineDTO registrationLine,
                                                        Map<String, String> errors, String currentClientId,
                                                        String organizationId, String createdByUsername) {
        String createdSubscriberId = null;
        List<ActionHistoryDTO> actionHistories = new ArrayList<>();

        try {
            // Step 1: Book eSIM from external API
            BookESimMbfResponse bookResponse = bookEsimFromExternalAPI();
            BookESimMbfResponse.BookEsimInfo bookInfo = getBookEsimInfo(bookResponse);
            String serial = bookInfo.getSerial();
            String qrCode = bookInfo.getQr();
            Long imsi = Long.parseLong(bookInfo.getImsi());
            String esimGwId = bookInfo.getEsimGwId();

            // Update registration line with book info (serial, lpa, imsi, pckCode) in a separate transaction
            BookEsimService selfTx = applicationContext.getBean(BookEsimService.class);
            selfTx.updateRegistrationLineBookedNewTx(registrationLine, serial, imsi, qrCode, packageCode);

            // Record BOOK_ESIM action history with reasonCode = VIEW
            ActionHistoryDTO bookEsimAction = ActionHistoryDTO.builder()
                .subId(stockIsdn.getIsdn().toString())
                .actionDate(LocalDateTime.now())
                .actionCode(ActionHistoryActionCode.BOOK_ESIM.getCode())
                .description(ActionHistoryActionCode.BOOK_ESIM.getDescription() + PREFIX_MESSAGE + stockIsdn.getIsdn())
                .shopCode(null)
                .empCode(null)
                .empName(null)
                .reasonCode(null)
                .reasonNote("Successfully booked eSIM from external API")
                .createdBy(SYSTEM_USER)
                .createdDate(LocalDateTime.now())
                .build();
            actionHistories.add(bookEsimAction);

            // Step 2: Craft kit for serial
            craftKitForSerial(serial, stockIsdn.getIsdn());

            // Record CRAFT_KIT action history
            ActionHistoryDTO craftKitAction = ActionHistoryDTO.builder()
                .subId(stockIsdn.getIsdn().toString())
                .actionDate(LocalDateTime.now())
                .actionCode(ActionHistoryActionCode.CRAFT_KIT.getCode())
                .description(ActionHistoryActionCode.CRAFT_KIT.getDescription() + " for serial: " + serial)
                .shopCode(null)
                .empCode(null)
                .empName(null)
                .reasonCode(null)
                .reasonNote("Successfully crafted kit for serial: " + serial)
                .createdBy(SYSTEM_USER)
                .createdDate(LocalDateTime.now())
                .build();
            actionHistories.add(craftKitAction);

            SubscriberDTO registration = SubscriberDTO.builder()
                .serial(serial)
                .isdn(stockIsdn.getIsdn())
                .imsi(imsi)
                .lpa(qrCode)
                .packCode(packageCode)
                .activeStatus(NOT_BLOCKING)
                .status(NOT_ACTIVE)
                .verifiedStatus(NOT_VERIFIED)
                .statusCall900(NOT_CALL)
                .boughtStatus(NOT_BOUGHT)
                .regDate(LocalDate.now())
                .esimGwId(esimGwId)
                .clientId(currentClientId)
                .orgId(organizationId)
                .modifiedBy(SYSTEM_USER)
                .modifiedDate(LocalDateTime.now())
                .createdBy(SYSTEM_USER)
                .createdDate(LocalDateTime.now())
                .build();

            registration = subscriberServicePort.saveAndFlushNewTransaction(registration);
            createdSubscriberId = registration.getId();

            // Step 3: Ensure subscriber info synced to MBF after creation
            registerSubscriberInfo(stockIsdn.getIsdn(), serial, imsi);

            // Record VERIFY_INFO action history
            ActionHistoryDTO verifyInfoAction = ActionHistoryDTO.builder()
                .subId(stockIsdn.getIsdn().toString())
                .actionDate(LocalDateTime.now())
                .actionCode(ActionHistoryActionCode.VERIFY_INFO.getCode())
                .description(ActionHistoryActionCode.VERIFY_INFO.getDescription() + PREFIX_MESSAGE + stockIsdn.getIsdn())
                .shopCode(null)
                .empCode(null)
                .empName(null)
                .reasonCode(null)
                .reasonNote("Successfully synced subscriber info with MBF")
                .createdBy(SYSTEM_USER)
                .createdDate(LocalDateTime.now())
                .build();
            actionHistories.add(verifyInfoAction);

            // Create sale order line only after successful registerSubscriberInfo
            String effectivePackageCode = (packageCode == null || packageCode.isEmpty())
                ? esimConfig.getDefaultPackage()
                : packageCode;

            BookEsimService selfTxForLine = applicationContext.getBean(BookEsimService.class);
            selfTxForLine.createSaleOrderLineAfterSuccess(registrationLine.getEsimRegistrationId(), effectivePackageCode, stockIsdn.getIsdn().toString(), createdByUsername);

            ActionHistoryDTO bookEsimActionFinish = ActionHistoryDTO.builder()
                .subId(createdSubscriberId)
                .actionDate(LocalDateTime.now())
                .actionCode(ActionHistoryActionCode.BOOK_ESIM.getCode())
                .description(ActionHistoryActionCode.BOOK_ESIM.getDescription() + PREFIX_MESSAGE + stockIsdn.getIsdn())
                .shopCode(null)
                .empCode(null)
                .empName(null)
                .reasonCode("VIEW")
                .reasonNote("Successfully booked eSIM from external API")
                .createdBy(SYSTEM_USER)
                .createdDate(LocalDateTime.now())
                .build();
            actionHistories.add(bookEsimActionFinish);

            return EsimProcessingResult.builder()
                .successfulResults(List.of(new EsimBookingResult(serial, qrCode, registration)))
                .errorInfos(new ArrayList<>())
                .actionHistory(actionHistories)
                .build();

        } catch (Exception e) {
            String errorKey = "ISDN_" + stockIsdn.getIsdn();
            String errorMessage = "Failed to process eSIM booking: " + e.getMessage();
            errors.put(errorKey, errorMessage);
            log.error("Error processing eSIM booking for ISDN {}: {}", stockIsdn.getIsdn(), e.getMessage(), e);

            int errorStatus = determineErrorStatus(e, registrationLine);
            try {
                if (createdSubscriberId != null) {
                    subscriberRepoPort.delete(createdSubscriberId);
                    log.info("Rolled back subscriber {} for ISDN {} due to processing error", createdSubscriberId, stockIsdn.getIsdn());
                }
            } catch (Exception cleanupEx) {
                log.warn("Failed to cleanup subscriber for ISDN {} after error: {}", stockIsdn.getIsdn(), cleanupEx.getMessage());
            }
            EsimErrorInfo errorInfo = EsimErrorInfo.builder()
                .isdn(stockIsdn.getIsdn().toString())
                .errorStatus(errorStatus)
                .errorMessage(errorMessage)
                .registrationLine(registrationLine)
                .build();

            return EsimProcessingResult.builder()
                .successfulResults(new ArrayList<>())
                .errorInfos(List.of(errorInfo))
                .actionHistory(actionHistories)
                .build();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSaleOrderLineAfterSuccess(String esimRegistrationId, String packageCode, String isdn, String createdByUsername) {
        Optional<EsimRegistrationDTO> registrationOpt = esimRegistrationRepoPort.findById(esimRegistrationId);
        if (registrationOpt.isEmpty()) {
            return;
        }

        String saleOrderId = registrationOpt.get().getOrderId();

        Long price = getPricePackage(packageCode);

        SaleOrderLineDTO saleOrderLine = SaleOrderLineDTO.builder()
            .saleOrderId(saleOrderId)
            .price(price)
            .pckCode(packageCode)
            .quantity(1L)
            .payStatus(TEMPORY_PAYMENT)
            .isdn(isdn)
            .modifiedBy(createdByUsername)
            .modifiedDate(LocalDateTime.now())
            .createdBy(createdByUsername)
            .createdDate(LocalDateTime.now())
            .build();

        saleOrderLineRepoPort.saveAndFlush(saleOrderLine);
        log.info("Created sale order line after success registration for saleOrderId: {}, package: {}", saleOrderId, packageCode);
    }

    private Long getPricePackage(String packageCode) {
        Long price = null;
        try {
            Optional<PackageProfileDTO> packageProfile = packageProfileRepoPort.findByPckCode(packageCode);
            price = packageProfile.map(PackageProfileDTO::getPackagePrice).orElse(null);
        } catch (Exception ex) {
            log.warn("Failed to fetch package price for code {}: {}", packageCode, ex.getMessage());
        }
        return price;
    }

    private void registerSubscriberInfo(Long isdn, String serial, Long imsi) {
        // Get verify info params that contain all fields except isdn, serial, and imsi
        VerifyInfoParams params = getVerifyInfoParams();

        // Load default document URLs from application_config
        String contractUrl = null;
        String passportUrl = null;
        String portraitUrl = null;
        String signatureUrl = null;
        try {
            contractUrl = applicationConfigRepoPort.getByTableNameAndColumnName(SUB_DOCUMENT_CONFIG_TYPE, "CONTRACT_URL").getName();
            passportUrl = applicationConfigRepoPort.getByTableNameAndColumnName(SUB_DOCUMENT_CONFIG_TYPE, "PASSPORT_URL").getName();
            portraitUrl = applicationConfigRepoPort.getByTableNameAndColumnName(SUB_DOCUMENT_CONFIG_TYPE, "PORTRAIT_URL").getName();
            signatureUrl = applicationConfigRepoPort.getByTableNameAndColumnName(SUB_DOCUMENT_CONFIG_TYPE, "SIGNATURE_URL").getName();
        } catch (Exception ex) {
            log.warn("Could not load default document URLs from application_config: {}", ex.getMessage());
        }

        AgreeDecree13DTO agreeDecree13DTO = AgreeDecree13DTO.builder()
            .agreeDk1(true)
            .agreeDk2(true)
            .agreeDk3(true)
            .agreeDk4(true)
            .agreeDk5(true)
            .build();

        BookEsimImageDataDTO imageData = BookEsimImageDataDTO.builder()
            .isdn(isdn)
            .agreeDegree13(agreeDecree13DTO)
            .transactionId(UUID.randomUUID().toString())
            .cccdFrontUrl(passportUrl)      // Maps to cccd_front (type = 0)
            .cccdBackUrl(signatureUrl)      // Maps to cccd_back (type = 0)
            .portraitUrl(portraitUrl)       // Maps to portrait (type = 1)
            .contractUrl(contractUrl)       // Maps to contract (type = 2)
            .contractCode(UlidCreator.getUlid().toString())
            .build();

        // Build arrImages for MBF update using our custom function with the new DTO
        List<List<String>> arrImages = buildArrImagesForBookEsim(imageData);

        // Create ModifyInfoCorpRequest with all fields from params plus isdn, serial, and imsi
        ModifyInfoCorpRequest modifyInfoCorpRequest = ModifyInfoCorpRequest.builder()
            // Fields from parameters (not from params)
            .strIsdn(isdn.toString())
            .strSerial(serial)
            .strImsi(imsi.toString())

            // All other fields from params
            .strSex(params.getStrSex())
            .strNationality(params.getStrNationality())
            .strSubName(params.getStrSubName())
            .strIdNo(params.getStrIdNo())
            .strIdIssueDate(params.getStrIdIssueDate())
            .strIdIssuePlace(params.getStrIdIssuePlace())
            .strBirthday(params.getStrBirthday())
            .strProvince(params.getStrProvince())
            .strDistrict(params.getStrDistrict())
            .strPrecinct(params.getStrPrecinct())
            .strHome(params.getStrHome())
            .strAddress(params.getStrAddress())
            .strRegType(params.getStrRegType())
            .strSubType(params.getStrSubType())
            .strKitType(params.getStrKitType())
            .strCustType(params.getStrCustType())
            .strReasonCode(params.getStrReasonCode())
            .strContractNo(params.getStrContractNo())
            .strAppObject(params.getStrAppObject())
            .strSignDate(params.getStrSignDate())
            .strMobiType(params.getStrMobiType())
            .strUserSubName(params.getStrUserSubName())
            .strUserBirthday(params.getStrUserBirthday())
            .strUserSex(params.getStrUserSex())
            .strUserOption(params.getStrUserOption())
            .strUserIdOrPpNo(params.getStrUserIdOrPpNo())
            .strUserIdOrPpIssueDate(params.getStrUserIdOrPpIssueDate())
            .strUserIdOrPpIssuePlace(params.getStrUserIdOrPpIssuePlace())
            .strUserProvince(params.getStrUserProvince())
            .strUserDistrict(params.getStrUserDistrict())
            .strUserPrecinct(params.getStrUserPrecinct())
            .strUserStreetBlockName(params.getStrUserStreetBlockName())
            .strUserStreetName(params.getStrUserStreetName())
            .strUserHome(params.getStrUserHome())
            .strRegBussiness(params.getStrRegBussiness())
            .strFoundedPermNo(params.getStrFoundedPermNo())
            .strContactAddress(params.getStrContactAddress())
            .strBusPermitNo(params.getStrBusPermitNo())
            .strContactName(params.getStrContactName())
            .strUserNationality(params.getStrUserNationality())
            .strFoundedPermDate(params.getStrFoundedPermDate())
            .strTin(params.getStrTin())
            .strTel(params.getStrTel())
            .strOption(params.getStrOption())
            .strLanguage(1)

            // Images array
            .arrImages(arrImages)
            .build();

        // Use the corporate method directly with BookEsimImageDataDTO
        BookEsimService self = applicationContext.getBean(BookEsimService.class);
        self.processSendCorpInfoToMbf(modifyInfoCorpRequest, imageData);
    }

    private Runnable runWithLocale(Runnable runnable) {
        Locale captured = LocaleContextHolder.getLocale();
        return () -> {
            Locale previous = LocaleContextHolder.getLocale();
            try {
                LocaleContextHolder.setLocale(captured);
                runnable.run();
            } finally {
                LocaleContextHolder.setLocale(previous);
            }
        };
    }

    private <T> Supplier<T> supplyWithLocale(Supplier<T> supplier) {
        Locale captured = LocaleContextHolder.getLocale();
        return () -> {
            Locale previous = LocaleContextHolder.getLocale();
            try {
                LocaleContextHolder.setLocale(captured);
                return supplier.get();
            } finally {
                LocaleContextHolder.setLocale(previous);
            }
        };
    }

    private VerifyInfoParams getVerifyInfoParams() {
        List<ApplicationConfigDTO> configs = applicationConfigRepoPort.getByType("VERIFY_INFO_SUBSCRIBER");

        return verifyInfoParamsMapper.mapToVerifyInfoParams(configs);
    }



    private int determineErrorStatus(Exception e, EsimRegistrationLineDTO registrationLine) {
        // Check if BOOK_ESIM failed by checking if serial is null in registration line
        if (registrationLine != null && registrationLine.getSerial() == null) {
            log.info("BOOK_ESIM failed - serial is null in registration line for ISDN: {}", registrationLine.getIsdn());
            return SaleOrderConstant.BOOK_ESIM_FAILED;
        }

        String errorMessage = e.getMessage() == null ? "" : e.getMessage();

        // Check if VERIFY_INFO (step 3) failed by looking at stack trace
        for (StackTraceElement element : e.getStackTrace()) {
            if ("registerSubscriberInfo".equals(element.getMethodName())) {
                log.info("VERIFY_INFO failed - registerSubscriberInfo method in stack trace for ISDN: {}", registrationLine);
                return SaleOrderConstant.REG_INFO_FAILED;
            }
        }

        // Check if CRAFT_KIT (step 2) failed by error message
        if (errorMessage.contains("craft kit for serial")) {
            log.info("CRAFT_KIT failed - craft kit error message detected for ISDN: {}", registrationLine);
            return SaleOrderConstant.CRAFT_KIT_FAILED;
        }

        // If serial is not null, then BOOK_ESIM succeeded, so this must be a later step failure
        // Default to CRAFT_KIT_FAILED since BOOK_ESIM succeeded (serial is not null)
        log.info("Assuming CRAFT_KIT failed - BOOK_ESIM succeeded (serial not null) for ISDN: {}", registrationLine);
        return SaleOrderConstant.CRAFT_KIT_FAILED;
    }

    private static BookESimMbfResponse.BookEsimInfo getBookEsimInfo(BookESimMbfResponse bookResponse) {
        if (bookResponse == null || bookResponse.getData() == null || bookResponse.getData().isEmpty()) {
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Failed to book eSIM from external API - invalid response structure")
                .build();
        }

        BookESimMbfResponse.BookEsimResponseItem responseItem = bookResponse.getData().get(0);
        if (responseItem == null || responseItem.getData() == null) {
            throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                .message("Failed to book eSIM from external API - invalid response item structure")
                .build();
        }

        return responseItem.getData();
    }

    private BookESimMbfResponse bookEsimFromExternalAPI() {
        BookEsimMbfRequest request = BookEsimMbfRequest.builder()
            .profileType(esimConfig.getBookEsimProfileType())
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD, IntegrationConstant.BOOK_ESIM_TYPE, null, request);

        try {
            return integrationPort.executeRequestWithRetryAndErrorHandling(integrationRequest, BookESimMbfResponse.class);
        } catch (BaseException e) {
            // Xử lý error code cụ thể cho BOOK_ESIM
            // BaseException từ IntegrationAdapter sẽ có message chứa thông tin lỗi MBF
            throw BaseException.internalServerError(ErrorCode.MBF_BOOK_ESIM_ERROR)
                .message(e.getMessage())
                .build();
        }
    }

    private void craftKitForSerial(String serial, Long isdn) {
        CraftKitMbfRequest request = CraftKitMbfRequest.builder()
            .serial(serial)
            .isdn(isdn.toString())
            .bhm(esimConfig.getBhm())
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD, IntegrationConstant.CRAFT_KIT_TYPE, null, request);

        try {
            CraftKitMbfResponse response = integrationPort.executeRequestWithRetry(integrationRequest, CraftKitMbfResponse.class);

            // Response đã được validate trong executeRequestWithRetryAndErrorHandling
            // Chỉ cần kiểm tra null và data structure
            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                throw BaseException.internalServerError(ErrorCode.INTERNAL_SERVER_ERROR)
                    .message("Failed to craft kit for serial: " + serial)
                    .build();
            }
        } catch (BaseException e) {
            // Xử lý error code cụ thể cho CRAFT_KIT
            // BaseException từ IntegrationAdapter sẽ có message chứa thông tin lỗi MBF
            throw BaseException.internalServerError(ErrorCode.MBF_CRAFT_KIT_ERROR)
                .message(e.getMessage())
                .build();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SaleOrderDTO createGeneralSaleOrder(OrganizationUserDTO organizationUser, List<BookEsimRequest> requests, String note, String createdByUsername) {
        Long totalQuantity = requests.stream()
            .mapToLong(BookEsimRequest::getQuantity)
            .sum();

        SaleOrderDTO saleOrder = SaleOrderDTO.builder()
            .orgId(organizationUser.getOrgId())
            .amountTotal(0L)
            .quantity(totalQuantity)
            .orderType(SaleOrderConstant.BOOK_ESIM)
            .orderDate(LocalDateTime.now())
            .note(note)
            .modifiedBy(createdByUsername)
            .modifiedDate(LocalDateTime.now())
            .createdBy(createdByUsername)
            .createdDate(LocalDateTime.now())
            .build();

        SaleOrderDTO savedSaleOrder = saleOrderRepoPort.saveAndFlush(saleOrder);

        log.info("Created sale order number: {}, note: {}", savedSaleOrder.getOrderNo(), savedSaleOrder.getNote());

        return savedSaleOrder;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public EsimRegistrationDTO createEsimRegistration(String saleOrderId, String createdByUsername) {
        EsimRegistrationDTO esimRegistration = EsimRegistrationDTO.builder()
            .orderId(saleOrderId)
            .bookEsimStatus(SaleOrderConstant.PROCESSING)
            .build();

        esimRegistration.setCreatedBy(createdByUsername);
        esimRegistration.setCreatedDate(LocalDateTime.now());
        esimRegistration.setModifiedBy(createdByUsername);
        esimRegistration.setModifiedDate(LocalDateTime.now());

        return esimRegistrationRepoPort.saveAndFlush(esimRegistration);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSaleOrderActionHistory(String saleOrderId, String createdByUsername) {
        ActionHistoryDTO actionHistory = ActionHistoryDTO.builder()
            .subId(saleOrderId)
            .actionDate(LocalDateTime.now())
            .actionCode(ActionHistoryActionCode.CREATE_SALE_ORDER.getCode())
            .description(ActionHistoryActionCode.CREATE_SALE_ORDER.getDescription())
            .shopCode(null)
            .empCode(null)
            .empName(null)
            .reasonCode(null)
            .reasonNote(null)
            .createdBy(createdByUsername)
            .createdDate(LocalDateTime.now())
            .build();

        actionHistoryRepoPort.saveAndFlush(actionHistory);
        log.info("Recorded action history for sale order: {}", saleOrderId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateEsimRegistrationFinalStatus(String organizationId, String esimRegistrationId, int totalSucceeded, int totalFailed, BigDecimal totalDebitAmount, String modifiedByUsername) {
        Optional<EsimRegistrationDTO> currentRegistration = esimRegistrationRepoPort.findById(esimRegistrationId);
        if (currentRegistration.isPresent()) {
            EsimRegistrationDTO current = currentRegistration.get();
            int baseSuccess = current.getSuccessedNumber() == null ? 0 : current.getSuccessedNumber();
            int baseFailed = current.getFailedNumber() == null ? 0 : current.getFailedNumber();
            EsimRegistrationDTO updated = EsimRegistrationDTO.builder()
                .id(current.getId())
                .orderId(current.getOrderId())
                .finishedDate(LocalDateTime.now())
                .successedNumber(baseSuccess + totalSucceeded)
                .failedNumber(baseFailed + totalFailed)
                .bookEsimStatus(SaleOrderConstant.DONE)
                .build();
            updated.setModifiedBy(modifiedByUsername);
            updated.setModifiedDate(LocalDateTime.now());
            esimRegistrationRepoPort.saveAndFlush(updated);

            updateSaleOrderAmountTotalFromSuccess(updated.getOrderId(), totalDebitAmount, modifiedByUsername);
            // Cập nhật lại công nợ tạm tính
            this.updateOrganizationUnitDebit(organizationId, totalDebitAmount.longValue(), 0L);
        }
    }

    private void updateSaleOrderAmountTotalFromSuccess(String saleOrderId, BigDecimal totalDebitAmount, String modifiedByUsername) {
        Optional<SaleOrderDTO> currentSaleOrder = saleOrderRepoPort.findById(saleOrderId);
        if (currentSaleOrder.isPresent()) {
            SaleOrderDTO current = currentSaleOrder.get();

            SaleOrderDTO updated = SaleOrderDTO.builder()
                .id(current.getId())
                .orgId(current.getOrgId())
                .amountTotal(totalDebitAmount.longValue())
                .quantity(current.getQuantity())
                .orderType(current.getOrderType())
                .orderDate(current.getOrderDate())
                .orderNo(current.getOrderNo())
                .note(current.getNote())
                .modifiedBy(modifiedByUsername)
                .modifiedDate(LocalDateTime.now())
                .createdBy(current.getCreatedBy())
                .createdDate(current.getCreatedDate())
                .build();

            saleOrderRepoPort.saveAndFlush(updated);
            log.info("Updated sale order {} amountTotal to: {}", saleOrderId, totalDebitAmount);
        }
    }

    private String convertStatusOrder(Integer statusOrder){
        if(statusOrder == 1) return "Đang xử lý";
        else return "Hoàn thành";
    }

    /**
     * Process sending corporate subscriber information to MBF for verification
     * Same logic as processSendActiveDataToMbf but for corporate subscribers
     *
     * @param modifyInfoCorpRequest: Corporate subscriber information request
     * @param imageData: Active subscriber data containing file URLs
     * @apiNote This function sends corporate subscriber info to MBF with cmd = MBF, type = VERIFY_SUBSCRIBER_INFO
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(retryFor = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 100))
    public void processSendCorpInfoToMbf(ModifyInfoCorpRequest modifyInfoCorpRequest, BookEsimImageDataDTO imageData) {
        log.info("[BOOK_ESIM]: Get active result for serial {}, isdn {}", modifyInfoCorpRequest.getStrSerial(), modifyInfoCorpRequest.getStrIsdn());

        //Step 1: Send request to update information
        log.info("[BOOK_ESIM]: Data for modify info MBF: {}", modifyInfoCorpRequest);

        BaseIntegrationRequest verifyInfoRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD,
            IntegrationConstant.VERIFY_SUBSCRIBER_INFO,
            null,
            modifyInfoCorpRequest
        );

        ModifyInfoCorpResponse verifyInfoResponse = integrationPort.executeRequest(verifyInfoRequest, ModifyInfoCorpResponse.class);

        if(Objects.equals(verifyInfoResponse.getCode(), IntegrationConstant.SUCCESS_CODE)){
            log.info("[BOOK_ESIM]: Active subscriber successfully for isdn {}, serial {}", modifyInfoCorpRequest.getStrIsdn(), modifyInfoCorpRequest.getStrSerial());

            //Step 2: Send decree 13 agreement request
            BookEsimService self = applicationContext.getBean(BookEsimService.class);
            self.submitAgreeDecree13(imageData.getAgreeDegree13(), modifyInfoCorpRequest.getStrIsdn(), imageData.getContractCode(), verifyInfoResponse.getData().get(0).getStrSubId());

            //Update status to successfully synchronized information
            SubscriberDTO esimRegistration = subscriberRepoPort.findByLastIsdn(imageData.getIsdn())
                .orElseThrow(() -> BaseException.notFoundError(ErrorCode.ISDN_NOT_FOUND).build());

            esimRegistration.setMbfSubId(verifyInfoResponse.getData().get(0).getStrSubId());
            esimRegistration.setStatus(Status.ACTIVE.getValue());
            subscriberServicePort.saveAndFlushNewTransaction(esimRegistration);

            log.info("[BOOK_ESIM]: Successfully processed corporate subscriber info for ISDN: {}, Serial: {}",
                modifyInfoCorpRequest.getStrIsdn(), modifyInfoCorpRequest.getStrSerial());
        } else {
            throw BaseException.badRequest(ErrorCode.ATTEMPT_GET_ACTIVE_RESULT_EXCEED_MAX_TIMES)
                .message(verifyInfoResponse.getDescription())
                .build();
        }
    }

    /**
     * Submit decree 13 agreement for corporate subscribers
     * Same logic as in ActiveSubscriberService
     *
     * @param agreeDecree13: Decree 13 agreement data
     * @param isdn:          ISDN number
     * @param contractId:    Contract ID
     * @param subId:         Subscriber ID from MBF
     */
    public void submitAgreeDecree13(AgreeDecree13DTO agreeDecree13, String isdn, String contractId, String subId) {
        // "CT01:1;CT02:1;CT03:1;CT04:1;CT05:1;CT06:1;"
        String tc1 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk1());
        String tc2 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk2());
        String tc3 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk3());
        String tc4 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk4());
        String tc5 = buildAcceptedValueDecree13(agreeDecree13.isAgreeDk5());

        SubmitDecree13Request submitRequest = SubmitDecree13Request.builder()
            .isdn(isdn)
            .subId(subId)
            .contractId(contractId)
            .tc1(tc1)
            .tc2(tc2)
            .tc3(tc3)
            .tc4(tc4)
            .tc5(tc5)
            .noteDesc("DGL")
            .build();

        BaseIntegrationRequest integrationRequest = integrationPort.buildIntegrationRequest(
            IntegrationConstant.MBF_CMD,
            "DECREE13",
            null,
            submitRequest
        );

        SubmitDecree13Response response = integrationPort.executeRequest(integrationRequest, SubmitDecree13Response.class);

        if(!Objects.equals(response.getCode(), STATUS_SUCCESS)){
            log.error("[BOOK_ESIM]: Submit decree 13 fail for isdn {} by {}", isdn, response.getDescription());
            throw BaseException.badRequest(ErrorCode.SUBMIT_DECREE13_FAIL)
                .message("Lỗi khi xác nhận nghị định 13 do "+ response.getDescription())
                .build();
            } else {
            log.info("[BOOK_ESIM]: Submit decree 13 successfully for isdn {}", isdn);
        }

    }

    public String buildAcceptedValueDecree13(Boolean agree){
        return Objects.equals(Boolean.TRUE, agree) ? "1" : "0";
    }

    /**
     * Build arrImages for BookEsim with specific image types:
     * - cccd_front with type = 0
     * - cccd_back with type = 0
     * - portrait with type = 1
     * - contract with type = 2
     *
     * @param imageData: Contains image URLs for building the array
     * @return List of image arrays with proper types for MBF
     */
    private List<List<String>> buildArrImagesForBookEsim(BookEsimImageDataDTO imageData) {
        log.info("[BOOK_ESIM]: Building arrImages for BookEsim with transactionId: {}", imageData.getTransactionId());

        // Download files from URLs using the correct field mappings
        Resource cccdFrontFile = downloadFile(imageData.getCccdFrontUrl()); // cccd_front (type = 0)
        Resource cccdBackFile = downloadFile(imageData.getCccdBackUrl());   // cccd_back (type = 0)
        Resource portraitFile = downloadFile(imageData.getPortraitUrl());   // portrait (type = 1)
        Resource contractFile = downloadFile(imageData.getContractUrl());   // contract (type = 2)

        // Build cccd_front array with type = 0
        List<String> cccdFrontArr = List.of(
            StringUtils.hasText(cccdFrontFile.getFilename()) ? cccdFrontFile.getFilename() : "cccd_front.jpg",
            buildBase64Image(cccdFrontFile),
            "0" // type = 0 for cccd_front
        );

        // Build cccd_back array with type = 0
        List<String> cccdBackArr = List.of(
            StringUtils.hasText(cccdBackFile.getFilename()) ? cccdBackFile.getFilename() : "cccd_back.jpg",
            buildBase64Image(cccdBackFile),
            "0" // type = 0 for cccd_back
        );

        // Build portrait array with type = 1
        List<String> portraitArr = List.of(
            StringUtils.hasText(portraitFile.getFilename()) ? portraitFile.getFilename() : "portrait.jpg",
            buildBase64Image(portraitFile),
            "1" // type = 1 for portrait
        );

        // Build contract array with type = 2
        List<String> contractArr = List.of(
            StringUtils.hasText(contractFile.getFilename()) ? contractFile.getFilename() : "contract.jpg",
            buildBase64Image(contractFile),
            "2" // type = 2 for contract
        );

        return Arrays.asList(cccdFrontArr, cccdBackArr, portraitArr, contractArr);
    }

    private Resource downloadFile(String url) {
        if (StringUtils.hasText(url)) {
            try {
                DownloadOptionDTO downloadOption = DownloadOptionDTO.builder()
                    .uri(url)
                    .isPublic(false)
                    .build();
                return minioOperations.download(downloadOption);
            } catch (Exception e) {
                log.warn("Failed to download file from URL: {}, using default file", url, e);
                return getDefaultResource();
            }
        }
        return getDefaultResource();
    }

    private Resource getDefaultResource() {
        return new ByteArrayResource(new byte[0]) {
            @Override
            public String getFilename() {
                return "default.jpg";
            }
        };
    }

    private String buildBase64Image(Resource resource) {
        try {
            byte[] fileContent = resource.getInputStream().readAllBytes();
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
            log.error("Failed to convert file to Base64: {}", resource.getFilename(), e);
            return "";
        }
    }
}

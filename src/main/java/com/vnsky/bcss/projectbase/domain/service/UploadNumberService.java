package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.domain.port.primary.NumberTransactionDetailServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.UploadNumberServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.AppPickListRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.IsdnTransactionLineRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.IsdnTransactionRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.StockIsdnRepoPort;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.*;
import com.vnsky.bcss.projectbase.shared.utils.ErrorTranslator;
import com.vnsky.common.constant.ModelStatus;
import com.vnsky.common.dto.ErrorRecord;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.common.utils.DataUtils;
import com.vnsky.common.utils.TemporaryFileResource;
import com.vnsky.excel.dto.ExcelData;
import com.vnsky.excel.service.CsvOperations;
import com.vnsky.excel.service.XlsxOperations;
import com.vnsky.security.SecurityUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

@Service
@Slf4j
public class UploadNumberService implements UploadNumberServicePort {

    private final IsdnTransactionRepoPort isdnTransactionRepository;
    private final IsdnTransactionLineRepoPort isdnTransactionLineRepository;
    private final NumberTransactionDetailServicePort transactionDetailService;
    private final TransactionTemplate transactionTemplate;
    private final TaskExecutor taskExecutor;
    private final MessageSource errorMessageSource;
    private final CsvOperations csvOperations;
    private final XlsxOperations xlsxOperations;
    private final StockIsdnRepoPort stockIsdnRepository;
    private final AppPickListRepoPort appPickListRepository;

    public UploadNumberService(IsdnTransactionRepoPort isdnTransactionRepository, NumberTransactionDetailServicePort transactionDetailService,
                               TransactionTemplate transactionTemplate, TaskExecutor taskExecutor, @Qualifier("applicationErrorMessageSource") MessageSource errorMessageSource,
                               CsvOperations csvOperations, XlsxOperations xlsxOperations, StockIsdnRepoPort stockIsdnRepository, AppPickListRepoPort appPickListRepository, IsdnTransactionLineRepoPort isdnTransactionLineRepository) {
        this.isdnTransactionRepository = isdnTransactionRepository;
        this.transactionDetailService = transactionDetailService;
        this.transactionTemplate = transactionTemplate;
        this.taskExecutor = taskExecutor;
        this.errorMessageSource = errorMessageSource;
        this.csvOperations = csvOperations;
        this.xlsxOperations = xlsxOperations;
        this.stockIsdnRepository = stockIsdnRepository;
        this.appPickListRepository = appPickListRepository;
        this.isdnTransactionLineRepository = isdnTransactionLineRepository;
    }

    @Override
    public Page<IsdnTransactionDTO> find(LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable) {
        Page<IsdnTransactionDTO> isdnTransactionPage = isdnTransactionRepository.findUploadByTime(fromTime != null ? fromTime.toLocalDate().atStartOfDay(): null, toTime != null ? toTime.plusDays(1).toLocalDate().atStartOfDay() : null, pageable)
            .map(this.transactionDetailService::attachResultToTransaction);
        this.appPickListRepository.patchToDataList(isdnTransactionPage.getContent());
        return isdnTransactionPage;
    }

    @SneakyThrows
    @Override
    public IsdnTransactionDTO submit(MultipartFile numberFile,
                                     UploadNumberMetadataDTO metadata) {
        final Locale locale = LocaleContextHolder.getLocale();
        ErrorTranslator errorTranslator = (errorCode, args) ->
            this.errorMessageSource.getMessage(errorCode.getDetailCode(), args, locale);
        try (InputStream is = numberFile.getInputStream()) {
            this.transactionDetailService.validateNumberFile(is, numberFile.getOriginalFilename(), IsdnUploadDTO.class, errorTranslator, true);
        }
        // Đảm bảo file chứa ít nhất 1 dòng có trường số (isdn) không rỗng
        try (InputStream is = numberFile.getInputStream()) {
            boolean isCsv = this.csvOperations.isCsvFile(numberFile.getOriginalFilename());
            ExcelData<IsdnUploadDTO> excelData = isCsv ?
                this.csvOperations.readCsv(is, IsdnUploadDTO.class) :
                this.xlsxOperations.readExcel(is, IsdnUploadDTO.class);
            boolean hasAnyNumber = excelData.getDataLines().stream()
                .anyMatch(line -> line.getIsdn() != null && !line.getIsdn().trim().isEmpty());
            if (!hasAnyNumber) {
                throw BaseException.bussinessError(ErrorCode.ISDN_RESOURCE_EMPTY_NUMBER).build();
            }
        }

        IsdnTransactionDTO transaction = this.transactionTemplate.execute(status -> {
            IsdnTransactionDTO innerTransaction = IsdnTransactionDTO.builder()
                .description(metadata.getDescription())
                .processType(NumberProcessType.BATCH.getValue())
                .transDate(LocalDateTime.now())
                .transStatus(NumberTransactionStatus.PRE_START.getValue())
                .uploadStatus(NumberUploadStatus.PROCESSING.getValue())
                .clientId(SecurityUtil.getCurrentClientId())
                .build();
            return isdnTransactionRepository.saveAndFlush(innerTransaction);
        });
        Objects.requireNonNull(transaction);
        final String transId = transaction.getId();
        this.transactionDetailService.saveUploadFile(transId, numberFile);
        log.info("[UPLOAD_NUMBER] Transaction created: {}", transId);
        TemporaryFileResource temporaryFileResource = TemporaryFileResource.from(numberFile);
        this.taskExecutor.execute(new DelegatingSecurityContextRunnable(() -> {
            try {
                log.info("[UPLOAD_NUMBER] Process queue: {}", transId);
                ExcelData<IsdnUploadDTO> numberData = this.processBatchData(temporaryFileResource);
                // running validation in background
                log.info("[UPLOAD_NUMBER] Start number file validation for trans-{}", transId);
                Pair<List<Long>, Integer> validationResult = this.validateDataList(numberData, errorTranslator, progressPercentage ->
                    this.isdnTransactionRepository.updateCheckProgress(transId, progressPercentage / 2));
                int errorCount = validationResult.getSecond();
                log.info("[UPLOAD_NUMBER] Finish number file validation for trans-{} => error count = {}", transId, errorCount);
                Runnable completeCallback = () -> {
                    TemporaryFileResource checkResultFile = this.xlsxOperations.writeExcel(numberData, IsdnUploadDTO.class, false);
                    this.transactionDetailService.saveCheckFile(transId, checkResultFile, numberData.getDataLines().size(), errorCount);
                };
                if (errorCount > 0) {
                    completeCallback.run();
                    this.isdnTransactionRepository.updateCheckProgress(transId, 100);
                } else {
                    List<Long> isdns = validationResult.getFirst();
                    this.doUpload(transId, numberData, isdns, completeCallback);
                }
            } catch (Exception ex) {
                log.error("[UPLOAD_NUMBER] Background process crashed: ", ex);
                this.transactionDetailService.markCrashedTransaction(transId, ex);
            }
        }));
        return transaction;
    }

    @NotNull
    private List<IsdnUploadDTO> doInsertIsdns(String transId) {
        log.info("[UPLOAD_NUMBER_DIRECT], start processing trans-{}", transId);
        IsdnTransactionDTO transaction = isdnTransactionRepository.getById(transId);
        final String modifiedBy = transaction.getModifiedBy();
        final String createdBy = transaction.getCreatedBy();
        final int totalNumber = transaction.getTotalNumber();
        AtomicInteger failedCount = new AtomicInteger(0);
        List<IsdnUploadDTO> uploadDTOS = new ArrayList<>();

        Page<IsdnTransactionLineDTO> transactionLinePage;
        Map<Long, StockIsdnDTO> stockIsdnMap = new HashMap<>();
        Map<Long, IsdnTransactionLineDTO> transLineMap = new HashMap<>();

        int pageNumber = 0;
        int pageSize = 500;
        do {
            log.info("[UPLOAD_NUMBER_DIRECT] Start processing trans-{}, batch page-{}, size-{}", transId, pageNumber, pageSize);
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            transactionLinePage = isdnTransactionLineRepository.findByIsdnTransId(transId, pageable);

            List<IsdnTransactionLineDTO> transactionLines = transactionLinePage.getContent();
            transactionLines.forEach(transactionLine -> {
                StockIsdnDTO stockIsdnDTO = StockIsdnDTO.builder()
                    .isdn(transactionLine.getToIsdn())
                    .status(ModelStatus.ACTIVE.getValue())
                    .description(transactionLine.getDescription())
                    .modifiedBy(modifiedBy)
                    .createdBy(createdBy)
                    .build();
                stockIsdnMap.put(transactionLine.getToIsdn(), stockIsdnDTO);
                transLineMap.put(transactionLine.getToIsdn(), transactionLine);
            });

            transLineMap.values().forEach(transLine -> {
                transLine.setStatus(ModelStatus.ACTIVE.getValue());
                transLine.setError(Constant.MESSAGE_SUCCESS);
            });

            List<StockIsdnDTO> existedIsdns = stockIsdnRepository.findByIsdnIn(stockIsdnMap.keySet());
            List<Long> existedIds = existedIsdns.stream().map(StockIsdnDTO::getIsdn).toList();

            existedIds.forEach(existedId -> {
                stockIsdnMap.remove(existedId);
                IsdnTransactionLineDTO existedTransLine = transLineMap.get(existedId);
                existedTransLine.setStatus(ModelStatus.INACTIVE.getValue());
                existedTransLine.setError(Constant.MESSAGE_FAILURE);
                failedCount.incrementAndGet();
            });

            this.insertStockIsdnInBatch(new ArrayList<>(stockIsdnMap.values()));
            this.updateIsdnTransLineInBatch(transactionLines);
            stockIsdnMap.clear();
            transLineMap.clear();

            uploadDTOS.addAll(transactionLines.stream()
                .map(isdnTransactionLine -> IsdnUploadDTO.builder()
                    .isdn(isdnTransactionLine.getToIsdn().toString())
                    .description(isdnTransactionLine.getDescription())
                    .result(isdnTransactionLine.getError())
                    .build())
                .toList());
            pageNumber++;
            log.info("[UPLOAD_NUMBER_DIRECT] Finish processing trans-{}, batch page-{}, size-{}", transId, pageNumber, pageSize);
        }
        while (transactionLinePage.hasNext());

        this.transactionTemplate.executeWithoutResult(status -> {
            IsdnTransactionDTO innerTransaction = isdnTransactionRepository.getById(transId);
            innerTransaction.setTransStatus(NumberTransactionStatus.COMPLETE.getValue());
            innerTransaction.setSucceededNumber(totalNumber - failedCount.get());
            innerTransaction.setFailedNumber(failedCount.get());
            isdnTransactionRepository.saveAndFlush(innerTransaction);
        });
        log.info("[UPLOAD_NUMBER_DIRECT], finish processing trans-{}", transId);
        return uploadDTOS;
    }

    public void insertStockIsdnInBatch(List<StockIsdnDTO> stockIsdns) {
        this.transactionTemplate.executeWithoutResult(status -> this.stockIsdnRepository.saveAllAndFlush(stockIsdns));
    }

    public void updateIsdnTransLineInBatch(List<IsdnTransactionLineDTO> isdnTransactionLines) {
        this.transactionTemplate.executeWithoutResult(status -> this.isdnTransactionLineRepository.saveAllAndFlush(isdnTransactionLines));
    }

    @NotNull
    private ExcelData<IsdnUploadDTO> processBatchData(TemporaryFileResource numberFile) throws IOException {
        String fileName = Objects.requireNonNull(numberFile.getOriginalFileName());
        try (InputStream is = numberFile.getInputStream()) {
            boolean isCsv = this.csvOperations.isCsvFile(numberFile.getFilename());
            return isCsv ? this.csvOperations.readCsv(is, IsdnUploadDTO.class) :
                this.xlsxOperations.readExcel(is, IsdnUploadDTO.class);
        } catch (Exception ex) {
            log.error("Error while reading number file {}:", fileName, ex);
            throw ex;
        }
    }

    private Pair<List<Long>, Integer> validateDataList(ExcelData<IsdnUploadDTO> numberData,
                                                       ErrorTranslator errorTranslator, IntConsumer batchProgressCallback) {
        Map<Long, List<IsdnUploadDTO>> hmIsdn = NumberUploadDTO.fromExcel(numberData);
        AtomicInteger errorCounterAtomic = new AtomicInteger(0);
        AtomicInteger counter = new AtomicInteger(0);
        DataUtils.batchProcess(hmIsdn, hmIsdnBatch -> {
            hmIsdnBatch.forEach((isdn, transferNumberDTOList) -> {
                int errorCount = NumberUploadDTO.collectNumberErrors(isdn, transferNumberDTOList, errorTranslator,
                    (uploadNumberDTO, errors) ->
                    {
                        if (this.stockIsdnRepository.findByIsdn(uploadNumberDTO.getIsdnTruncated()).isPresent()) {
                            errors.add(errorTranslator.apply(ErrorCode.ISDN_RESOURCE_NUMBER_EXISTS));
                        }
                    });
                transferNumberDTOList.forEach(dto -> {
                    dto.finalizeResult();
                    if (!dto.getErrors().isEmpty()) {
                        dto.setReason(String.join("; ", dto.getErrors()));
                    } else {
                        dto.setReason(Constant.EMPTY_STRING);
                    }
                });
                errorCounterAtomic.addAndGet(errorCount);
            });
            batchProgressCallback.accept(counter.addAndGet(hmIsdnBatch.size()) * 100 / hmIsdn.size());
        });
        List<Long> isdns = hmIsdn.keySet().stream().toList();
        return Pair.of(isdns, errorCounterAtomic.get());
    }

    public void doUpload(String transId, ExcelData<IsdnUploadDTO> numberData, List<Long> isdns, Runnable completeCallback) {

        // Đặt thông tin giao dịch trước khi tạo dòng kết quả
        IsdnTransactionDTO transaction = this.transactionTemplate.execute(status -> {
            IsdnTransactionDTO innerTransaction = isdnTransactionRepository.getById(transId);
            innerTransaction = innerTransaction
                .setTotalNumber(numberData.getDataLines().size())
                .setSucceededNumber(0)
                .setFailedNumber(0)
                .setTransStatus(NumberTransactionStatus.PROCESSING.getValue())
                .setApprovalStatus(ApprovalStatus.APPROVED.getCode());
            return isdnTransactionRepository.saveAndFlush(innerTransaction);
        });
        Objects.requireNonNull(transaction);

        // Tạo dòng kết quả và hoàn tất giao dịch (bao gồm cập nhật số thành công/thất bại, tiến độ và file kết quả)
        this.transactionDetailService.createResultTransactionLine("[UPLOAD_NUMBER]", isdns, transId, completeCallback);

    }
}

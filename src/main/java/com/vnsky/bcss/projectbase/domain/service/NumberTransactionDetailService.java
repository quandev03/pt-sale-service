package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.FileInfoDTO;
import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionDTO;
import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionLineDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.NumberTransactionDetailServicePort;
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
import com.vnsky.excel.error.NoDataFileException;
import com.vnsky.excel.error.SampleFileMismatchException;
import com.vnsky.excel.service.CsvOperations;
import com.vnsky.excel.service.XlsxOperations;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class NumberTransactionDetailService implements NumberTransactionDetailServicePort {
    private static final String UPLOAD_FILENAME = "upload%s";
    private static final String CHECK_FILENAME = "check.xlsx";
    private static final String RESULT_FILENAME = "result.xlsx";
    private static final String NUMBER_FILE_NAME = "numberFile";

    private final CsvOperations csvOperations;
    private final XlsxOperations xlsxOperations;
    private final TransactionTemplate transactionTemplate;
    private final IsdnTransactionRepoPort isdnTransactionRepository;
    private final MinioOperations minioStorage;
    private final IsdnTransactionLineRepoPort isdnTransactionLineRepository;
    private final StockIsdnRepoPort stockIsdnRepository;

    @Override
    public IsdnTransactionDTO attachResultToTransaction(IsdnTransactionDTO isdnTransactionDTO) {
        String uploadFilename = isdnTransactionDTO.getUploadFilename();
        if (!Objects.equals(NumberProcessType.BATCH.getValue(), isdnTransactionDTO.getProcessType()))
            return isdnTransactionDTO;
        DownloadOptionDTO downloadOptionDTO;
        if (isdnTransactionDTO.getStepStatus() != null && isdnTransactionDTO.getStepStatus() >= NumberStepStatus.UPLOADED.getValue()) {
            String ext = "";
            if (isdnTransactionDTO.getUploadFilename() != null) {
                int dotIndex = uploadFilename.lastIndexOf(".");
                if (dotIndex >= 0) {
                    ext = uploadFilename.substring(dotIndex);
                }
            }
            downloadOptionDTO = DownloadOptionDTO.builder()
                .uri(Constant.MinioDir.ISDN_TRANSACTION, isdnTransactionDTO.getId(), String.format(UPLOAD_FILENAME, ext))
                .build();
            isdnTransactionDTO.setUploadFile(FileInfoDTO.builder()
                .fileUrl(downloadOptionDTO.getUri())
                .fileName(uploadFilename)
                .build());
        }
        if (Objects.equals(isdnTransactionDTO.getProcessType(), NumberProcessType.BATCH.getValue()) &&
            isdnTransactionDTO.getStepStatus() != null && isdnTransactionDTO.getStepStatus() >= NumberStepStatus.CHECKED.getValue()) {
            downloadOptionDTO = DownloadOptionDTO.builder()
                .uri(Constant.MinioDir.ISDN_TRANSACTION, isdnTransactionDTO.getId(), CHECK_FILENAME)
                .build();
            isdnTransactionDTO.setResultCheckFile(FileInfoDTO.builder()
                .fileUrl(downloadOptionDTO.getUri())
                .fileName(uploadFilename)
                .build());
        }
        if (isdnTransactionDTO.getStepStatus() != null && isdnTransactionDTO.getStepStatus() >= NumberStepStatus.PROCESSED.getValue()) {
            downloadOptionDTO = DownloadOptionDTO.builder()
                .uri(Constant.MinioDir.ISDN_TRANSACTION, isdnTransactionDTO.getId(), RESULT_FILENAME)
                .build();
            isdnTransactionDTO.setResultFile(FileInfoDTO.builder()
                .fileUrl(downloadOptionDTO.getUri())
                .fileName(uploadFilename)
                .build());
        }
        return isdnTransactionDTO;
    }

    @Override
    public <T> void validateNumberFile(InputStream is, String fileName, Class<T> clazz,
                                       ErrorTranslator errorTranslator, boolean allowCsv) {
        try {
            if (allowCsv && this.csvOperations.isCsvFile(fileName)) {
                this.csvOperations.isValidCsv(is, clazz);
            } else if (this.xlsxOperations.isXlsxFile(fileName)) {
                this.xlsxOperations.isValidXlsx(is, clazz);
            } else {
                throw BaseException.bussinessError(ErrorCode.ISDN_RESOURCE_FILE_ERROR)
                    .addProperty(
                        new ErrorRecord(errorTranslator.apply(ErrorCode.ISDN_RESOURCE_FILE_ERROR), NUMBER_FILE_NAME)
                    ).build();
            }
        } catch (SampleFileMismatchException sampleFileMismatchException) {
            throw BaseException.bussinessError(ErrorCode.ISDN_RESOURCE_WRONG_FILE_SAMPLE)
                .addProperty(new ErrorRecord(
                    errorTranslator.apply(ErrorCode.ISDN_RESOURCE_WRONG_FILE_SAMPLE), NUMBER_FILE_NAME
                ))
                .build();
        } catch (NoDataFileException noDataFileException) {
            throw BaseException.bussinessError(ErrorCode.ISDN_RESOURCE_EMPTY_NUMBER)
                .addProperty(new ErrorRecord(
                    errorTranslator.apply(ErrorCode.ISDN_RESOURCE_EMPTY_NUMBER), NUMBER_FILE_NAME
                )).build();
        } catch (Exception e) {
            throw BaseException.bussinessError(ErrorCode.ISDN_RESOURCE_FILE_ERROR)
                .addProperty(
                    new ErrorRecord(errorTranslator.apply(ErrorCode.ISDN_RESOURCE_FILE_ERROR), NUMBER_FILE_NAME)
                ).build();
        }
    }

    @Override
    @SneakyThrows
    public void saveUploadFile(String id, MultipartFile numberFile) {
        transactionTemplate.executeWithoutResult(status -> {
            IsdnTransactionDTO transaction = isdnTransactionRepository.getById(id);
            if (Objects.equals(transaction.getProcessType(), NumberProcessType.BATCH.getValue())) {
                String originalFilename = Objects.requireNonNull(numberFile.getOriginalFilename());
                transaction.setUploadFilename(originalFilename);
                try (InputStream numberIS = numberFile.getInputStream()) {
                    String ext = "";
                    int dotIndex = originalFilename.lastIndexOf(".");
                    if (dotIndex >= 0) {
                        ext = originalFilename.substring(dotIndex);
                    }
                    String filename = String.format(UPLOAD_FILENAME, ext);
                    UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                        .isPublic(false)
                        .originalFilename(originalFilename)
                        .uri(Constant.MinioDir.ISDN_TRANSACTION, transaction.getId(), filename)
                        .build();
                    this.minioStorage.upload(numberIS, uploadOptionDTO);
                    transaction.setStepStatus(NumberStepStatus.UPLOADED.getValue());
                    this.isdnTransactionRepository.saveAndFlush(transaction);
                } catch (IOException ex) {
                    log.error("Error read number file {}:", originalFilename, ex);
                    throw BaseException.bussinessError(ErrorCode.ISDN_UPLOAD_FILE_FAIL).build();
                }
            }
        });
    }

    @Override
    public void saveCheckFile(String id, TemporaryFileResource resultResource, int totalCount, int failCount) {
        this.saveCheckFile(id, resultResource, true, totalCount, failCount);
    }

    @Override
    @SneakyThrows
    public void saveCheckFile(String id, TemporaryFileResource resultResource, boolean delete, int totalCount, int failCount) {
        this.transactionTemplate.executeWithoutResult(status -> {
            IsdnTransactionDTO transaction = isdnTransactionRepository.getById(id);
            try (InputStream resultIS = resultResource.getInputStream(delete)) {
                UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                    .isPublic(false)
                    .uri(Constant.MinioDir.ISDN_TRANSACTION, transaction.getId(), CHECK_FILENAME)
                    .originalFilename(Constant.NumberProcessFile.getCheckFileName())
                    .build();
                this.minioStorage.upload(resultIS, uploadOptionDTO);
                if (failCount > 0) {
                    transaction.setUploadStatus(NumberUploadStatus.FAILURE.getValue());
                } else {
                    transaction.setUploadStatus(NumberUploadStatus.SUCCESS.getValue());
                }
                transaction.setTotalNumber(totalCount);
                transaction.setValidSucceededNumber(totalCount - failCount);
                transaction.setValidFailedNumber(failCount);
                transaction.setStepStatus(NumberStepStatus.CHECKED.getValue());
                this.isdnTransactionRepository.saveAndFlush(transaction);
                this.isdnTransactionRepository.updateCheckProgress(id, 100);
            } catch (IOException ex) {
                log.error("Error read result file {}:", resultResource.getFilename(), ex);
                throw BaseException.badRequest(ErrorCode.ISDN_CHECK_FILE_FAIL).build();
            }
        });
    }

    @Override
    public void createResultTransactionLine(String tag, List<Long> isdns, String transId, Runnable completeCallback) {
        IsdnTransactionDTO transaction = isdnTransactionRepository.getById(transId);
        Objects.requireNonNull(transaction);

        final boolean isBatch = Objects.equals(transaction.getProcessType(), NumberProcessType.BATCH.getValue());
        final LocalDateTime transDate = transaction.getTransDate();
        final int lineStatus = ModelStatus.ACTIVE.getValue();
        List<IsdnTransactionLineDTO> isdnTransactionLineList = new ArrayList<>();

        // Đánh dấu giao dịch đang xử lý
        this.transactionTemplate.executeWithoutResult(status -> {
            IsdnTransactionDTO inner = isdnTransactionRepository.getById(transId);
            inner.setTransStatus(NumberTransactionStatus.PROCESSING.getValue());
            isdnTransactionRepository.saveAndFlush(inner);
        });

        AtomicInteger counter = new AtomicInteger(0);
        DataUtils.batchProcess(isdns, isdnBatch -> {
            this.transactionTemplate.executeWithoutResult(status -> {
                List<IsdnTransactionLineDTO> batchLines = isdnBatch.stream()
                    .map(isdn -> IsdnTransactionLineDTO.builder()
                        .transDate(transDate)
                        .fromIsdn(isdn)
                        .toIsdn(isdn)
                        .quantity(1)
                        .error(null)
                        .isdnTransId(transId)
                        .status(lineStatus)
                        .build())
                    .collect(Collectors.toList());

                isdnTransactionLineRepository.saveAllAndFlush(batchLines);
                isdnTransactionLineList.addAll(batchLines);

                // Không còn bước chờ duyệt: thêm mới kho trực tiếp
                this.stockIsdnRepository.createStockIsdnIn(isdnBatch);
            });

            int progress = counter.addAndGet(isdnBatch.size()) * 100 / isdns.size();
            // Có thể cập nhật trực tiếp 0..100 trong giai đoạn tạo line
            this.isdnTransactionRepository.updateCheckProgress(transId, progress);
            log.info("{} Created lines for trans-{}, counter-{}", tag, transId, counter);
        }, 500);

        completeCallback.run();
        log.info("{} Complete callback for trans-{} done", tag, transId);

        this.transactionTemplate.executeWithoutResult(status -> {
            IsdnTransactionDTO inner = isdnTransactionRepository.getById(transId);
            inner.setStepStatus(NumberStepStatus.PROCESSED.getValue());
            inner.setSucceededNumber(isdns.size());
            inner.setFailedNumber(0);
            inner.setTransStatus(NumberTransactionStatus.COMPLETE.getValue());
            isdnTransactionRepository.saveAndFlush(inner);
        });

        if (isBatch) {
            ExcelData<IsdnTransactionLineDTO> excelData = new ExcelData<>(new HashMap<>(), isdnTransactionLineList);
            TemporaryFileResource resultResource = this.xlsxOperations.writeExcel(excelData, IsdnTransactionLineDTO.class, false);
            this.saveResultFile(transId, resultResource, true);
        }

        // Đảm bảo progress = 100 khi xong
        this.isdnTransactionRepository.updateCheckProgress(transId, 100);
        log.info("{} Finish processing trans-{}", tag, transId);
    }

    @Override
    @SneakyThrows
    public void saveResultFile(String id, TemporaryFileResource resultResource, boolean delete) {
        this.transactionTemplate.executeWithoutResult(status -> {
            IsdnTransactionDTO transaction = isdnTransactionRepository.getById(id);
            try (InputStream resultIS = resultResource.getInputStream(delete)) {
                UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                    .isPublic(false)
                    .uri(Constant.MinioDir.ISDN_TRANSACTION, transaction.getId(), RESULT_FILENAME)
                    .build();
                this.minioStorage.upload(resultIS, uploadOptionDTO);
                transaction.setStepStatus(NumberStepStatus.PROCESSED.getValue());
                this.isdnTransactionRepository.saveAndFlush(transaction);
            } catch (IOException ex) {
                log.error("Error read result file {}:", resultResource.getFilename(), ex);
                throw BaseException.badRequest(ErrorCode.ISDN_RESULT_FILE_FAIL).build();
            }
        });
    }

    @Override
    public void saveResultFile(String id, TemporaryFileResource resultResource) {
        this.saveResultFile(id, resultResource, true);
    }

    @Override
    @Transactional
    public void markCrashedTransaction(String id, Exception ex) {
        this.isdnTransactionRepository.markCrashedTransaction(id, ex);
    }

    @Override
    public IsdnTransactionDTO get(String transId) {
        IsdnTransactionDTO transactionDTO = isdnTransactionRepository.getById(transId);
        if (transactionDTO == null) {
            throw BaseException.notFoundError(ErrorCode.ISDN_TRANSACTION_NOT_FOUND).build();
        }
        if (transactionDTO.getProcessType() == NumberProcessType.INDIVIDUAL.getValue()) {
            List<IsdnTransactionLineDTO> isdnTransactionLineDTOS = isdnTransactionLineRepository.findAllByIsdnTransId(transId);
            if (isdnTransactionLineDTOS.isEmpty()) {
                throw BaseException.notFoundError(ErrorCode.ISDN_TRANSACTION_LINE_NOT_FOUND).build();
            }
            transactionDTO.setLines(isdnTransactionLineDTOS);
        }
        this.attachResultToTransaction(transactionDTO);
        return transactionDTO;
    }
}

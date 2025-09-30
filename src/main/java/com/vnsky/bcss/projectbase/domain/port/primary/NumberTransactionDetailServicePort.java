package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionDTO;
import com.vnsky.bcss.projectbase.shared.utils.ErrorTranslator;
import com.vnsky.common.utils.TemporaryFileResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface NumberTransactionDetailServicePort {

    IsdnTransactionDTO get(String transId);

    IsdnTransactionDTO attachResultToTransaction(IsdnTransactionDTO isdnTransactionDTO);

    <T> void validateNumberFile(InputStream is, String fileName, Class<T> clazz,
                                ErrorTranslator errorTranslator, boolean allowCsv);

    void saveUploadFile(String id, MultipartFile numberFile);

    void saveCheckFile(String id, TemporaryFileResource resultResource, int successCount, int failCount);

    void saveCheckFile(String id, TemporaryFileResource resultResource, boolean delete, int successCount, int failCount);

    void createResultTransactionLine(String tag, List<Long> isdns, String transId, Runnable completeCallback);

    void markCrashedTransaction(String id, Exception ex);

    void saveResultFile(String id, TemporaryFileResource resultResource, boolean delete);

    void saveResultFile(String id, TemporaryFileResource resultResource);
}

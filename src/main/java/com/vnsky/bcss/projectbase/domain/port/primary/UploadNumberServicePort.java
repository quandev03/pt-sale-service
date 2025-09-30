package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.IsdnTransactionDTO;
import com.vnsky.bcss.projectbase.domain.dto.UploadNumberMetadataDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public interface UploadNumberServicePort{
    Page<IsdnTransactionDTO> find(LocalDateTime fromTime, LocalDateTime toTime, Pageable pageable);

    IsdnTransactionDTO submit(MultipartFile numberFile, UploadNumberMetadataDTO metadata);
}

package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.SubscriberServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriberService implements SubscriberServicePort {
    private final SubscriberRepoPort subscriberRepoPort;
    private final MinioOperations minioOperations;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public SubscriberDTO saveAndFlushNewTransaction(SubscriberDTO dto) {
        return subscriberRepoPort.saveAndFlush(dto);
    }

    @Override
    public SubscriberDTO findByIsdn(Long isdn) {
        return subscriberRepoPort.findByLastIsdn(isdn).orElse(null);
    }

    @Override
    public Resource downloadFile(String url) {
        DownloadOptionDTO downloadOption = DownloadOptionDTO.builder()
            .uri(url)
            .isPublic(false)
            .build();
        return minioOperations.download(downloadOption);
    }
}

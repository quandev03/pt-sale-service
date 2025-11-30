package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.AdvertisementDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.AdvertisementServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.AdvertisementRepoPort;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertisementService implements AdvertisementServicePort {

    private final AdvertisementRepoPort advertisementRepoPort;
    private final MinioOperations minioOperations;

    private static final String LOG_PREFIX = "[AdvertisementService]_";

    @Override
    @Transactional
    public AdvertisementDTO create(AdvertisementDTO dto) {
        log.info("{}Creating advertisement: {}", LOG_PREFIX, dto.getTitle());

        // Validate dates
        validateDates(dto.getStartDate(), dto.getEndDate());

        // Set clientId from security context
        String clientId = SecurityUtil.getCurrentClientId();
        dto.setClientId(clientId);

        // Set default status if not provided
        if (dto.getStatus() == null) {
            dto.setStatus(AdvertisementStatus.DRAFT);
        }

        return advertisementRepoPort.save(dto);
    }

    @Override
    @Transactional
    public AdvertisementDTO update(String id, AdvertisementDTO dto) {
        log.info("{}Updating advertisement id: {}", LOG_PREFIX, id);

        String clientId = SecurityUtil.getCurrentClientId();

        // Check if advertisement exists and belongs to current client
        AdvertisementDTO existing = advertisementRepoPort.findByIdAndClientId(id, clientId)
            .orElseThrow(() -> {
                log.error("{}Advertisement not found: {} for clientId: {}", LOG_PREFIX, id, clientId);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Quảng cáo không tồn tại")
                    .build();
            });

        // Validate dates if provided
        if (dto.getStartDate() != null || dto.getEndDate() != null) {
            LocalDateTime startDate = dto.getStartDate() != null ? dto.getStartDate() : existing.getStartDate();
            LocalDateTime endDate = dto.getEndDate() != null ? dto.getEndDate() : existing.getEndDate();
            validateDates(startDate, endDate);
        }

        // Update fields
        if (StringUtils.isNotBlank(dto.getTitle())) {
            existing.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            existing.setContent(dto.getContent());
        }
        if (StringUtils.isNotBlank(dto.getImageUrl())) {
            log.info("{}Creating advertisement image: {}", LOG_PREFIX, dto.getImageUrl());
            existing.setImageUrl(dto.getImageUrl());
        }
        if (dto.getStartDate() != null) {
            existing.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            existing.setEndDate(dto.getEndDate());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }

        return advertisementRepoPort.save(existing);
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.info("{}Deleting advertisement id: {}", LOG_PREFIX, id);

        String clientId = SecurityUtil.getCurrentClientId();

        // Check if advertisement exists and belongs to current client
        advertisementRepoPort.findByIdAndClientId(id, clientId)
            .orElseThrow(() -> {
                log.error("{}Advertisement not found: {} for clientId: {}", LOG_PREFIX, id, clientId);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Quảng cáo không tồn tại")
                    .build();
            });

        advertisementRepoPort.deleteById(id);
        log.info("{}Advertisement deleted: {}", LOG_PREFIX, id);
    }

    @Override
    public AdvertisementDTO getById(String id) {
        log.info("{}Getting advertisement by id: {}", LOG_PREFIX, id);

        String clientId = SecurityUtil.getCurrentClientId();

        return advertisementRepoPort.findByIdAndClientId(id, clientId)
            .orElseThrow(() -> {
                log.error("{}Advertisement not found: {} for clientId: {}", LOG_PREFIX, id, clientId);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Quảng cáo không tồn tại")
                    .build();
            });
    }

    @Override
    public List<AdvertisementDTO> getAll(String clientId, AdvertisementStatus status) {
        log.info("{}Getting all advertisements for clientId: {}, status: {}", LOG_PREFIX, clientId, status);

        List<AdvertisementDTO> all = advertisementRepoPort.findByClientId(clientId);

        if (status != null) {
            return all.stream()
                .filter(ad -> ad.getStatus() == status)
                .toList();
        }

        return all;
    }

    @Override
    public List<AdvertisementDTO> getActiveAdvertisements() {
        log.info("{}Getting active advertisements", LOG_PREFIX);

        LocalDateTime now = LocalDateTime.now();
        return advertisementRepoPort.findActiveAdvertisements(now);
    }

    @Override
    @Transactional
    public AdvertisementDTO getRandomActiveAdvertisement() {
        log.info("{}Getting random active advertisement", LOG_PREFIX);

        LocalDateTime now = LocalDateTime.now();
        AdvertisementDTO advertisement = advertisementRepoPort.findRandomActiveAdvertisement(now)
            .orElseThrow(() -> {
                log.warn("{}No active advertisement found", LOG_PREFIX);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Không tìm thấy quảng cáo đang diễn ra")
                    .build();
            });

        // Increment view count when getting random advertisement
        advertisementRepoPort.incrementViewCount(advertisement.getId());
        log.info("{}View count incremented for advertisement: {}", LOG_PREFIX, advertisement.getId());

        // Reload to get updated view count
        return advertisementRepoPort.findById(advertisement.getId())
            .orElse(advertisement);
    }

    @Override
    @Transactional
    public void incrementViewCount(String id) {
        log.info("{}Incrementing view count for advertisement: {}", LOG_PREFIX, id);

        // Check if advertisement exists
        advertisementRepoPort.findById(id)
            .orElseThrow(() -> {
                log.error("{}Advertisement not found: {}", LOG_PREFIX, id);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Quảng cáo không tồn tại")
                    .build();
            });

        advertisementRepoPort.incrementViewCount(id);
        log.info("{}View count incremented for advertisement: {}", LOG_PREFIX, id);
    }

    /**
     * Upload image to MinIO and return the file URL
     */
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String imageUrl = "advertisements/" + fileName;

            UploadOptionDTO uploadOption = UploadOptionDTO.builder()
                .uri(imageUrl)
                .isPublic(true)
                .build();

            minioOperations.upload(file.getInputStream(), uploadOption);
            log.info("{}Image uploaded to MinIO: {}", LOG_PREFIX, imageUrl);
            return imageUrl;
        } catch (Exception e) {
            log.error("{}Error uploading image to MinIO: {}", LOG_PREFIX, e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Lỗi khi upload ảnh: " + e.getMessage())
                .build();
        }
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Thời gian bắt đầu và kết thúc không được để trống")
                .build();
        }

        if (endDate.isBefore(startDate)) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Thời gian kết thúc phải sau thời gian bắt đầu")
                .build();
        }
    }
}


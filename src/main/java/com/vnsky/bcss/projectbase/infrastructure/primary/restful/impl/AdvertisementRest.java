package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.dto.AdvertisementDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.AdvertisementServicePort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateAdvertisementRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.request.UpdateAdvertisementRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.AdvertisementResponse;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.AdvertisementOperation;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.AdvertisementStatus;
import com.vnsky.bcss.projectbase.shared.utils.DateUtils;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import com.vnsky.security.SecurityUtil;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdvertisementRest implements AdvertisementOperation {

    private final AdvertisementServicePort advertisementServicePort;
    private final MinioOperations minioClient;

    @Override
    public ResponseEntity<AdvertisementResponse> create(
        CreateAdvertisementRequest request,
        @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        // Upload image if provided
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = ((com.vnsky.bcss.projectbase.domain.service.AdvertisementService) advertisementServicePort).uploadImage(image);
        } else if (request.getImageUrl() != null) {
            imageUrl = request.getImageUrl();
        }

        LocalDateTime now = LocalDateTime.now();

        String fileUrl =  "/advertisements/" + DateUtils.localDateTimeToString(now, Constant.DATE_TIME_NO_SYMBOL_PATTERN) + "/" + image.getOriginalFilename();

        UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
            .uri(fileUrl)
            .isPublic(false)
            .build();
        minioClient.upload(image.getInputStream(), uploadOptionDTO);

        // Map request to DTO
        AdvertisementDTO dto = AdvertisementDTO.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .imageUrl(fileUrl)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .status(request.getStatus())
            .build();

        AdvertisementDTO created = advertisementServicePort.create(dto);
        return ResponseEntity.ok(mapToResponse(created));
    }

    @Override
    public ResponseEntity<AdvertisementResponse> update(
        String id,
        UpdateAdvertisementRequest request,
        MultipartFile image) {

        // Upload new image if provided
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = ((com.vnsky.bcss.projectbase.domain.service.AdvertisementService) advertisementServicePort).uploadImage(image);
        } else if (request.getImageUrl() != null) {
            imageUrl = request.getImageUrl();
        }
        LocalDateTime now = LocalDateTime.now();

        String fileUrl =  "/advertisements/" + DateUtils.localDateTimeToString(now, Constant.DATE_TIME_NO_SYMBOL_PATTERN) + "/" + image.getOriginalFilename();

        log.info("FILE_IMAGE: {}", fileUrl);
        try{
            UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                .uri(fileUrl)
                .isPublic(false)
                .build();
            minioClient.upload(image.getInputStream(), uploadOptionDTO);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        // Map request to DTO
        AdvertisementDTO dto = AdvertisementDTO.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .imageUrl(fileUrl)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .status(request.getStatus())
            .build();

        AdvertisementDTO updated = advertisementServicePort.update(id, dto);
        return ResponseEntity.ok(mapToResponse(updated));
    }

    @Override
    public ResponseEntity<Object> delete(String id) {
        advertisementServicePort.delete(id);
        return ResponseEntity.ok(Map.of("message", "Quảng cáo đã được xóa thành công"));
    }

    @Override
    public ResponseEntity<AdvertisementResponse> getById(String id) {
        AdvertisementDTO dto = advertisementServicePort.getById(id);
        return ResponseEntity.ok(mapToResponse(dto));
    }

    @Override
    public ResponseEntity<List<AdvertisementResponse>> getAll(AdvertisementStatus status) {
        String clientId = SecurityUtil.getCurrentClientId();
        List<AdvertisementDTO> dtos = advertisementServicePort.getAll(clientId, status);
        List<AdvertisementResponse> responses = dtos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<List<AdvertisementResponse>> getActiveAdvertisements() {
        List<AdvertisementDTO> dtos = advertisementServicePort.getActiveAdvertisements();
        List<AdvertisementResponse> responses = dtos.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<AdvertisementResponse> getRandomActiveAdvertisement() {
        AdvertisementDTO dto = advertisementServicePort.getRandomActiveAdvertisement();
        return ResponseEntity.ok(mapToResponse(dto));
    }

    @Override
    public ResponseEntity<Object> incrementViewCount(String id) {
        advertisementServicePort.incrementViewCount(id);
        return ResponseEntity.ok(Map.of("message", "Lượt xem đã được tăng thành công"));
    }

    private AdvertisementResponse mapToResponse(AdvertisementDTO dto) {
        return AdvertisementResponse.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .content(dto.getContent())
            .imageUrl(dto.getImageUrl())
            .startDate(dto.getStartDate())
            .endDate(dto.getEndDate())
            .status(dto.getStatus())
            .clientId(dto.getClientId())
            .createdBy(dto.getCreatedBy())
            .createdDate(dto.getCreatedDate())
            .modifiedBy(dto.getModifiedBy())
            .modifiedDate(dto.getModifiedDate())
            .viewCount(dto.getViewCount() != null ? dto.getViewCount() : 0L)
            .build();
    }
}



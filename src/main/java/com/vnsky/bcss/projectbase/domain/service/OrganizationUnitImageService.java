package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitImageDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitImageServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitImageRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.DeleteOptionDTO;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrganizationUnitImageService implements OrganizationUnitImageServicePort {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String FOLDER_PREFIX = "organization-unit-images";

    private final OrganizationUnitImageRepoPort imageRepoPort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;
    private final MinioOperations minioOperations;

    @Override
    @Transactional
    public List<String> uploadImages(String orgUnitId, List<MultipartFile> files) {
        validateOrganizationUnit(orgUnitId);
        validateFiles(files);

        // Delete old images from MinIO and DB
        List<OrganizationUnitImageDTO> oldImages = imageRepoPort.findByOrgUnitId(orgUnitId);
        for (OrganizationUnitImageDTO oldImage : oldImages) {
            deleteFromMinio(oldImage.getImageUrl());
        }
        imageRepoPort.deleteByOrgUnitId(orgUnitId);

        // Upload new images
        List<OrganizationUnitImageDTO> imageDTOs = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            try {
                String imageUrl = uploadToMinio(orgUnitId, file);
                imageUrls.add(imageUrl);

                OrganizationUnitImageDTO imageDTO = OrganizationUnitImageDTO.builder()
                    .orgUnitId(orgUnitId)
                    .imageUrl(imageUrl)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .sortOrder(i)
                    .build();

                imageDTOs.add(imageDTO);
            } catch (IOException e) {
                log.error("Failed to upload image to MinIO for orgUnitId: {}", orgUnitId, e);
                throw BaseException.badRequest(ErrorCode.UPLOAD_FILE_TO_MINIO_FAIL)
                    .message("Lỗi tải ảnh lên MinIO")
                    .build();
            }
        }

        imageRepoPort.saveAll(imageDTOs);
        return imageUrls;
    }

    @Override
    public Resource downloadImage(String orgUnitId, String imageId) {
        validateOrganizationUnit(orgUnitId);

        List<OrganizationUnitImageDTO> images = imageRepoPort.findByOrgUnitId(orgUnitId);
        OrganizationUnitImageDTO image = images.stream()
            .filter(img -> img.getId().equals(imageId))
            .findFirst()
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.PACKAGE_NOT_EXISTS)
                .message("Ảnh không tồn tại")
                .build());

        try {
            DownloadOptionDTO downloadOption = DownloadOptionDTO.builder()
                .uri(image.getImageUrl())
                .isPublic(false)
                .build();

            return minioOperations.download(downloadOption);
        } catch (Exception e) {
            log.error("Failed to download image from MinIO: {}", image.getImageUrl(), e);
            throw BaseException.badRequest(ErrorCode.ERROR_UPLOAD_FILE_TO_MINIO)
                .message("Lỗi tải ảnh từ MinIO")
                .build();
        }
    }

    @Override
    @Transactional
    public List<String> updateImages(String orgUnitId, List<MultipartFile> files) {
        // updateImages now does the same as uploadImages (delete old and upload new)
        return uploadImages(orgUnitId, files);
    }

    @Override
    public List<String> getImageUrls(String orgUnitId) {
        return imageRepoPort.findByOrgUnitId(orgUnitId)
            .stream()
            .map(OrganizationUnitImageDTO::getImageUrl)
            .toList();
    }

    private void validateOrganizationUnit(String orgUnitId) {
        organizationUnitRepoPort.findById(orgUnitId)
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                .message("Đơn vị tổ chức không tồn tại")
                .build());
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.PACKAGE_PROFILE_INVALID)
                .message("Danh sách ảnh không được để trống")
                .build();
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw BaseException.badRequest(ErrorCode.PACKAGE_PROFILE_INVALID)
                    .message("File ảnh không được để trống")
                    .build();
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw BaseException.badRequest(ErrorCode.MAX_SIZE_FILE)
                    .message("Dung lượng file vượt quá 5MB")
                    .build();
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw BaseException.badRequest(ErrorCode.PACKAGE_PROFILE_INVALID)
                    .message("File phải là định dạng ảnh")
                    .build();
            }
        }
    }

    private String uploadToMinio(String orgUnitId, MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String imageUrl = String.format("%s/%s/%s", FOLDER_PREFIX, orgUnitId, fileName);

        UploadOptionDTO uploadOption = UploadOptionDTO.builder()
            .uri(imageUrl)
            .isPublic(false)
            .build();

        minioOperations.upload(file.getInputStream(), uploadOption);
        log.info("Uploaded image to MinIO: {}", imageUrl);

        return imageUrl;
    }

    private void deleteFromMinio(String imageUrl) {
        try {
            DeleteOptionDTO deleteOption = DeleteOptionDTO.builder()
                .uri(imageUrl)
                .isPublic(false)
                .build();

            minioOperations.remove(deleteOption);
            log.info("Deleted image from MinIO: {}", imageUrl);
        } catch (Exception e) {
            log.warn("Failed to delete image from MinIO: {}", imageUrl, e);
            // Don't throw exception, just log warning
        }
    }
}


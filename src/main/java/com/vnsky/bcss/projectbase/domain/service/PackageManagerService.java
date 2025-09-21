package com.vnsky.bcss.projectbase.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.mapper.PackageProfileMapper;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.utils.QrUtils;
import com.vnsky.bcss.projectbase.shared.utils.SecurityDataUtils;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import com.vnsky.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageManagerService implements PackageManagerServicePort {

    private final PackageProfileRepoPort packageProfileRepoPort;
    private final ObjectMapper objectMapper;
    private final PackageProfileMapper packageProfileMapper;
    private final MinioOperations minioOperations;

    private static final Pattern PACKAGE_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]+(?>_[a-zA-Z0-9]+)*$");


    @Override
    public Page<PackageProfileDTO> getListPackageProfile(String pckCodeOrPckName, Integer status, Long minPrice, Long maxPrice, Pageable pageable) {
        log.debug("Get list package profile");
        log.info("Search with pckCodeOrPckName: {} and status: {}", pckCodeOrPckName, status);

        return packageProfileRepoPort.searchPackageProfile(pckCodeOrPckName, status, minPrice, maxPrice, pageable);
    }

    @Override
    public PackageProfileDTO getDetailPackageProfile(String idPackageProfile) {
        log.debug("Get detail package profile: {}", idPackageProfile);
        return packageProfileRepoPort.findById(idPackageProfile);
    }

    @Override
    @Transactional
    public PackageProfileDTO createPackageProfile(PackageProfileDTO data, MultipartFile imagePackage) {
        log.debug("[CREATE_PACKAGE_PROFILE]: Start");
        log.info("Current id: {}", SecurityUtil.getCurrentUserId());

        validateDataCreatePackageProfile(data);
        if(!Objects.isNull(imagePackage)){
            log.debug("[CREATE_PACKAGE_PROFILE]: Upload image to miniO");
            String urlImage = uploadFileToMinio(imagePackage, data.getPckCode());
            data.setUrlImagePackage(urlImage);
        }
        PackageProfileDTO dto = packageProfileRepoPort.saveAndFlush(data);
        log.info("[CREATE_PACKAGE_PROFILE]: SUCCESS");
        return dto;
    }

    @Override
    @Transactional
    public PackageProfileDTO updatePackageProfile(String idPackageProfile,  PackageProfileDTO data,MultipartFile images) {
        log.info("[UPDATE_PACKAGE_PROFILE]: Start");
        log.debug("Update package profile: {}", idPackageProfile);
        PackageProfileDTO dto = packageProfileRepoPort.findById(idPackageProfile);

        log.info("[UPDATE_PACKAGE_PROFILE]: find data {}",  dto);dto.setPckName(data.getPckName());
        dto.setDescription(data.getDescription());
        if( !Objects.isNull(data.getPackagePrice()) && data.getPackagePrice()>=0) dto.setPackagePrice(data.getPackagePrice());
        if (!Objects.isNull(data.getStatus())) dto.setStatus(data.getStatus());
        if (!Objects.isNull(images)) {
            String urlImage = uploadFileToMinio(images, data.getPckCode());
            dto.setUrlImagePackage(urlImage);
        }
        dto = packageProfileRepoPort.saveAndFlush(dto);
        log.info("[UPDATE_PACKAGE_PROFILE]: SUCCESS");
        return dto;
    }

    @Override
    @Transactional
    public PackageProfileDTO updateStatusPackageProfile(String idPackageProfile, int status) {
        log.info("[UPDATE_STATUS_PACKAGE_PROFILE]: Start");
        log.debug("[UPDATE_STATUS_PACKAGE_PROFILE] Update status package profile: {}", idPackageProfile);
        PackageProfileDTO dto = packageProfileRepoPort.findById(idPackageProfile);
        log.info("[UPDATE_STATUS_PACKAGE_PROFILE]: find data {}",  dto);
        dto.setStatus(status);
        dto = packageProfileRepoPort.saveAndFlush(dto);
        log.info("[UPDATE_STATUS_PACKAGE_PROFILE]: SUCCESS");
        return dto;
    }

    @Override
    @Transactional
    public void deletePackageProfile(String idPackageProfile) {
        log.debug("[DELETE_PACKAGE_PROFILE]: Start");
        log.debug("Delete package profile: {}", idPackageProfile);
        packageProfileRepoPort.deleteById(idPackageProfile);
        log.info("[DELETE_PACKAGE_PROFILE]: SUCCESS");

    }

    @Override
    public List<PackageProfileDTO> getListPackageProfileFree() {
        log.debug("[GET_LIST_PACKAGE_PROFILE_FREE]: Start");
        List<PackageProfileDTO> packageProfileDTOS = packageProfileRepoPort.getListPackageProfileFree();
        log.info("[GET_LIST_PACKAGE_PROFILE_FREE]: SUCCESS, get {} package profiles", packageProfileDTOS.size());
        return packageProfileDTOS;
    }

    @Override
    public List<PackageProfileDTO> getAllPackageProfile() {
        return packageProfileRepoPort.getAll();
    }

    private void validateDataCreatePackageProfile(PackageProfileDTO request) {
        if (Objects.isNull(request)) {
            log.error("Package manager request is null");
            throw  BaseException.bussinessError(ErrorCode.DATA_PACKAGE_PROFILE_REQUEST_NOT_NULL).build();
        }
        // validate package code
        validatePackageCode(request.getPckCode());
        if(request.getPackagePrice() < 0){
            log.error("Price Package is negative");
            throw BaseException.bussinessError(ErrorCode.PACKAGE_PRICE_MUST_BE_GREATER_THAN_ZERO).build();
        }
    }

    private void validatePackageCode(String codePackage) {
        if (Objects.isNull(codePackage) || codePackage.isEmpty()) {
            log.error("Package code is null");
            throw BaseException.notFoundError(ErrorCode.PACKAGE_CODE_NOT_NULL).build();
        }

        if(packageProfileRepoPort.isExistPackageCode(codePackage)){
            log.error("Package code is exist");
            throw BaseException.bussinessError(ErrorCode.PACKAGE_CODE_IS_REALLY_EXIST).build();
        }

        if(!PACKAGE_CODE_PATTERN.matcher(codePackage).matches()){
            log.error("Package code is invalid");
            throw BaseException.bussinessError(ErrorCode.PACKAGE_CODE_INVALID).build();
        }

    }

    private String uploadFileToMinio(MultipartFile file, String packageCode){
        try {
            UploadOptionDTO uploadOptionDTO = UploadOptionDTO.builder()
                .uri(Constant.MinioDir.PackageProfile.FOLDER_URL,
                    packageCode,
                    UUID.randomUUID().toString())
                .isPublic(false)
                .build();

            minioOperations.upload(file.getInputStream(), uploadOptionDTO);
            log.info("[UPLOAD_IMAGE_PACKAGE_PROFILE]: upload file successfully");
            return uploadOptionDTO.getUri();
        } catch (IOException e) {
            log.error("[UPLOAD_IMAGE_PACKAGE_PROFILE]: Cannot upload file to minio", e);
            throw BaseException.badRequest(ErrorCode.UPLOAD_FILE_TO_MINIO_FAIL).build();
        }
    }
}

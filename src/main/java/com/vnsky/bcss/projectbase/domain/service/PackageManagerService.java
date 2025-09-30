package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticOrgResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.dto.ErrorRecord;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.common.exception.domain.ErrorKey;
import com.vnsky.minio.dto.DeleteOptionDTO;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import com.vnsky.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final MinioOperations minioOperations;
    private static final Long MAX_SIZE = 5242880L;

    private static final Pattern PACKAGE_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9]+(?>_[a-zA-Z0-9]+)*$");
    private static final String FIELD_PCK_CODE = "pckCode";
    private static final String FIELD_PCK_PRICE = "pckPrice";
    private static final String FIELD_PCK_NAME = "pckName";
    private static final int MAX_LENGTH_CODE = 20;
    private static final int MAX_LENGTH_NAME = 100;
    private static final int MAX_LENGTH_DESCRIPT = 200;

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
        if (!Objects.isNull(imagePackage)) {
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
    public PackageProfileDTO updatePackageProfile(String idPackageProfile, PackageProfileDTO data, MultipartFile images) {
        log.info("[UPDATE_PACKAGE_PROFILE]: Start");
        log.debug("Update package profile: {}", idPackageProfile);
        PackageProfileDTO dto = packageProfileRepoPort.findById(idPackageProfile);

        log.info("[UPDATE_PACKAGE_PROFILE]: find data {}", dto);
        dto.setPckName(data.getPckName());
        dto.setDescription(data.getDescription());
        dto.setCycleValue(data.getCycleValue());
        dto.setCycleUnit(data.getCycleUnit());
        if (!Objects.isNull(data.getPackagePrice()) && data.getPackagePrice() >= 0)
            dto.setPackagePrice(data.getPackagePrice());
        if (!Objects.isNull(data.getStatus())) dto.setStatus(data.getStatus());
        if (Objects.isNull(images)) {
            if (!Objects.isNull(dto.getUrlImagePackage())) {
                deleteFileToMinio(data.getUrlImagePackage());
            }
            dto.setUrlImagePackage(null);

        } else {
            if (!Objects.isNull(dto.getUrlImagePackage())) {
                deleteFileToMinio(data.getUrlImagePackage());
            }
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
        log.info("[UPDATE_STATUS_PACKAGE_PROFILE]: find data {}", dto);
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
    public List<PackageProfileDTO> getListPackageProfile() {
        log.debug("[GET_LIST_PACKAGE_PROFILE_FREE]: Start");
        String currentClientId = SecurityUtil.getCurrentClientId();
        List<PackageProfileDTO> packageProfileDTOS = packageProfileRepoPort.getListPackageProfile(currentClientId);
        log.info("[GET_LIST_PACKAGE_PROFILE_FREE]: SUCCESS, get {} package profiles", packageProfileDTOS.size());
        return packageProfileDTOS;
    }

    @Override
    public List<PackageProfileDTO> getAllPackageProfile() {
        return packageProfileRepoPort.getAll();
    }

    @Override
    public ResponseEntity<Resource> downloadImage(String id) {
        try {
            log.debug("[DOWNLOAD_IMAGE]: Start package ");
            PackageProfileDTO dto = packageProfileRepoPort.findById(id);

            if(Objects.isNull(dto.getUrlImagePackage())){
                log.error("[DOWNLOAD_IMAGE_PROFILE]: image package profile not exists");
                return null;
            }

            DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
                .uri(dto.getUrlImagePackage())
                .isPublic(false)
                .build();
            Resource resource = minioOperations.download(downloadOptionDTO);

            if (resource == null || !resource.exists() || !resource.isReadable()) {
                throw BaseException.badRequest(ErrorKey.BAD_REQUEST).build();
            }

            String contentType = determineContentType(resource.getFilename());
            log.info("[DOWNLOAD_IMAGE_PROFILE]: type image {}",  contentType);
            log.info("[DOWNLOAD_IMAGE_PROFILE]: type image {}",  MediaType.valueOf(contentType));
            return ResponseEntity.ok()
                .contentType(MediaType.valueOf(contentType))
                .contentLength(resource.contentLength()) // Lấy độ dài file trực tiếp từ resource
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename()).build().getFilename())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } catch (IOException e) {
            throw BaseException.badRequest(ErrorKey.BAD_REQUEST).build();
        }
    }

    @Override
    public Long totalPackagesSold(String orgCode) {
        return packageProfileRepoPort.totalPackagesSold(orgCode);
    }

    @Override
    public List<StatisticResponse> statisticPackagesSold(String orgCode, String startDate, String endDate, int granularity) {
        return packageProfileRepoPort.statisticPackagesSold(orgCode, startDate, endDate, granularity);
    }

    @Override
    public List<StatisticOrgResponse> statisticPackagesSoldOrg(String orgCode, String startDate, String endDate) {
        return packageProfileRepoPort.statisticPackagesSoldOrg(orgCode, startDate, endDate);
    }

    @Override
    public Long revenusPackageSold(String orgCode) {
        return packageProfileRepoPort.revenusPackageSold(orgCode);
    }

    private String determineContentType(String filename) {
        if (filename == null || filename.isEmpty())  return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) return MediaType.IMAGE_JPEG_VALUE;
        else if (filename.toLowerCase().endsWith(".gif"))  return MediaType.IMAGE_GIF_VALUE;
        else return MediaType.IMAGE_PNG_VALUE;
    }

    private void validateDataCreatePackageProfile(PackageProfileDTO request) {
        if (Objects.isNull(request)) {
            log.error("Package manager request is null");
            throw  BaseException.badRequest(ErrorCode.DATA_PACKAGE_PROFILE_REQUEST_NOT_NULL).build();
        }
        // validate package code
        validatePackageCode(request.getPckCode());
        if (request.getPackagePrice() < 0) {
            log.error("Price Package is negative");
            throw BaseException.bussinessError(ErrorCode.PACKAGE_PRICE_MUST_BE_GREATER_THAN_ZERO).addProperty(new ErrorRecord("Giá gói cước không được nhỏ hơn 0", FIELD_PCK_PRICE)).build();
        }
        if (request.getPckName().isEmpty()) {
            log.error("Package manager request is empty");
            throw BaseException.badRequest(ErrorCode.PCK_NAME_EMPTY).addProperty(new ErrorRecord("Tên gói cước không được để trống", FIELD_PCK_NAME)).build();
        }

        if(request.getPckName().length() > MAX_LENGTH_NAME){
            log.error("Package manager request is longer than 100 characters");
            request.setPckName(request.getPckName().substring(0, MAX_LENGTH_NAME));
        }

        if (packageProfileRepoPort.isExistPckName(request.getPckName())) {
            log.error("Package manager request is exist");
            throw BaseException.badRequest(ErrorCode.PACK_NAME_EXIST).addProperty(new ErrorRecord("Tên gói cước đã tồn tại trên hệ thống", FIELD_PCK_NAME)).build();
        }

        if (request.getDescription().length() > MAX_LENGTH_DESCRIPT){
            log.error("Package manager request is longer than 200 characters");
            request.setDescription(request.getDescription().substring(0, MAX_LENGTH_DESCRIPT));
        }
    }

    private void validatePackageCode(String codePackage) {
        if (Objects.isNull(codePackage) || codePackage.isEmpty()) {
            log.error("Package code is null");
            throw BaseException.notFoundError(ErrorCode.PACKAGE_CODE_NOT_NULL).addProperty(new ErrorRecord("Mã gói cước không được để trống", FIELD_PCK_CODE)).build();
        }
        if(codePackage.length() > MAX_LENGTH_CODE){
            log.error("Package code length is greater than 20");
            codePackage = codePackage.substring(0, MAX_LENGTH_CODE);
        }

        if(!PACKAGE_CODE_PATTERN.matcher(codePackage).matches()){
            log.error("Package code is invalid");
            throw BaseException.bussinessError(ErrorCode.PACKAGE_CODE_INVALID).addProperty(new ErrorRecord("Mã gói cước không đúng định dạng", FIELD_PCK_CODE)).build();
        }

        if(packageProfileRepoPort.isExistPackageCode(codePackage)){
            log.error("Package code is exist");
            throw BaseException.bussinessError(ErrorCode.PACKAGE_CODE_IS_REALLY_EXIST).addProperty(new ErrorRecord("Mã gói cước đã tồn tại trên hệ thống", FIELD_PCK_CODE)).build();
        }

    }

    private String uploadFileToMinio(MultipartFile file, String packageCode){

        log.debug("[UPLOAD_FILE_TO_MINIO]: Start");
        log.info("[UPLOAD_FILE_TO_MINIO]: Size file: {}", file.getSize());
        if(file.getSize() > MAX_SIZE) {
            log.error("[UPLOAD_FILE_TO_MINIO]: file size is too large");
            throw BaseException.badRequest(ErrorCode.MAX_SIZE_FILE).addProperty(new ErrorRecord("Dung lượng file vượt quá 5MB", "images")).build();
        }

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

    private void deleteFileToMinio(String url) {
        try {
            DeleteOptionDTO deleteOptionDTO = DeleteOptionDTO.builder()
                .uri(url)
                .isPublic(false)
                .build();
            minioOperations.remove(deleteOptionDTO);
            log.info("[DELETE_IMAGE_PACKAGE_PROFILE]: upload file successfully");
        } catch (Exception e) {
            log.error("DELETE_IMAGE_PACKAGE_PROFILE]: Cannot upload file to minio", e);
        }
    }
}

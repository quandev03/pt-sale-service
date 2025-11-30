package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.ContractData;
import com.vnsky.bcss.projectbase.domain.dto.ContractDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.RoomServiceDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.ContractServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OcrServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.ContractRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.RoomServiceRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateContractRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.ContractResponse;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.dto.UploadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContractService implements ContractServicePort {

    private final ContractRepoPort contractRepoPort;
    private final MinioOperations minioOperations;
    private final OcrServicePort ocrServicePort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;
    private final RoomServiceRepoPort roomServiceRepoPort;

    private static final String TEMPLATE_PATH = "/contract-pt/example-2.docx";
    private static final String CONTRACTS_BASE_PATH = "contracts";

    @Override
    @Transactional
    public ContractResponse createContract(CreateContractRequest request) {
        log.info("Creating new contract for organizationUnitId: {}", request.getOrganizationUnitId());

        // Get room information
        OrganizationUnitDTO room = organizationUnitRepoPort.findById(request.getOrganizationUnitId())
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                .message("Không tìm thấy thông tin phòng")
                .build());

        // Get owner information (parent of room)
        OrganizationUnitDTO owner = null;
        if (room.getParentId() != null) {
            owner = organizationUnitRepoPort.findById(room.getParentId())
                .orElse(null);
        }

        if (owner == null) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Không tìm thấy thông tin chủ trọ")
                .build();
        }

        // Get room services (WATER, ELECTRICITY, ROOM_RENT)
        List<RoomServiceDTO> roomServices = roomServiceRepoPort.findByOrgUnitId(request.getOrganizationUnitId());
        Map<RoomServiceType, RoomServiceDTO> serviceMap = roomServices.stream()
            .filter(service -> service.getServiceType() != null)
            .collect(Collectors.toMap(
                RoomServiceDTO::getServiceType,
                service -> service,
                (existing, replacement) -> existing
            ));

        // Fill ContractData with information from database
        ContractData contractData = fillContractDataFromDatabase(
            request.getContractData(),
            room,
            owner,
            serviceMap
        );

        log.info("Creating contract for owner: {}, tenant: {}", 
            contractData.getOwnerName(), 
            contractData.getTenantName());

        // Generate contract ID
        String contractId = UUID.randomUUID().toString();
        String contractPath = CONTRACTS_BASE_PATH + "/" + contractId;

        try {
            // Upload images to MinIO
            String frontImageUrl = uploadImage(request.getFrontImage(), contractPath + "/front.jpg");
            String backImageUrl = uploadImage(request.getBackImage(), contractPath + "/back.jpg");
            String portraitImageUrl = uploadImage(request.getPortraitImage(), contractPath + "/portrait.jpg");

            // Download template from MinIO
            DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
                .isPublic(false)
                .uri(TEMPLATE_PATH)
                .build();
            InputStream template = minioOperations.download(downloadOptionDTO).getInputStream();

            // Convert ContractData to Map
            Map<String, String> dataMap = convertContractDataToMap(contractData);

            // Generate PDF contract
            Resource pdfResource = ocrServicePort.genContract(template, dataMap);
            byte[] pdfBytes = pdfResource.getInputStream().readAllBytes();

            // Upload PDF to MinIO
            String pdfUrl = uploadPdf(new ByteArrayInputStream(pdfBytes), contractPath + "/contract.pdf");

            // Create ContractDTO
            ContractDTO contractDTO = createContractDTO(contractData, contractId, 
                frontImageUrl, backImageUrl, portraitImageUrl, pdfUrl);

            // Save to database
            ContractDTO savedContract = contractRepoPort.save(contractDTO);

            // Convert to response
            return convertToResponse(savedContract);
        } catch (Exception e) {
            log.error("Error creating contract: {}", e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Lỗi khi tạo hợp đồng: " + e.getMessage())
                .build();
        }
    }

    @Override
    public Resource genContract(CreateContractRequest request) throws Exception {
        log.info("Generating contract PDF for organizationUnitId: {}", request.getOrganizationUnitId());

        // Get room information
        OrganizationUnitDTO room = organizationUnitRepoPort.findById(request.getOrganizationUnitId())
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                .message("Không tìm thấy thông tin phòng")
                .build());

        // Get owner information (parent of room)
        OrganizationUnitDTO owner = null;
        if (room.getParentId() != null) {
            owner = organizationUnitRepoPort.findById(room.getParentId())
                .orElse(null);
        }

        if (owner == null) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Không tìm thấy thông tin chủ trọ")
                .build();
        }

        // Get room services (WATER, ELECTRICITY, ROOM_RENT)
        List<RoomServiceDTO> roomServices = roomServiceRepoPort.findByOrgUnitId(request.getOrganizationUnitId());
        Map<RoomServiceType, RoomServiceDTO> serviceMap = roomServices.stream()
            .filter(service -> service.getServiceType() != null)
            .collect(Collectors.toMap(
                RoomServiceDTO::getServiceType,
                service -> service,
                (existing, replacement) -> existing
            ));

        // Fill ContractData with information from database
        ContractData contractData = fillContractDataFromDatabase(
            request.getContractData(),
            room,
            owner,
            serviceMap
        );

        log.info("Generating contract PDF for owner: {}, tenant: {}", 
            contractData.getOwnerName(), 
            contractData.getTenantName());

        try {
            // Download template from MinIO
            DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
                .isPublic(false)
                .uri(TEMPLATE_PATH)
                .build();
            InputStream template = minioOperations.download(downloadOptionDTO).getInputStream();

            // Convert ContractData to Map
            Map<String, String> dataMap = convertContractDataToMap(contractData);

            // Generate PDF contract (không lưu vào DB, không upload lên MinIO)
            return ocrServicePort.genContract(template, dataMap);
        } catch (Exception e) {
            log.error("Error generating contract PDF: {}", e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Lỗi khi generate hợp đồng: " + e.getMessage())
                .build();
        }
    }

    @Override
    public Page<ContractResponse> listContracts(String ownerName, String tenantName, 
                                                 LocalDateTime fromDate, LocalDateTime toDate, 
                                                 Pageable pageable) {
        Page<ContractDTO> contracts = contractRepoPort.search(ownerName, tenantName, fromDate, toDate, pageable);
        return contracts.map(this::convertToResponse);
    }

    private String uploadImage(MultipartFile file, String path) {
        if (file == null || file.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("File ảnh không được để trống")
                .build();
        }

        try {
            UploadOptionDTO uploadOption = UploadOptionDTO.builder()
                .uri(path)
                .isPublic(false)
                .build();

            minioOperations.upload(file.getInputStream(), uploadOption);
            log.info("Image uploaded to MinIO: {}", path);
            return path;
        } catch (Exception e) {
            log.error("Error uploading image to MinIO: {}", e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Lỗi khi upload ảnh: " + e.getMessage())
                .build();
        }
    }

    private String uploadPdf(InputStream pdfStream, String path) {
        try {
            UploadOptionDTO uploadOption = UploadOptionDTO.builder()
                .uri(path)
                .isPublic(false)
                .build();

            minioOperations.upload(pdfStream, uploadOption);
            log.info("PDF uploaded to MinIO: {}", path);
            return path;
        } catch (Exception e) {
            log.error("Error uploading PDF to MinIO: {}", e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Lỗi khi upload PDF: " + e.getMessage())
                .build();
        }
    }

    private ContractData fillContractDataFromDatabase(
            ContractData requestData,
            OrganizationUnitDTO room,
            OrganizationUnitDTO owner,
            Map<RoomServiceType, RoomServiceDTO> serviceMap) {
        
        ContractData contractData = new ContractData();
        
        // Copy data from request (tenant info and other fields from FE)
        if (requestData != null) {
            // Tenant info from FE
            contractData.setTenantName(requestData.getTenantName());
            contractData.setTenantBirth(requestData.getTenantBirth());
            contractData.setTenantPermanentAddress(requestData.getTenantPermanentAddress());
            contractData.setTenantId(requestData.getTenantId());
            contractData.setTenantIdIssueDay(requestData.getTenantIdIssueDay());
            contractData.setTenantIdIssueMonth(requestData.getTenantIdIssueMonth());
            contractData.setTenantIdIssueYear(requestData.getTenantIdIssueYear());
            contractData.setTenantIdIssuePlace(requestData.getTenantIdIssuePlace());
            contractData.setTenantPhone(requestData.getTenantPhone());
            
            // Contract dates and other info from FE
            contractData.setContractLocation(requestData.getContractLocation());
            contractData.setCurrentDay(requestData.getCurrentDay());
            contractData.setCurrentMonth(requestData.getCurrentMonth());
            contractData.setCurrentYear(requestData.getCurrentYear());
            contractData.setStartDateDay(requestData.getStartDateDay());
            contractData.setStartDateMonth(requestData.getStartDateMonth());
            contractData.setStartYear(requestData.getStartYear());
            contractData.setEndDateDay(requestData.getEndDateDay());
            contractData.setEndDateMonth(requestData.getEndDateMonth());
            contractData.setEndYear(requestData.getEndYear());
            contractData.setNoticeDays(requestData.getNoticeDays());
            contractData.setPaymentMethod(requestData.getPaymentMethod());
            contractData.setDepositAmount(requestData.getDepositAmount());
        }
        
        // Fill owner info from database (parent organization unit)
        if (owner != null) {
            contractData.setOwnerName(owner.getOrgName() != null ? owner.getOrgName() : "");
            contractData.setOwnerId(owner.getCccd() != null ? owner.getCccd() : "");
            contractData.setOwnerPhone(owner.getPhone() != null ? owner.getPhone() : "");
            contractData.setOwnerPermanentAddress(owner.getAddress() != null ? owner.getAddress() : "");
            // Note: owner birth, id issue info may not be in OrganizationUnit, keep from request if available
            if (requestData != null) {
                contractData.setOwnerBirth(requestData.getOwnerBirth());
                contractData.setOwnerIdIssueDay(requestData.getOwnerIdIssueDay());
                contractData.setOwnerIdIssueMonth(requestData.getOwnerIdIssueMonth());
                contractData.setOwnerIdIssueYear(requestData.getOwnerIdIssueYear());
                contractData.setOwnerIdIssuePlace(requestData.getOwnerIdIssuePlace());
            }
        }
        
        // Fill room address
        contractData.setRoomAddress(room.getAddress() != null ? room.getAddress() : "");
        
        // Fill service prices from RoomService
        RoomServiceDTO waterService = serviceMap.get(RoomServiceType.WATER);
        RoomServiceDTO electricityService = serviceMap.get(RoomServiceType.ELECTRICITY);
        RoomServiceDTO roomRentService = serviceMap.get(RoomServiceType.ROOM_RENT);
        
        contractData.setWaterPrice(waterService != null && waterService.getPrice() != null 
            ? formatPrice(waterService.getPrice()) : "0");
        contractData.setElectricPrice(electricityService != null && electricityService.getPrice() != null 
            ? formatPrice(electricityService.getPrice()) : "0");
        contractData.setRentPrice(roomRentService != null && roomRentService.getPrice() != null 
            ? formatPrice(roomRentService.getPrice()) : "0");
        
        return contractData;
    }
    
    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0";
        }
        // Format as Vietnamese currency format (e.g., 1.000.000)
        return String.format("%,.0f", price.doubleValue()).replace(",", ".");
    }

    private Map<String, String> convertContractDataToMap(ContractData contractData) {
        Map<String, String> dataMap = new HashMap<>();

        // Thông tin chung
        dataMap.put("contractLocation", contractData.getContractLocation());
        dataMap.put("currentDay", contractData.getCurrentDay());
        dataMap.put("currentMonth", contractData.getCurrentMonth());
        dataMap.put("currentYear", contractData.getCurrentYear());

        // Bên A - Chủ nhà
        dataMap.put("ownerName", contractData.getOwnerName());
        dataMap.put("ownerBirth", contractData.getOwnerBirth());
        dataMap.put("ownerPermanentAddress", contractData.getOwnerPermanentAddress());
        dataMap.put("ownerId", contractData.getOwnerId());
        dataMap.put("ownerIdIssueDay", contractData.getOwnerIdIssueDay());
        dataMap.put("ownerIdIssueMonth", contractData.getOwnerIdIssueMonth());
        dataMap.put("ownerIdIssueYear", contractData.getOwnerIdIssueYear());
        dataMap.put("ownerIdIssuePlace", contractData.getOwnerIdIssuePlace());
        dataMap.put("ownerPhone", contractData.getOwnerPhone());

        // Bên B - Người thuê
        dataMap.put("tenantName", contractData.getTenantName());
        dataMap.put("tenantBirth", contractData.getTenantBirth());
        dataMap.put("tenantPermanentAddress", contractData.getTenantPermanentAddress());
        dataMap.put("tenantId", contractData.getTenantId());
        dataMap.put("tenantIdIssueDay", contractData.getTenantIdIssueDay());
        dataMap.put("tenantIdIssueMonth", contractData.getTenantIdIssueMonth());
        dataMap.put("tenantIdIssueYear", contractData.getTenantIdIssueYear());
        dataMap.put("tenantIdIssuePlace", contractData.getTenantIdIssuePlace());
        dataMap.put("tenantPhone", contractData.getTenantPhone());

        // Thông tin hợp đồng
        dataMap.put("address", contractData.getRoomAddress());
        dataMap.put("rent", contractData.getRentPrice());
        dataMap.put("paymentMethod", contractData.getPaymentMethod());
        dataMap.put("electricPrice", contractData.getElectricPrice());
        dataMap.put("waterPrice", contractData.getWaterPrice());
        dataMap.put("deposit", contractData.getDepositAmount());
        dataMap.put("startDateDay", contractData.getStartDateDay());
        dataMap.put("startDateMonth", contractData.getStartDateMonth());
        dataMap.put("startYear", contractData.getStartYear());
        dataMap.put("endDateDay", contractData.getEndDateDay());
        dataMap.put("endDateMonth", contractData.getEndDateMonth());
        dataMap.put("endYear", contractData.getEndYear());
        dataMap.put("noticeDays", contractData.getNoticeDays());

        return dataMap;
    }

    private ContractDTO createContractDTO(ContractData contractData, String contractId,
                                          String frontImageUrl, String backImageUrl, 
                                          String portraitImageUrl, String pdfUrl) {
        return ContractDTO.builder()
            .id(contractId)
            .contractLocation(contractData.getContractLocation())
            .currentDay(contractData.getCurrentDay())
            .currentMonth(contractData.getCurrentMonth())
            .currentYear(contractData.getCurrentYear())
            .ownerName(contractData.getOwnerName())
            .ownerBirth(contractData.getOwnerBirth())
            .ownerPermanentAddress(contractData.getOwnerPermanentAddress())
            .ownerId(contractData.getOwnerId())
            .ownerIdIssueDay(contractData.getOwnerIdIssueDay())
            .ownerIdIssueMonth(contractData.getOwnerIdIssueMonth())
            .ownerIdIssueYear(contractData.getOwnerIdIssueYear())
            .ownerIdIssuePlace(contractData.getOwnerIdIssuePlace())
            .ownerPhone(contractData.getOwnerPhone())
            .tenantName(contractData.getTenantName())
            .tenantBirth(contractData.getTenantBirth())
            .tenantPermanentAddress(contractData.getTenantPermanentAddress())
            .tenantId(contractData.getTenantId())
            .tenantIdIssueDay(contractData.getTenantIdIssueDay())
            .tenantIdIssueMonth(contractData.getTenantIdIssueMonth())
            .tenantIdIssueYear(contractData.getTenantIdIssueYear())
            .tenantIdIssuePlace(contractData.getTenantIdIssuePlace())
            .tenantPhone(contractData.getTenantPhone())
            .roomAddress(contractData.getRoomAddress())
            .rentPrice(contractData.getRentPrice())
            .paymentMethod(contractData.getPaymentMethod())
            .electricPrice(contractData.getElectricPrice())
            .waterPrice(contractData.getWaterPrice())
            .depositAmount(contractData.getDepositAmount())
            .startDateDay(contractData.getStartDateDay())
            .startDateMonth(contractData.getStartDateMonth())
            .startYear(contractData.getStartYear())
            .endDateDay(contractData.getEndDateDay())
            .endDateMonth(contractData.getEndDateMonth())
            .endYear(contractData.getEndYear())
            .noticeDays(contractData.getNoticeDays())
            .frontImageUrl(frontImageUrl)
            .backImageUrl(backImageUrl)
            .portraitImageUrl(portraitImageUrl)
            .contractPdfUrl(pdfUrl)
            .build();
    }

    private ContractResponse convertToResponse(ContractDTO dto) {
        return ContractResponse.builder()
            .id(dto.getId())
            .contractLocation(dto.getContractLocation())
            .currentDay(dto.getCurrentDay())
            .currentMonth(dto.getCurrentMonth())
            .currentYear(dto.getCurrentYear())
            .ownerName(dto.getOwnerName())
            .ownerBirth(dto.getOwnerBirth())
            .ownerPermanentAddress(dto.getOwnerPermanentAddress())
            .ownerId(dto.getOwnerId())
            .ownerIdIssueDay(dto.getOwnerIdIssueDay())
            .ownerIdIssueMonth(dto.getOwnerIdIssueMonth())
            .ownerIdIssueYear(dto.getOwnerIdIssueYear())
            .ownerIdIssuePlace(dto.getOwnerIdIssuePlace())
            .ownerPhone(dto.getOwnerPhone())
            .tenantName(dto.getTenantName())
            .tenantBirth(dto.getTenantBirth())
            .tenantPermanentAddress(dto.getTenantPermanentAddress())
            .tenantId(dto.getTenantId())
            .tenantIdIssueDay(dto.getTenantIdIssueDay())
            .tenantIdIssueMonth(dto.getTenantIdIssueMonth())
            .tenantIdIssueYear(dto.getTenantIdIssueYear())
            .tenantIdIssuePlace(dto.getTenantIdIssuePlace())
            .tenantPhone(dto.getTenantPhone())
            .roomAddress(dto.getRoomAddress())
            .rentPrice(dto.getRentPrice())
            .paymentMethod(dto.getPaymentMethod())
            .electricPrice(dto.getElectricPrice())
            .waterPrice(dto.getWaterPrice())
            .depositAmount(dto.getDepositAmount())
            .startDateDay(dto.getStartDateDay())
            .startDateMonth(dto.getStartDateMonth())
            .startYear(dto.getStartYear())
            .endDateDay(dto.getEndDateDay())
            .endDateMonth(dto.getEndDateMonth())
            .endYear(dto.getEndYear())
            .noticeDays(dto.getNoticeDays())
            .frontImageUrl(dto.getFrontImageUrl())
            .backImageUrl(dto.getBackImageUrl())
            .portraitImageUrl(dto.getPortraitImageUrl())
            .contractPdfUrl(dto.getContractPdfUrl())
            .createdBy(dto.getCreatedBy())
            .createdDate(dto.getCreatedDate())
            .modifiedBy(dto.getModifiedBy())
            .modifiedDate(dto.getModifiedDate())
            .build();
    }
}


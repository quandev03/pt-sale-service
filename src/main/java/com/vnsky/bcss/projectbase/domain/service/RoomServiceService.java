package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.RoomServiceDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.RoomServiceServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.RoomServiceRepoPort;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomServiceType;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceService implements RoomServiceServicePort {

    private final RoomServiceRepoPort roomServiceRepoPort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;

    private static final String LOG_PREFIX = "[RoomServiceService]_";

    @Override
    @Transactional
    public RoomServiceDTO create(RoomServiceDTO dto) {
        log.info("{}create room service: {}", LOG_PREFIX, dto);

        // Validate organization unit exists
        OrganizationUnitDTO orgUnit = organizationUnitRepoPort.get(dto.getOrgUnitId());
        if (orgUnit == null) {
            log.error("{}Organization unit not found: {}", LOG_PREFIX, dto.getOrgUnitId());
            throw BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED).build();
        }

        String clientId = SecurityUtil.getCurrentClientId();
        dto.setClientId(clientId);

        // Validate and set service code and name based on service type
        validateAndSetServiceCodeAndName(dto, null);

        // Check if service code already exists for this client
        Optional<RoomServiceDTO> existing = roomServiceRepoPort.findByClientIdAndServiceCode(clientId, dto.getServiceCode());
        if (existing.isPresent()) {
            log.error("{}Service code already exists: {} for clientId: {}", LOG_PREFIX, dto.getServiceCode(), clientId);
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Mã dịch vụ đã tồn tại cho đối tác này")
                .build();
        }

        // Set default status if not provided
        if (dto.getStatus() == null) {
            dto.setStatus(Status.ACTIVE.getValue());
        }

        return roomServiceRepoPort.save(dto);
    }

    @Override
    @Transactional
    public RoomServiceDTO update(String id, RoomServiceDTO dto) {
        log.info("{}update room service id: {}, dto: {}", LOG_PREFIX, id, dto);

        // Check if room service exists
        RoomServiceDTO existing = roomServiceRepoPort.findById(id)
            .orElseThrow(() -> {
                log.error("{}Room service not found: {}", LOG_PREFIX, id);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Dịch vụ phòng không tồn tại")
                    .build();
            });

        // Validate organization unit exists if changed
        if (dto.getOrgUnitId() != null && !dto.getOrgUnitId().equals(existing.getOrgUnitId())) {
            OrganizationUnitDTO orgUnit = organizationUnitRepoPort.get(dto.getOrgUnitId());
            if (orgUnit == null) {
                log.error("{}Organization unit not found: {}", LOG_PREFIX, dto.getOrgUnitId());
                throw BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED).build();
            }
        }

        String clientId = SecurityUtil.getCurrentClientId();

        // Validate and set service code and name based on service type
        validateAndSetServiceCodeAndName(dto, existing);

        // Check if service code already exists for another service
        if (dto.getServiceCode() != null && !dto.getServiceCode().equals(existing.getServiceCode())) {
            Optional<RoomServiceDTO> duplicate = roomServiceRepoPort.findByClientIdAndServiceCodeExcludingId(
                clientId, dto.getServiceCode(), id);
            if (duplicate.isPresent()) {
                log.error("{}Service code already exists: {} for clientId: {}", LOG_PREFIX, dto.getServiceCode(), clientId);
                throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                    .message("Mã dịch vụ đã tồn tại cho đối tác này")
                    .build();
            }
        }

        // Update fields
        dto.setId(id);
        dto.setClientId(clientId);
        if (dto.getStatus() == null) {
            dto.setStatus(existing.getStatus());
        }

        return roomServiceRepoPort.update(dto);
    }

    @Override
    public RoomServiceDTO getById(String id) {
        return roomServiceRepoPort.findById(id)
            .orElseThrow(() -> {
                log.error("{}Room service not found: {}", LOG_PREFIX, id);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Dịch vụ phòng không tồn tại")
                    .build();
            });
    }

    @Override
    public List<RoomServiceDTO> getAll(String orgUnitId, RoomServiceType serviceType, Integer status) {
        String clientId = SecurityUtil.getCurrentClientId();
        return roomServiceRepoPort.findByFilters(clientId, orgUnitId, serviceType, status);
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.info("{}delete room service id: {}", LOG_PREFIX, id);

        roomServiceRepoPort.findById(id)
            .orElseThrow(() -> {
                log.error("{}Room service not found: {}", LOG_PREFIX, id);
                return BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                    .message("Dịch vụ phòng không tồn tại")
                    .build();
            });

        roomServiceRepoPort.delete(id);
        log.info("{}Room service deleted: {}", LOG_PREFIX, id);
    }

    private void validateAndSetServiceCodeAndName(RoomServiceDTO dto, RoomServiceDTO existing) {
        RoomServiceType serviceType = dto.getServiceType();
        if (serviceType == null) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Loại dịch vụ không được để trống")
                .build();
        }

        // For ELECTRICITY, WATER, INTERNET, ROOM_RENT: auto-generate code and name
        if (serviceType == RoomServiceType.ELECTRICITY ||
            serviceType == RoomServiceType.WATER ||
            serviceType == RoomServiceType.INTERNET ||
            serviceType == RoomServiceType.ROOM_RENT) {

            dto.setServiceCode(serviceType.getCode());
            dto.setServiceName(serviceType.getServiceName());

        } else if (serviceType == RoomServiceType.OTHER) {
            // For OTHER: user must provide code and name
            if (StringUtils.isBlank(dto.getServiceCode())) {
                throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                    .message("Mã dịch vụ không được để trống cho loại dịch vụ 'Khác'")
                    .build();
            }
            if (StringUtils.isBlank(dto.getServiceName())) {
                throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                    .message("Tên dịch vụ không được để trống cho loại dịch vụ 'Khác'")
                    .build();
            }
        }

        // Validate price
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE)
                .message("Giá dịch vụ phải lớn hơn 0")
                .build();
        }
    }
}


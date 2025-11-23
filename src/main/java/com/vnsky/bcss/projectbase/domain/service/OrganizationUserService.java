package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUserServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateOrganizationUserRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationCurrentResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationDTOResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.UserInfoResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.utils.StringUtilsOCR;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationUserService implements OrganizationUserServicePort {
    private final OrganizationUserRepoPort organizationUserRepoPort;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;

    @Override
    @Transactional
    public OrganizationUserDTO save(OrganizationUserDTO organizationUserDTO) {
        return organizationUserRepoPort.save(organizationUserDTO);
    }

    @Override
    @Transactional
    public OrganizationUserDTO savePrivate(OrganizationUserDTO organizationUserDTO) {
        OrganizationUnitDTO organizationUnitPartner = organizationUnitRepoPort.getOrgByClientIdAndOrgType(organizationUserDTO.getClientId(), Constant.OrgType.PARTNER);
        if (Objects.isNull(organizationUnitPartner)) {
            throw BaseException.badRequest(ErrorCode.ORG_NOT_EXISTED).build();
        }

        OrganizationUnitDTO organizationUnitNBO = organizationUnitRepoPort.findByCodeAndType(organizationUnitPartner.getOrgCode(), Constant.OrgType.NBO);
        organizationUserDTO.setOrgId(organizationUnitNBO.getId());
        return organizationUserRepoPort.save(organizationUserDTO);
    }

    @Override
    public List<OrganizationUserDTO> getAllOrganizationUserByUnit(String id) {
        return organizationUserRepoPort.getAllOrganizationUserByOrgId(id);
    }

    @Override
    public Page<OrganizationUserDTO> getByUnit(String username, String unitID, Integer page, Integer size) {
        if (page == null || size == null) {
            return organizationUserRepoPort.getByUnit(username, unitID);
        } else {
            return organizationUserRepoPort.getByUnit(username, unitID, PageRequest.of(page, size));
        }
    }

    @Override
    @Transactional
    public List<OrganizationUserDTO> saveOrganizationUser(CreateOrganizationUserRequest request) {
        this.organizationUserRepoPort.deleteByUserId(request.getUserId());

        if (!CollectionUtils.isEmpty(request.getOrganizationIds())) {
            AtomicInteger index = new AtomicInteger(0);
            List<OrganizationUserDTO> organizationUserDTOS = request.getOrganizationIds().stream()
                .map(organizationId -> OrganizationUserDTO.builder()
                    .orgId(organizationId)
                    .userId(request.getUserId())
                    .userName(request.getUsername())
                    .userFullname(request.getUserFullName())
                    .clientId(request.getClientId())
                    .isCurrent(index.getAndIncrement())
                    .email(request.getEmail())
                    .status(request.getStatus())
                    .build()).collect(Collectors.toList());
            return this.organizationUserRepoPort.saveAllAndFlush(organizationUserDTOS);
        }

        return List.of();
    }

    @Override
    public List<OrganizationUserDTO> findByUserId(String userId) {
        return this.organizationUserRepoPort.findOrgByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserIdIn(List<String> userIds) {
        this.organizationUserRepoPort.deleteByUserIdIn(userIds);
    }

    @Override
    public List<OrganizationCurrentResponse> findByUserIdCurrent() {
        return this.organizationUserRepoPort.getOrganizationUser(SecurityUtil.getCurrentUserId());
    }

    @Override
    public OrganizationDTOResponse getOrganizationUserIdCurrent() {
        return this.organizationUserRepoPort.getOrganizationUserIdCurrent(SecurityUtil.getCurrentUserId());
    }

    @Override
    public Page<UserInfoResponse> getUserVnSky(String q, Pageable pageable) {
        return this.organizationUserRepoPort.getUserByClientId(StringUtilsOCR.buildLikeOperator(q), Constant.VNSKY_CLIENT_ID, pageable);
    }

    @Override
    public boolean existByUserIdAndOrgId(String userId, String orgId) {
        return organizationUserRepoPort.existsByUserIdAndOrgId(userId, orgId);
    }

    @Override
    public List<OrganizationDTOResponse> getAllOrganizationUserIdCurrent() {
        OrganizationUserDTO organizationUserDTO = this.organizationUserRepoPort.findByUserIdAndIsCurrent(SecurityUtil.getCurrentUserId(), 1);
        if (Objects.isNull(organizationUserDTO)) {
            return Collections.emptyList();
        }

        String clientId = SecurityUtil.getCurrentClientId();
        return organizationUserRepoPort.findAllChildOrgById(organizationUserDTO.getOrgId(), clientId);
    }

    @Override
    public List<UserInfoResponse> getUnitByOrgIdentity(String orgCode) {
        OrganizationUnitDTO organizationUnitDTO =  this.organizationUnitRepoPort.getUnitByOrgIdentity(orgCode, Constant.VNSKY_CLIENT_ID);
        if (organizationUnitDTO == null || organizationUnitDTO.getEmail() == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(organizationUnitDTO.getEmail().split(Constant.COMMA))
            .map(email -> UserInfoResponse.builder()
                .email(email)
                .orgName(organizationUnitDTO.getOrgName())
                .username(email)
                .fullName(email)
                .build())
            .toList();
    }

    @Override
    public void updateOrgUnit(String userId, String orgId) {
        organizationUserRepoPort.updateOrganizationUnit(userId, orgId);
    }

    @Override
    @Transactional
    public OrganizationUserDTO update(OrganizationUserDTO organizationUserDTO) {
        log.info("update organization user: {}", organizationUserDTO);
        // Kiểm tra organization user có tồn tại không
        OrganizationUserDTO existing = organizationUserRepoPort.findById(organizationUserDTO.getUserId())
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED)
                .message("Không tìm thấy người dùng với ID: " + organizationUserDTO.getId())
                .build());

        // Cập nhật các trường được phép
        if (organizationUserDTO.getOrgId() != null) {
            existing.setOrgId(organizationUserDTO.getOrgId());
        }
        if (organizationUserDTO.getUserId() != null) {
            existing.setUserId(organizationUserDTO.getUserId());
        }
        if (organizationUserDTO.getUserName() != null) {
            existing.setUserName(organizationUserDTO.getUserName());
        }
        if (organizationUserDTO.getUserFullname() != null) {
            existing.setUserFullname(organizationUserDTO.getUserFullname());
        }
        if (organizationUserDTO.getEmail() != null) {
            existing.setEmail(organizationUserDTO.getEmail());
        }
        if (organizationUserDTO.getStatus() != null) {
            existing.setStatus(organizationUserDTO.getStatus());
        }
        if (organizationUserDTO.getIsCurrent() != null) {
            existing.setIsCurrent(organizationUserDTO.getIsCurrent());
        }

        return organizationUserRepoPort.save(existing);
    }
}

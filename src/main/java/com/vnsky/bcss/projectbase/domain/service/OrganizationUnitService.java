package com.vnsky.bcss.projectbase.domain.service;


import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.dto.UserDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitImageServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.OrganizationUnitServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.PackageManagerServicePort;
import com.vnsky.bcss.projectbase.domain.port.primary.StockIsdnServicePort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.CheckOrgParentRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.CheckOrgParentResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.DebtRevenueResponse;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomRentalStatus;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.StockSerialType;
import com.vnsky.bcss.projectbase.shared.utils.MessageSourceUtils;
import com.vnsky.common.dto.ErrorRecord;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.common.exception.domain.ErrorKey;
import com.vnsky.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationUnitService implements OrganizationUnitServicePort {
    private final OrganizationUnitRepoPort organizationUnitRepositoryPort;
    private final OrganizationUserRepoPort organizationUserRepositoryPort;
    private final StockIsdnServicePort stockIsdnServicePort;
    private final PackageManagerServicePort packageManagerServicePort;
    private final OrganizationUnitImageServicePort imageServicePort;

    private static final String LOG_PREFIX = "[OrganizationUnitService]_";

    @Override
    @Transactional
    public OrganizationUnitDTO save(OrganizationUnitDTO organizationUnitDTO, String id) {
        // Kiểm tra mã đơn vị
        OrganizationUnitDTO orgDTO = this.organizationUnitRepositoryPort.getByOrgCode(id, organizationUnitDTO.getOrgCode());
        if (!Objects.isNull(orgDTO)) {
            log.error("{}save orgCode : {} existed", LOG_PREFIX, orgDTO.getOrgCode());
            throw BaseException.badRequest(ErrorCode.VALIDATION_ERROR_CODE).
                addProperty(
                    new ErrorRecord(
                        MessageSourceUtils.getMessageDetail(ErrorCode.ORG_CODE_EXISTED.get()),
                        OrganizationUnitDTO.Fields.orgCode
                    )
                )
                .build();
        }

            // kiểm tra Id có tồn tại trong Db không
            orgDTO = this.organizationUnitRepositoryPort.getRootOrg(SecurityUtil.getCurrentClientId());

            if (Objects.isNull(orgDTO)) {
                log.error("{}save org unit not found with id : {}", LOG_PREFIX, id);
                throw BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED).build();
            }

            if (orgDTO.getStatus().equals(Constant.Status.ACTIVE) && organizationUnitDTO.getStatus().equals(Constant.Status.IN_ACTIVE)) {
                // Kiểm tra có đơn vị con nào đang hoạt động không
                int countActive = this.organizationUnitRepositoryPort.countActiveUnitAll(id, Constant.Status.ACTIVE, orgDTO.getOrgType());
                if (countActive > 0) {
                    log.error("{}save child org unit is active cannot edit", LOG_PREFIX);
                    throw BaseException.bussinessError(ErrorCode.CHILD_ACTIVE_CANNOT_EDIT).build();
                }
            }

        // Nếu thêm mới đơn vị gốc => Kiểm tra đã có đơn vị gốc tồn tại chưa


        // Set orgType cho danh mục đơn vị
        organizationUnitDTO.setOrgType(Constant.OrgType.NBO);
        organizationUnitDTO.setClientId(SecurityUtil.getCurrentClientId());
        organizationUnitDTO.setParentCode(orgDTO.getOrgCode());
        organizationUnitDTO.setParentId(orgDTO.getId());
        checkDuplicate(organizationUnitDTO);
        organizationUnitDTO.setProvinceCode(orgDTO.getProvinceCode());
        organizationUnitDTO.setWardCode(orgDTO.getWardCode());

        if (Objects.isNull(id)) {
            // Thêm mới
            return this.organizationUnitRepositoryPort.save(organizationUnitDTO);
        } else {
            // Cập nhật
            organizationUnitDTO.setId(id);
            return this.organizationUnitRepositoryPort.update(organizationUnitDTO);
        }
    }

    private void checkDuplicate(OrganizationUnitDTO organizationUnitDTO) {
        if (!StringUtils.isBlank(organizationUnitDTO.getDeliveryAreas()) && !StringUtils.isBlank(organizationUnitDTO.getSaleChanel())) {
            List<String> listDeliveryAreas = Arrays.asList(organizationUnitDTO.getDeliveryAreas().split(","));
            List<String> listSaleChanel = Arrays.asList(organizationUnitDTO.getSaleChanel().split(","));
            listSaleChanel.forEach(e -> {
                long count = this.organizationUnitRepositoryPort.countExitsDeliveryAreas(listDeliveryAreas, e ,organizationUnitDTO.getId());
                if (count > 0) {
                    log.error("duplicate SaleChanel {}", e);
                    throw BaseException.conflictError(ErrorCode.SALES_CHANNELS_EXISTED).build();
                }
            });
        }
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getPartnersWithoutOrganizationLimit() {
        return organizationUnitRepositoryPort.getPartnersWithoutOrganizationLimit();
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getAll(Integer status, String orgType, String orgSubType, String textSearch, String rentalStatus) {
        return this.organizationUnitRepositoryPort.getAllOrganizationUnits(status, orgType, orgSubType, textSearch, rentalStatus);
    }

    @Override
    @Transactional
    public OrganizationUnitDTO get(String id) {
        OrganizationUnitDTO organizationUnitDTO = this.organizationUnitRepositoryPort.get(id);

        if (Objects.isNull(organizationUnitDTO)) {
            log.error("{}get org unit not found with id : {}", LOG_PREFIX, id);
            throw BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED).build();
        }

        // Load images
        organizationUnitDTO.setImageUrls(imageServicePort.getImageUrls(id));

        return organizationUnitDTO;
    }

    @Override
    @Transactional
    public void deleteOrganizationUnit(String id) {
        OrganizationUnitDTO organizationUnitDTO = this.organizationUnitRepositoryPort.get(id);

        if (Objects.isNull(organizationUnitDTO)) {
            log.error("{}deleteOrganizationUnit org unit not found with id : {}", LOG_PREFIX, id);
            throw BaseException.notFoundError(ErrorCode.ORG_NOT_EXISTED).build();
        }

        // Kiểm tra có đơn vị nào đang hoạt động không
        int countActive = this.organizationUnitRepositoryPort.countChildUnit(id);
        if (countActive > 0) {
            log.error("{}deleteOrganizationUnit have child org unit => cannot delete", LOG_PREFIX);
            throw BaseException.bussinessError(ErrorCode.CHILD_ACTIVE_CANNOT_DELETE).build();
        }

        try {
            this.organizationUnitRepositoryPort.deleteAndFlush(id);
        } catch (DataIntegrityViolationException e) {
            throw BaseException.bussinessError(ErrorCode.UNIT_CANNOT_DELETE).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BaseException.internalErrorDefaultMessage(e);
        }
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getAllStores(Boolean isAll) {
        Integer status = Boolean.TRUE.equals(isAll) ? null : Constant.Status.ACTIVE;
        return this.organizationUnitRepositoryPort.getAllOrganizationUnitByStatusAndTypeAndClient(status, Constant.OrgType.NBO, StockSerialType.STORE.getValue(), Constant.VNSKY_CLIENT_ID);
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getActivedAuthorizedOrg(String orgSubType) {
        return organizationUnitRepositoryPort.getActivedAuthorizedOrg(orgSubType);
    }

    @Override
    public CheckOrgParentResponse checkOrgParent(CheckOrgParentRequest request) {
        log.debug("[CHECK_ORG_PARENT]: Start");
        log.info("[CHECK_ORG_PARENT]: request = {}", request);
//        OrganizationUserDTO currentUser = organizationUserRepositoryPort.findByUserId(request.getCurrentUserId()).orElseThrow(()-> BaseException.badRequest(ErrorKey.BAD_REQUEST).build());
//
//        int resultCheck = organizationUnitRepositoryPort.checkChildAndParent(currentUser.getOrgId(), request.getOrgId());

//        log.info("Response: {}", resultCheck);
        return CheckOrgParentResponse.builder()
            .result(1)
            .build();
    }

    @Override
    public List<OrganizationUnitDTO> getChild(String parentId) {
        return organizationUnitRepositoryPort.getChildUnits(parentId);
    }

    @Override
    public List<UserDTO> mapUsersWithOrg(String userId, String clientId, String currentClientId, List<UserDTO> users) {
        List<UserDTO> input = Optional.ofNullable(users).orElse(Collections.emptyList());
        if (input.isEmpty()) {
            return Collections.emptyList();
        }

        if(clientId == null || clientId.isEmpty() || currentClientId == null || currentClientId.isEmpty()) {
            throw BaseException.notFoundError(ErrorCode.MISSING_CLIENT_ID).build();
        }

        log.info("User ID: {}, Client ID: {}", userId, clientId);

        String currentOrgId;
        if(!currentClientId.equals(Constant.VNSKY_CLIENT_ID)) {
            currentOrgId = organizationUserRepositoryPort.findByUserId(userId)
                .orElseThrow(() -> BaseException.bussinessError(ErrorCode.ORG_ID_NOT_FOUND).build())
                .getOrgId();
        } else {
            currentOrgId = organizationUnitRepositoryPort.getDetailPartnerByClientId(clientId, Constant.OrgType.NBO).getId();
        }

        Map<String, String> userIdToOrgName = organizationUserRepositoryPort.getChildUser(currentOrgId)
            .stream()
            .filter(Objects::nonNull)
            .filter(u -> u.getId() != null && u.getOrgName() != null)
            .collect(Collectors.toMap(UserDTO::getId, UserDTO::getOrgName, (a, b) -> a));

        List<UserDTO> result = input.stream()
            .filter(Objects::nonNull)
            .filter(u -> userIdToOrgName.containsKey(u.getId()))
            .map(u -> {
                u.setOrgName(userIdToOrgName.get(u.getId()));
                return u;
            })
            .toList();

        log.info("[MAP_USERS_WITH_ORG]: mapped {} of {} users", result.size(), input.size());
        return result;
    }

    @Override
    public OrganizationUnitDTO getInfoOrgUnit(String orgId) {
        log.debug("[GET_INFO_ORG_UNIT]: Start, org id: {}", orgId);
        return organizationUnitRepositoryPort.get(orgId);
    }

    @Override
    public OrganizationUnitDTO getOrgCurrent() {
        log.info("user id: {}", SecurityUtil.getCurrentUserId());
        OrganizationUnitDTO organizationUnitDTO = organizationUnitRepositoryPort.findByUserId(SecurityUtil.getCurrentUserId());
        if (organizationUnitDTO == null) {
            throw BaseException.bussinessError(ErrorCode.ORG_UNIT_NOT_EXISTED_BY_USER).build();
        }
        return organizationUnitDTO;
    }

    @Override
    public DebtRevenueResponse getDebtRevenue() {
        OrganizationUnitDTO organizationUnitDTO = getOrgCurrent();
        Long revenus = this.packageManagerServicePort.revenusPackageSold(organizationUnitDTO.getOrgCode());
        return new DebtRevenueResponse(organizationUnitDTO.getDebtLimit(), organizationUnitDTO.getDebtLimitMbf(), revenus);
    }

    @Override
    public List<OrganizationUnitDTO> getAvailableRooms() {
        log.info("{}Getting available rooms", LOG_PREFIX);
        return organizationUnitRepositoryPort.findByRentalStatus(RoomRentalStatus.AVAILABLE);
    }
}

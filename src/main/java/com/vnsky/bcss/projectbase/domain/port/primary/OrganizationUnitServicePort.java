package com.vnsky.bcss.projectbase.domain.port.primary;


import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.partner.CheckOrgParentRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.CheckOrgParentResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.domain.dto.UserDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.partner.DebtRevenueResponse;

import java.util.List;

public interface OrganizationUnitServicePort {
    OrganizationUnitDTO save(OrganizationUnitDTO organizationUnitDTO, String id, boolean isUpdate);
    List<GetAllOrganizationUnitResponse> getPartnersWithoutOrganizationLimit();
    List<GetAllOrganizationUnitResponse> getAll(Integer status, String orgType, String orgSubType, String textSearch, String rentalStatus);

    OrganizationUnitDTO get(String id);

    void deleteOrganizationUnit(String id);

    List<GetAllOrganizationUnitResponse> getAllStores(Boolean isAll);

    List<GetAllOrganizationUnitResponse> getActivedAuthorizedOrg(String orgSubType);

    CheckOrgParentResponse checkOrgParent(CheckOrgParentRequest request);

    List<OrganizationUnitDTO> getChild(String parentId);

    List<UserDTO> mapUsersWithOrg(String userId, String clientId, String currentClientId, List<UserDTO> users);

    OrganizationUnitDTO getInfoOrgUnit(String orgId);

    OrganizationUnitDTO getOrgCurrent();

    DebtRevenueResponse getDebtRevenue();

    List<OrganizationUnitDTO> getAvailableRooms();

    List<OrganizationUnitDTO> getAvailableRoomsWithFilters(
        String provinceCode,
        String wardCode,
        Long minAcreage,
        Long maxAcreage);
}

package com.vnsky.bcss.projectbase.domain.port.secondary;


import com.vnsky.bcss.projectbase.domain.dto.OrganizationDeliveryInfoDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchPartnerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrganizationUnitRepoPort {
    OrganizationUnitDTO save(OrganizationUnitDTO organizationUnitEntity);

    OrganizationUnitDTO update(OrganizationUnitDTO organizationUnitEntity);

    OrganizationUnitDTO updatePartner(OrganizationUnitDTO organizationUnitDTO);

    Optional<OrganizationUnitDTO> findById(String id);

    Optional<OrganizationUnitDTO> findByCode(String code);

    void delete(String id);

    void deleteAndFlush(String id);

    OrganizationUnitDTO getByOrgCode(String id, String code);

    OrganizationUnitDTO get(String id);

    OrganizationUnitDTO getOrgRoot(String id);

    List<GetAllOrganizationUnitResponse> getPartnersWithoutOrganizationLimit();

    List<GetAllOrganizationUnitResponse> getAllOrganizationUnits(Integer status, String orgType, String orgSubType, String textSearch, String currentOrgId);

    int countActiveUnit(String id, Integer status);

    int countActiveUnitAll(String id, Integer status, String orgType);

    int countChildUnit(String id);

    boolean existsByCode(String id, String code, String type);

    boolean existsById(String id);

    boolean existsByIdAndOrgType(String id, String orgType);

    Page<SearchPartnerResponse> searchPartner(String q, String partnerType, Integer status, Integer approvalStatus, Pageable pageable);

    void updateStatus(String id, Integer status);

    OrganizationUnitDTO getDetailPartnerByClientId(String clientId, String partner);

    OrganizationUnitDTO getOrgRootByClientId(String clientId);

    OrganizationUnitDTO getOrgRootByClientIdAndOrgType(String clientId, String orgType);

    OrganizationUnitDTO getOrgByClientIdAndOrgType(String clientId, String orgType);

    Long getRootOrgByClientId(String clientId);

    List<OrganizationUnitDTO> getAllById(List<Long> orgIds);

    List<OrganizationUnitDTO> getAllByOrgType(String value);

    OrganizationUnitDTO findByCodeAndType(String orgCode, String type);

    OrganizationDeliveryInfoDTO findDeliveryByOrgId(String id);

    List<GetAllOrganizationUnitResponse> getAllUnitByClientId(String orgCode, String currentClientId);

    List<GetAllOrganizationUnitResponse> getAllOrganizationByType(String q, String orgType);

    OrganizationUnitDTO getCurrentPartnerByUserIdAndClientId(String userId, String clientId);

    OrganizationDeliveryInfoDTO saveDeliveryInfo(OrganizationDeliveryInfoDTO organizationDeliveryInfoDTO);

    void updateApprovalStatus(String id, Integer status);

    void updateClientId(String id, String clientId);

    OrganizationUnitDTO getUnitByOrgIdentity(String orgCode, String clientId);

    long countExitsDeliveryAreas(List<String> listDeliveryAreas, String web, String id);

    List<GetAllOrganizationUnitResponse> getAllOrganizationUnitByStatusAndTypeAndClient(Integer status, String orgType, String orgSubType, String clientId);

    List<GetAllOrganizationUnitResponse> getActivedAuthorizedOrg(String orgSubType);

    List<OrganizationUnitResponse> getListOrganization();

    int checkChildAndParent(String parentId, String childId);

    List<OrganizationUnitDTO> getChildUnits(String parentId);

    List<OrganizationUnitResponse> getListOrganizationUnitChild(String parentId);

    OrganizationUnitDTO findByUserId(String userId);

    OrganizationUnitDTO getOrgNBOByPartner(String partnerID);
}

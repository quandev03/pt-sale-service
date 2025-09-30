package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.dto.UserDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationCurrentResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationDTOResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrganizationUserRepoPort {
    Optional<OrganizationUserDTO> findByUserId(String userId);

    OrganizationUserDTO save(OrganizationUserDTO organizationUserDTO);

    List<OrganizationUserDTO> findOrgByUserId(String userId);

    List<OrganizationUserDTO> getAllOrganizationUserByOrgId(String id);

    Page<OrganizationUserDTO> getByUnit(String username, String unitID);

    Page<OrganizationUserDTO> getByUnit(String username, String unitID, Pageable page);

    void deleteByUserId(String userId);

    List<OrganizationUserDTO> saveAllAndFlush(List<OrganizationUserDTO> organizationUserDTOS);

    void deleteByUserIdIn(List<String> userIds);

    List<OrganizationCurrentResponse> getOrganizationUser(String currentUserId);

    OrganizationDTOResponse getOrganizationUserIdCurrent(String currentUserId);

    Page<UserInfoResponse> getUserByClientId(String q, String clientId, Pageable pageable);

    boolean existsByUserIdAndOrgId(String userId, String orgId);

    OrganizationUserDTO findByUserIdAndIsCurrent(String currentUserId, Integer aTrue);

    List<OrganizationDTOResponse> findAllChildOrgById(String orgId, String clientId);

    String getOrganizationUnitNameByUserId(String userId);

    void updateOrganizationUnit(String userId, String orgId);

    List<UserDTO> getChildUser(String parentId);
}

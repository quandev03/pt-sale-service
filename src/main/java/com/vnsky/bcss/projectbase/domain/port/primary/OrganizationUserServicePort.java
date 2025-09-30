package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.request.CreateOrganizationUserRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationCurrentResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationDTOResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrganizationUserServicePort {
    OrganizationUserDTO save(OrganizationUserDTO organizationUserDTO);

    OrganizationUserDTO savePrivate(OrganizationUserDTO organizationUserDTO);

    List<OrganizationUserDTO> getAllOrganizationUserByUnit(String i);

    Page<OrganizationUserDTO> getByUnit(String username, String unitID, Integer page, Integer size);

    List<OrganizationUserDTO> saveOrganizationUser(CreateOrganizationUserRequest request);

    List<OrganizationUserDTO> findByUserId(String userId);

    void deleteByUserIdIn(List<String> userId);

    List<OrganizationCurrentResponse> findByUserIdCurrent();

    OrganizationDTOResponse getOrganizationUserIdCurrent();

    Page<UserInfoResponse> getUserVnSky(String q, Pageable pageable);

    boolean existByUserIdAndOrgId(String userId, String orgId);

    List<OrganizationDTOResponse> getAllOrganizationUserIdCurrent();

    List<UserInfoResponse> getUnitByOrgIdentity(String orgCode);

    void updateOrgUnit(String userId, String orgId);
}

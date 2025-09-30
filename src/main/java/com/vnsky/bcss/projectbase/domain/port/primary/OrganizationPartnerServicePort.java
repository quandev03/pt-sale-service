package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.*;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PackageClientRequest;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchPartnerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface OrganizationPartnerServicePort {

    OrganizationUnitDTO createPartner(OrganizationUnitDTO organizationUnitDTO, String acceptLanguage);

    OrganizationUnitDTO updatePartner(String id, OrganizationUnitDTO organizationUnitDTO);

    OrganizationUnitDTO getDetailById(String id);

    Page<SearchPartnerResponse> searchPartner(String q, String partnerType, Integer status, Integer approvalStatus, Pageable pageable);

    void updateStatusPartner(String id, Integer status);

    List<GetAllOrganizationUnitResponse> getUnitByCode(String code);

    List<GetAllOrganizationUnitResponse> getAllPartner(String q);

    OrganizationDeliveryInfoDTO getDeliveryInfo(FileAttachments attachments);

    OrganizationUnitDTO getDetailByCode(String code);

    void createPackageClientForClient(PackageClientRequest clientRequest);

    List<PackageProfileDTO> getAllPackageByClientId(String clientId);

    OrganizationUnitDTO getOrgNBOByPartner(String partnerId);
}

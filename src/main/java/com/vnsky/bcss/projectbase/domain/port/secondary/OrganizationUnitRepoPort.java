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
    List<OrganizationUnitResponse> getListOrganization();

    List<OrganizationUnitResponse> getListOrganizationUnitChild(String parentId);
}

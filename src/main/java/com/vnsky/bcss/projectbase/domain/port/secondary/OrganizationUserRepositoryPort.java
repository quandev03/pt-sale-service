package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationCurrentResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationDTOResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.UserInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrganizationUserRepositoryPort {

    List<OrganizationUserDTO> findByUserId(String userId);

}

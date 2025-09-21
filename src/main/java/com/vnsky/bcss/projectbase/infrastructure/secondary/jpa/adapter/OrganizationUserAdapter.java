package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUserEntity;
import com.vnsky.bcss.projectbase.domain.mapper.OrganizationUserMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepositoryPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationDTOResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationCurrentResponse;
import  com.vnsky.bcss.projectbase.infrastructure.data.response.UserInfoResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.OrganizationUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class OrganizationUserAdapter extends BaseJPAAdapterVer2<OrganizationUserEntity, OrganizationUserDTO, String, OrganizationUserMapper, OrganizationUserRepository>
implements OrganizationUserRepoPort {

    public OrganizationUserAdapter(OrganizationUserMapper mapper,
                                 OrganizationUserRepository repository) {
        super(repository, mapper);
    }

    @Override
    public Optional<OrganizationUserDTO> findByUserId(String userId) {
        return repository.findByUserId(userId).map(mapper::toDto);
    }
}

package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;


import com.vnsky.bcss.projectbase.domain.dto.OrganizationDeliveryInfoDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationDeliveryInfoEntity;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUnitEntity;

import com.vnsky.bcss.projectbase.domain.mapper.OrganizationUnitMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchPartnerResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.OrganizationUnitRepository;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import com.vnsky.security.SecurityUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrganizationUnitAdapter extends BaseJPAAdapterVer2<OrganizationUnitEntity, OrganizationUnitDTO, String, OrganizationUnitMapper, OrganizationUnitRepository> implements OrganizationUnitRepoPort {

    private final DbMapper dbMapper;

    public OrganizationUnitAdapter(OrganizationUnitRepository repository, OrganizationUnitMapper mapper, DbMapper dbMapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }


    @Override
    public List<OrganizationUnitResponse> getListOrganization() {
        return repository.getInfoOrganization()
            .stream().map(org-> dbMapper.castSqlResult(org, OrganizationUnitResponse.class)).toList();
    }

    @Override
    public List<OrganizationUnitResponse> getListOrganizationUnitChild(String parentId) {
        return repository.getInfoOrganizationByParentId(parentId)
            .stream().map(org-> dbMapper.castSqlResult(org, OrganizationUnitResponse.class)).toList();
    }
}

package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationUserDTO;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUserEntity;
import com.vnsky.bcss.projectbase.domain.mapper.OrganizationUserMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUserRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationDTOResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationCurrentResponse;
import  com.vnsky.bcss.projectbase.infrastructure.data.response.UserInfoResponse;
import com.vnsky.bcss.projectbase.domain.dto.UserDTO;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.OrganizationUserRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.Status;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class OrganizationUserAdapter extends BaseJPAAdapterVer2<OrganizationUserEntity, OrganizationUserDTO, String, OrganizationUserMapper, OrganizationUserRepository>
implements OrganizationUserRepoPort {
    private final DbMapper dbMapper;

    public OrganizationUserAdapter(OrganizationUserMapper mapper,
                                 OrganizationUserRepository repository, DbMapper dbMapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public Optional<OrganizationUserDTO> findById(String id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Optional<OrganizationUserDTO> findByUserId(String userId) {
        return repository.findByUserId(userId).map(mapper::toDto);
    }
    @Override
    public List<OrganizationUserDTO> getAllOrganizationUserByOrgId(String id) {
        return mapper.toListDto(repository.getAllOrganizationUsersByOrgId(id));
    }

    @Override
    public Page<OrganizationUserDTO> getByUnit(String username, String unitID) {
        return mapper.toPageDto(repository.getByUnit(username, unitID));
    }

    @Override
    public Page<OrganizationUserDTO> getByUnit(String username, String unitID, Pageable page) {
        return repository.getByUnit(username, unitID, page).map(mapper::toDto);
    }

    @Override
    public void deleteByUserId(String userId) {
        this.repository.deleteByUserId(userId);
    }

    @Override
    public List<OrganizationUserDTO> findOrgByUserId(String userId) {
        List<OrganizationUserEntity> organizationUserEntities = this.repository.findOrgByUserId(userId);
        return this.mapper.toListDto(organizationUserEntities);
    }

    @Override
    public void deleteByUserIdIn(List<String> userIds) {
        this.repository.deleteByUserIdIn(userIds);
    }

    @Override
    public OrganizationUserDTO findByUserIdAndIsCurrent(String currentUserId, Integer aTrue) {
        return this.mapper.toDto(this.repository.findByUserIdAndIsCurrent(currentUserId, aTrue));
    }

    @Override
    public boolean existsByUserIdAndOrgId(String userId, String orgId) {
        return this.repository.existsByUserIdAndOrgId(userId, orgId);
    }

    @Override
    public OrganizationDTOResponse getOrganizationUserIdCurrent(String currentUserId) {
        Tuple results = this.repository.getOrganizationUserIdCurrent(currentUserId, Status.ACTIVE.getValue());
        return dbMapper.castSqlResult(results, OrganizationDTOResponse.class);
    }

    @Override
    public List<OrganizationCurrentResponse> getOrganizationUser(String currentUserId) {
        List<Tuple> results = this.repository.getOrganizationUser(currentUserId, Status.ACTIVE.getValue());
        return dbMapper.castSqlResult(results, OrganizationCurrentResponse.class);
    }

    @Override
    public Page<UserInfoResponse> getUserByClientId(String q, String clientId, Pageable pageable) {
        List<Tuple> results = this.repository.getUserByClientId(q, clientId, pageable.getOffset(), pageable.getPageSize());
        Long total = CollectionUtils.isEmpty(results) ? 0 : dbMapper.getLongSafe(results.get(0), "total");
        return new PageImpl<>(this.dbMapper.castSqlResult(results, UserInfoResponse.class), pageable, total);
    }

    @Override
    public List<OrganizationDTOResponse> findAllChildOrgById(String orgId, String clientId) {
        List<Tuple> results = this.repository.findAllChildByParentId(orgId, clientId);
        return dbMapper.castSqlResult(results, OrganizationDTOResponse.class);
    }


    @Override
    public String getOrganizationUnitNameByUserId(String userId) {
        return repository.findByOrganizationUnitByUserId(userId);
    }

    @Override
    public void updateOrganizationUnit(String userId, String orgId) {
        repository.updateOrgUnit(orgId, userId);
    }

    @Override
    public List<UserDTO> getChildUser(String parentId) {
        List<Tuple> results = repository.getChildUserTuples(parentId);
        return dbMapper.castSqlResult(results, UserDTO.class);
    }
}

package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.OrganizationDeliveryInfoDTO;
import com.vnsky.bcss.projectbase.domain.dto.OrganizationUnitDTO;

import com.vnsky.bcss.projectbase.domain.entity.OrganizationDeliveryInfoEntity;
import com.vnsky.bcss.projectbase.domain.entity.OrganizationUnitEntity;
import com.vnsky.bcss.projectbase.domain.mapper.OrganizationDeliveryInfosMapper;
import com.vnsky.bcss.projectbase.domain.mapper.OrganizationUnitMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.GetAllOrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.OrganizationUnitResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.SearchPartnerResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.OrganizationDeliveryInfoRepository;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.OrganizationUnitRepository;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomRentalStatus;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import com.vnsky.common.exception.domain.BaseException;
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

@Component
@Slf4j
public class OrganizationUnitAdapter extends BaseJPAAdapterVer2<OrganizationUnitEntity, OrganizationUnitDTO, String, OrganizationUnitMapper, OrganizationUnitRepository> implements OrganizationUnitRepoPort {


    private final OrganizationDeliveryInfoRepository organizationDeliveryInfoRepository;
    private final EntityManager entityManager;
    private final DbMapper dbMapper;

    public OrganizationUnitAdapter(OrganizationUnitRepository repository, OrganizationUnitMapper mapper, DbMapper dbMapper, OrganizationDeliveryInfoRepository organizationDeliveryInfoRepository, EntityManager entityManager) {
        super(repository, mapper);
        this.organizationDeliveryInfoRepository = organizationDeliveryInfoRepository;
        this.entityManager = entityManager;
        this.dbMapper = dbMapper;
    }

    @Override
    public OrganizationUnitDTO updatePartner(OrganizationUnitDTO organizationUnitDTO) {
        OrganizationUnitEntity entity = repository.findById(organizationUnitDTO.getId()).orElseThrow(() -> BaseException.badRequest(ErrorCode.ORG_PARTNER_NOT_EXISTS).build());
        entity.setOrgDescription(organizationUnitDTO.getOrgDescription());
        return this.mapper.toDto(repository.save(entity));
    }

    @Override
    public OrganizationUnitDTO getByOrgCode(String id, String orgCode) {
        return this.mapper.toDto(this.repository.findByCode(id, orgCode, Constant.OrgType.NBO, SecurityUtil.getCurrentClientId()).orElse(null));
    }

    @Override
    public OrganizationUnitDTO getOrgRoot(String id) {
        return this.mapper.toDto(this.repository.findOrgRoot(id, Constant.OrgType.NBO, SecurityUtil.getCurrentClientId()).orElse(null));
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getPartnersWithoutOrganizationLimit() {
        List<Tuple> result = repository.getPartnersWithoutOrganizationLimit(Constant.OrgType.PARTNER);
        return dbMapper.castSqlResult(result, GetAllOrganizationUnitResponse.class);
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getAllOrganizationUnits(Integer status, String orgType, String orgSubType, String textSearch, String rentalStatus) {
        List<Tuple> result;
        if(StringUtils.hasText(orgType))
            result = this.repository.getAllOrganizationUnit(status, orgType, SecurityUtil.getCurrentClientId(), orgSubType, textSearch, rentalStatus);
        else
            result = this.repository.getAllOrganizationUnit(status, Constant.OrgType.NBO, SecurityUtil.getCurrentClientId(), orgSubType, textSearch, rentalStatus);

        return dbMapper.castSqlResult(result, GetAllOrganizationUnitResponse.class);
    }

    @Override
    public int countActiveUnit(String id, Integer status) {
        return this.repository.countOrganizationUnitActive(id, status, Constant.OrgType.NBO);
    }

    @Override
    public int countActiveUnitAll(String id, Integer status, String orgType) {
        return this.repository.countOrganizationUnitActive(id, status, Constant.OrgType.NBO);
    }

    @Override
    public int countChildUnit(String id) {
        return this.repository.countChildOrganizationUnit(id, Constant.OrgType.NBO);
    }

    @Override
    public boolean existsByCode(String id, String code, String type) {
        return this.repository.existsByCode(id, code, type);
    }

    @Override
    public boolean existsById(String id) {
        return this.repository.existsById(id);
    }

    @Override
    public boolean existsByIdAndOrgType(String id, String orgType) {
        return repository.existsByIdAndOrgType(id, orgType);
    }

    @Override
    public Optional<OrganizationUnitDTO> findById(String id) {
        return this.repository.findByIdLazy(id)
            .map(this.mapper::toDto);
    }

    @Override
    public Optional<OrganizationUnitDTO> findByCode(String code) {
        return this.repository.findByCode(code)
            .map(this.mapper::toDto);
    }

    @Override
    public void deleteAndFlush(String id) {

    }

    @Override
    public Long getRootOrgByClientId(String clientId) {
        return this.repository.getRootOrgByClientId(clientId, Constant.OrgType.NBO);
    }

    @Override
    public List<OrganizationUnitDTO> getAllById(List<Long> orgIds) {
        return this.mapper.toListDto(this.repository.getAllByIdIn(orgIds));
    }

    @Override
    public List<OrganizationUnitDTO> getAllByOrgType(String orgType) {
        return this.mapper.toListDto(this.repository.getAllByOrgType(orgType));
    }

    @Override
    public OrganizationUnitDTO findByCodeAndType(String orgCode, String type) {
        return this.mapper.toDto(this.repository.findByOrgCodeFromRoot(orgCode, type));
    }

    @Override
    public OrganizationUnitDTO findByUserId(String userId) {
        return this.mapper.toDto(this.repository.findByUserId(userId));
    }

    @Override
    public OrganizationUnitDTO getOrgNBOByPartner(String partnerID) {
        return this.mapper.toDto(this.repository.findNBOByPartnerId(partnerID));
    }

    @Override
    public OrganizationUnitDTO getRootOrg(String clientID) {
        return this.repository.getOrgRoot(clientID)
            .map(mapper::toDto)
            .orElse(null);
    }

    @Override
    public OrganizationDeliveryInfoDTO findDeliveryByOrgId(String id) {
        OrganizationDeliveryInfoEntity entity = this.repository.findDeliveryByOrgId(id);
        return Mappers.getMapper(OrganizationDeliveryInfosMapper.class).toDto(entity);
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getAllUnitByClientId(String orgCode, String clientId) {
        List<Tuple> results = this.repository.getAllUnitByClientId(orgCode, clientId, Constant.OrgType.NBO);
        return this.dbMapper.castSqlResult(results, GetAllOrganizationUnitResponse.class);
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getAllOrganizationByType(String q, String orgType) {
        List<Tuple> results = this.repository.getAllOrganizationByType(q, orgType);
        return this.dbMapper.castSqlResult(results, GetAllOrganizationUnitResponse.class);
    }

    @Override
    public Page<SearchPartnerResponse> searchPartner(String q, String partnerType, Integer status, Integer approvalStatus, Pageable pageable) {
        List<Tuple> results = this.repository.search(q, Constant.OrgType.PARTNER, partnerType, status, approvalStatus, pageable.getOffset(), pageable.getPageSize());
        long total = CollectionUtils.isEmpty(results) ? 0 : dbMapper.getLongSafe(results.get(0), "total");
        return new PageImpl<>(dbMapper.castSqlResult(results, SearchPartnerResponse.class), pageable, total);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        this.repository.updateStatus(id, status);
    }

    @Override
    public OrganizationUnitDTO getDetailPartnerByClientId(String clientId, String partnerType) {
        return this.mapper.toDto(this.repository.getDetailPartnerByClientId(clientId, partnerType));
    }

    @Override
    public OrganizationUnitDTO getOrgRootByClientId(String clientId) {
        return this.mapper.toDto(this.repository.getOrgRootByClientId(clientId, Constant.OrgType.NBO));
    }

    @Override
    public OrganizationUnitDTO getOrgRootByClientIdAndOrgType(String clientId, String orgType) {
        return this.mapper.toDto(this.repository.getOrgRootByClientId(clientId, orgType));
    }

    @Override
    public OrganizationUnitDTO getOrgByClientIdAndOrgType(String clientId, String orgType) {
        return this.mapper.toDto(this.repository.getOrgByClientId(clientId, orgType));
    }

    @Override
    public OrganizationUnitDTO getCurrentPartnerByUserIdAndClientId(String userId, String clientId) {
        return mapper.toDto(repository.getCurrentPartnerByUserIdAndClientId(clientId, Constant.Status.ACTIVE, Constant.OrgType.PARTNER));
    }

    @Override
    public OrganizationDeliveryInfoDTO saveDeliveryInfo(OrganizationDeliveryInfoDTO organizationDeliveryInfoDTO) {
        OrganizationDeliveryInfosMapper deliveryMapper = Mappers.getMapper(OrganizationDeliveryInfosMapper.class);
        OrganizationDeliveryInfoEntity entity;
        if (organizationDeliveryInfoDTO.getId() == null) {
            entity = this.organizationDeliveryInfoRepository.saveAndFlush(deliveryMapper.toEntity(organizationDeliveryInfoDTO));
        }else {
            organizationDeliveryInfoRepository.findById(organizationDeliveryInfoDTO.getId()).ifPresent(organizationDeliveryInfoRepository::delete);
            entity = organizationDeliveryInfoRepository.saveAndFlush(deliveryMapper.toEntity(organizationDeliveryInfoDTO));
        }
        return deliveryMapper.toDto(entity);
    }

    @Override
    public void updateApprovalStatus(String id, Integer status) {
        this.repository.updateApprovalStatus( id, status);
    }

    @Override
    public void updateClientId(String id, String clientId) {
        this.repository.updateClientId(id, clientId);
    }

    @Override
    public OrganizationUnitDTO getUnitByOrgIdentity(String orgIdentity, String clientId) {
        return this.mapper.toDto(this.repository.getUnitByOrgIdentity(orgIdentity, clientId));
    }

    @Override
    public long countExitsDeliveryAreas(List<String> listDeliveryAreas, String web, String id) {
        StringBuilder query = new StringBuilder("SELECT count(*) FROM ORGANIZATION_UNIT ou WHERE 1 = 1 ");
        query.append("AND INSTR(',' || ou.SALES_CHANNELS  || ',', ").append("',").append(web).append(",'").append(") > 0 ");
        if (Objects.nonNull(id)) {
            query.append(" AND ou.ID != ").append(id).append(" ");
        }
        if (!listDeliveryAreas.isEmpty()) {
            query.append("AND (");
            for (int i = 0; i < listDeliveryAreas.size(); i++) {
                if (i > 0) {
                    query.append(" OR ");
                }
                query.append("INSTR(',' || ou.DELIVERY_AREAS || ',', ',").append(listDeliveryAreas.get(i)).append(",') > 0");
            }
            query.append(") ");
        }
        return ((Number) entityManager.createNativeQuery(query.toString()).getSingleResult()).longValue();
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getAllOrganizationUnitByStatusAndTypeAndClient(Integer status, String orgType, String orgSubType, String clientId) {
        List<Tuple> results = this.repository.getAllOrganizationUnitByStatusAndTypeAndClient(status, orgType, orgSubType, clientId);
        return this.dbMapper.castSqlResult(results, GetAllOrganizationUnitResponse.class);
    }


    @Override
    public List<OrganizationUnitResponse> getListOrganization() {
        return repository.getInfoOrganization()
            .stream().map(org-> dbMapper.castSqlResult(org, OrganizationUnitResponse.class)).toList();
    }

    @Override
    public int checkChildAndParent(String parentId, String childId) {
        return repository.checkConnectParentId(parentId, childId);
    }

    @Override
    public List<OrganizationUnitDTO> getChildUnits(String parentId) {
        return repository.getListChildOrganizationUnit(parentId).stream().map(org-> mapper.toDto(org)).toList();
    }

    @Override
    public List<GetAllOrganizationUnitResponse> getActivedAuthorizedOrg(String orgSubType) {
        return List.of();
    }


    @Override
    public List<OrganizationUnitResponse> getListOrganizationUnitChild(String parentId) {
        return repository.getInfoOrganizationByParentId(SecurityUtil.getCurrentClientId())
            .stream().map(org-> dbMapper.castSqlResult(org, OrganizationUnitResponse.class)).toList();
    }

    @Override
    public List<OrganizationUnitDTO> findByRentalStatus(com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomRentalStatus rentalStatus) {
        return repository.findByRentalStatus(rentalStatus).stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public List<OrganizationUnitDTO> findAvailableRoomsWithFilters(
            String provinceCode,
            String wardCode,
            Long minAcreage,
            Long maxAcreage) {
        return repository.findAvailableRoomsWithFilters(
                com.vnsky.bcss.projectbase.shared.enumeration.domain.RoomRentalStatus.AVAILABLE,
                provinceCode,
                wardCode,
                minAcreage,
                maxAcreage
        ).stream()
            .map(mapper::toDto)
            .toList();
    }
}

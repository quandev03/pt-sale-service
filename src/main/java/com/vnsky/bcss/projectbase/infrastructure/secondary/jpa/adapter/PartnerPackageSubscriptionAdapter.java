package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionDTO;
import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionView;
import com.vnsky.bcss.projectbase.domain.entity.PartnerPackageSubscriptionEntity;
import com.vnsky.bcss.projectbase.domain.mapper.PartnerPackageSubscriptionMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.OrganizationUnitRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.PartnerPackageSubscriptionRepository;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionStatus;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import com.vnsky.security.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class PartnerPackageSubscriptionAdapter extends BaseJPAAdapterVer2<
    PartnerPackageSubscriptionEntity,
    PartnerPackageSubscriptionDTO,
    String,
    PartnerPackageSubscriptionMapper,
    PartnerPackageSubscriptionRepository> implements PartnerPackageSubscriptionRepoPort {

    private final PartnerPackageSubscriptionRepository repository;
    private final PartnerPackageSubscriptionMapper mapper;
    private final DbMapper dbMapper;
    private final OrganizationUnitRepoPort organizationUnitRepoPort;

    public PartnerPackageSubscriptionAdapter(PartnerPackageSubscriptionRepository repository,
                                             PartnerPackageSubscriptionMapper mapper,
                                             DbMapper dbMapper, OrganizationUnitRepoPort organizationUnitRepoPort) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.dbMapper = dbMapper;
        this.organizationUnitRepoPort = organizationUnitRepoPort;
    }

    @Override
    public PartnerPackageSubscriptionDTO saveAndFlush(PartnerPackageSubscriptionDTO dto) {
        return mapper.toDto(repository.saveAndFlush(mapper.toEntity(dto)));
    }

    @Override
    public Optional<PartnerPackageSubscriptionDTO> findById(String id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Optional<PartnerPackageSubscriptionDTO> findActiveByOrgUnitAndPackage(String organizationUnitId, String packageProfileId) {
        return repository.findByOrganizationUnitIdAndPackageProfileIdAndStatus(
                organizationUnitId,
                packageProfileId,
                PartnerPackageSubscriptionStatus.ACTIVE
            )
            .map(mapper::toDto);
    }

    @Override
    public Optional<PartnerPackageSubscriptionDTO> findByOrgUnitAndPackageAndStatuses(String organizationUnitId,
                                                                                      String packageProfileId,
                                                                                      List<PartnerPackageSubscriptionStatus> statuses) {
        return repository.findFirstByOrganizationUnitIdAndPackageProfileIdAndStatusIn(organizationUnitId, packageProfileId, statuses)
            .map(mapper::toDto);
    }

    @Override
    public List<PartnerPackageSubscriptionDTO> findActiveSubscriptionsEndingBefore(LocalDateTime deadline) {
        return repository.findByStatusAndEndTimeLessThanEqual(PartnerPackageSubscriptionStatus.ACTIVE, deadline)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public Page<PartnerPackageSubscriptionView> search(String organizationUnitId,
                                                       String packageProfileId,
                                                       String status,
                                                       Pageable pageable) {
        String orgId = null;
        if(!Objects.equals(SecurityUtil.getCurrentClientId(), "000000000000")){
            orgId = organizationUnitRepoPort.getOrgRootByClientId(SecurityUtil.getCurrentClientId()).getId();
        }
        return dbMapper.castSqlResult(
            repository.search(organizationUnitId, packageProfileId, status, orgId, pageable),
            PartnerPackageSubscriptionView.class
        );
    }

    @Override
    public void updateStatusActive(String id) {
        repository.updateStatus(id);
    }
}


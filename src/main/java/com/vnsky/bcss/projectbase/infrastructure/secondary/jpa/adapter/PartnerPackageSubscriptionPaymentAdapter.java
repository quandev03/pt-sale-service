package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.PartnerPackageSubscriptionPaymentDTO;
import com.vnsky.bcss.projectbase.domain.entity.PartnerPackageSubscriptionPaymentEntity;
import com.vnsky.bcss.projectbase.domain.mapper.PartnerPackageSubscriptionPaymentMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.PartnerPackageSubscriptionPaymentRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.PartnerPackageSubscriptionPaymentRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.PartnerPackageSubscriptionPaymentStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PartnerPackageSubscriptionPaymentAdapter extends BaseJPAAdapterVer2<
    PartnerPackageSubscriptionPaymentEntity,
    PartnerPackageSubscriptionPaymentDTO,
    String,
    PartnerPackageSubscriptionPaymentMapper,
    PartnerPackageSubscriptionPaymentRepository> implements PartnerPackageSubscriptionPaymentRepoPort {

    private final PartnerPackageSubscriptionPaymentRepository repository;
    private final PartnerPackageSubscriptionPaymentMapper mapper;

    public PartnerPackageSubscriptionPaymentAdapter(PartnerPackageSubscriptionPaymentRepository repository,
                                                    PartnerPackageSubscriptionPaymentMapper mapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PartnerPackageSubscriptionPaymentDTO saveAndFlush(PartnerPackageSubscriptionPaymentDTO dto) {
        return super.saveAndFlush(dto);
    }

    @Override
    public Optional<PartnerPackageSubscriptionPaymentDTO> findById(String id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public Optional<PartnerPackageSubscriptionPaymentDTO> findByTxnRef(String txnRef) {
        return repository.findByTxnRef(txnRef).map(mapper::toDto);
    }

    @Override
    public Optional<PartnerPackageSubscriptionPaymentDTO> findFirstBySubscriptionIdAndStatuses(String subscriptionId,
                                                                                               List<PartnerPackageSubscriptionPaymentStatus> statuses) {
        return repository.findFirstBySubscriptionIdAndStatusInOrderByCreatedDateDesc(subscriptionId, statuses)
            .map(mapper::toDto);
    }
}


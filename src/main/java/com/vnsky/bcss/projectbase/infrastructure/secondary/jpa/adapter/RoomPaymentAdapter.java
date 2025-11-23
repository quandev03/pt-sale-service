package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDTO;
import com.vnsky.bcss.projectbase.domain.dto.RoomPaymentDetailDTO;
import com.vnsky.bcss.projectbase.domain.entity.RoomPaymentDetailEntity;
import com.vnsky.bcss.projectbase.domain.entity.RoomPaymentEntity;
import com.vnsky.bcss.projectbase.domain.mapper.RoomPaymentDetailMapper;
import com.vnsky.bcss.projectbase.domain.mapper.RoomPaymentMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.RoomPaymentRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.RoomPaymentDetailRepository;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.RoomPaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoomPaymentAdapter extends BaseJPAAdapterVer2<
    RoomPaymentEntity,
    RoomPaymentDTO,
    String,
    RoomPaymentMapper,
    RoomPaymentRepository> implements RoomPaymentRepoPort {

    private final RoomPaymentRepository repository;
    private final RoomPaymentMapper mapper;
    private final RoomPaymentDetailRepository detailRepository;
    private final RoomPaymentDetailMapper detailMapper;

    public RoomPaymentAdapter(RoomPaymentRepository repository,
                             RoomPaymentMapper mapper,
                             RoomPaymentDetailRepository detailRepository,
                             RoomPaymentDetailMapper detailMapper) {
        super(repository, mapper);
        this.repository = repository;
        this.mapper = mapper;
        this.detailRepository = detailRepository;
        this.detailMapper = detailMapper;
    }

    @Override
    public RoomPaymentDTO save(RoomPaymentDTO dto) {
        return super.save(dto);
    }

    @Override
    public RoomPaymentDTO update(RoomPaymentDTO dto) {
        return super.update(dto);
    }

    @Override
    public Optional<RoomPaymentDTO> findById(String id) {
        return repository.findById(id)
            .map(mapper::toDto);
    }

    @Override
    public Optional<RoomPaymentDTO> findByOrgUnitIdAndMonthAndYear(String orgUnitId, Integer month, Integer year) {
        return repository.findByOrgUnitIdAndMonthAndYear(orgUnitId, month, year)
            .map(mapper::toDto);
    }

    @Override
    public List<RoomPaymentDTO> findByOrgUnitId(String orgUnitId) {
        return repository.findByOrgUnitIdOrderByYearDescMonthDesc(orgUnitId)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public List<RoomPaymentDTO> findByFilters(String orgUnitId, Integer year, Integer month) {
        return repository.findByFilters(orgUnitId, year, month)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public List<RoomPaymentDTO> findByClientId(String clientId, Integer year, Integer month) {
        return repository.findByClientId(clientId, year, month)
            .stream()
            .map(mapper::toDto)
            .toList();
    }

    @Override
    public RoomPaymentDetailDTO saveDetail(RoomPaymentDetailDTO dto) {
        RoomPaymentDetailEntity entity = detailMapper.toEntity(dto);
        return detailMapper.toDto(detailRepository.save(entity));
    }

    @Override
    public List<RoomPaymentDetailDTO> findDetailsByRoomPaymentId(String roomPaymentId) {
        return detailRepository.findByRoomPaymentIdOrderByCreatedDateAsc(roomPaymentId)
            .stream()
            .map(detailMapper::toDto)
            .toList();
    }

    @Override
    public void deleteDetailsByRoomPaymentId(String roomPaymentId) {
        detailRepository.deleteByRoomPaymentId(roomPaymentId);
    }
}


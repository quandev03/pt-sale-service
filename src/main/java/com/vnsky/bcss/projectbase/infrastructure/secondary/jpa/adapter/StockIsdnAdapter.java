package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.StockIsdnDTO;
import com.vnsky.bcss.projectbase.domain.entity.StockIsdnEntity;
import com.vnsky.bcss.projectbase.domain.mapper.StockIsdnMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.StockIsdnRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.StockIsdnRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StockIsdnAdapter extends BaseJPAAdapterVer2<StockIsdnEntity, StockIsdnDTO, String, StockIsdnMapper, StockIsdnRepository>
        implements StockIsdnRepoPort {

    public StockIsdnAdapter(StockIsdnMapper mapper, StockIsdnRepository repository) {
        super(repository, mapper);
    }

    @Override
    public List<StockIsdnDTO> findAvailableIsdns(int limit) {
        return repository.findAvailableIsdns().stream()
                .limit(limit)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Optional<StockIsdnDTO> findByIsdn(Long isdn) {
        return repository.findByIsdn(isdn).map(mapper::toDto);
    }

    @Override
    public Optional<StockIsdnDTO> findBySerial(Long serial) {
        return repository.findBySerial(serial).map(mapper::toDto);
    }
} 
package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.port.secondary.CommonRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.CommonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CommonRepoAdapter implements CommonRepoPort {
    private final CommonRepository commonRepository;

    @Override
    @Transactional
    public int getSequenceValue(String sequenceName) {
        return commonRepository.getSequenceValue(sequenceName);
    }
}

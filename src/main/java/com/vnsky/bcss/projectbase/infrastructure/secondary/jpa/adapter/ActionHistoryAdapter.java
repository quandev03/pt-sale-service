package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.entity.ActionHistoryEntity;
import com.vnsky.bcss.projectbase.domain.mapper.ActionHistoryMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.ActionHistoryRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.ActionHistoryRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ActionHistoryAdapter extends BaseJPAAdapterVer2<ActionHistoryEntity, ActionHistoryDTO, String, ActionHistoryMapper, ActionHistoryRepository>
        implements ActionHistoryRepoPort {
    private final DbMapper dbMapper;

    public ActionHistoryAdapter(DbMapper dbMapper,
                                ActionHistoryMapper mapper,
                                ActionHistoryRepository repository) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public List<ActionHistoryDTO> getListActionHistoryBySubId(String subId, Sort sort) {
        return repository.findAllBySubId(subId,sort).stream().map(actionHistory -> mapper.toDto(actionHistory)).toList();
    }
}

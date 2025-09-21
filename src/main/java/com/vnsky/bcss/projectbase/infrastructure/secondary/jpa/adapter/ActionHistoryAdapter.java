package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.ActionHistoryDTO;
import com.vnsky.bcss.projectbase.domain.dto.EsimInforDTO;
import com.vnsky.bcss.projectbase.domain.dto.SubscriberDTO;
import com.vnsky.bcss.projectbase.domain.entity.ActionHistoryEntity;
import com.vnsky.bcss.projectbase.domain.entity.SubscriberEntity;
import com.vnsky.bcss.projectbase.domain.mapper.ActionHistoryMapper;
import com.vnsky.bcss.projectbase.domain.mapper.SubscriberMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.ActionHistoryRepoPort;
import com.vnsky.bcss.projectbase.domain.port.secondary.SubscriberRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.ActionHistoryRepository;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.SubscriberRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    public List<ActionHistoryDTO> getListActionHistoryBySubId(String subId) {
        return repository.findAllBySubId(subId).stream().map(actionHistory -> mapper.toDto(actionHistory)).toList();
    }

    @Override
    public ActionHistoryDTO findByActionCodeAndSubId(String actionCode, String subId) {
        return mapper.toDto(repository.findByActionCodeAndSubId(actionCode, subId));
    }
}

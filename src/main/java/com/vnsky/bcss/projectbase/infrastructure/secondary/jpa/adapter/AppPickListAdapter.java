package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.AppPickListDTO;
import com.vnsky.bcss.projectbase.domain.entity.AppPickListEntity;
import com.vnsky.bcss.projectbase.domain.mapper.AppPickListMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.AppPickListRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.AppPickListRepository;
import com.vnsky.database.service.DatabaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppPickListAdapter extends BaseJPAAdapterVer2<AppPickListEntity, AppPickListDTO, String, AppPickListMapper, AppPickListRepository>
    implements AppPickListRepoPort {

    private final DatabaseMapper dbMapper;

    @Autowired
    public AppPickListAdapter(AppPickListRepository repository, AppPickListMapper mapper, DatabaseMapper dbMapper) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }

    @Override
    public List<AppPickListEntity> findByTableNameAndColumnName(String tableName, String columnName) {
        log.debug("Finding AppPickList by tableName: {} and columnName: {}", tableName, columnName);
        return repository.findByTableNameAndColumnName(tableName, columnName);
    }

    @Override
    public void patchToDataList(List<?> objects) {
        this.dbMapper.patchAppPickList(objects);
    }

    @Override
    public List<AppPickListEntity> findAllActive() {
        return this.repository.findAllActive();
    }
}

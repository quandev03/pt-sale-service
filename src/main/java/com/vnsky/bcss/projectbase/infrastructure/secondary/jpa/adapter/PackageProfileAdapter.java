package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.entity.PackageProfileEntity;
import com.vnsky.bcss.projectbase.domain.mapper.PackageProfileMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.PackageProfileRepository;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.common.exception.domain.ErrorKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PackageProfileAdapter extends BaseJPAAdapterVer2<PackageProfileEntity, PackageProfileDTO, String, PackageProfileMapper, PackageProfileRepository>
        implements PackageProfileRepoPort {
    private final DbMapper dbMapper;

    public PackageProfileAdapter(DbMapper dbMapper,
                                 PackageProfileMapper mapper,
                                 PackageProfileRepository repository) {
        super(repository, mapper);
        this.dbMapper = dbMapper;
    }


    @Override
    public boolean isExistPackageCode(String packageCode) {
        return repository.isExistPackageCode(packageCode) > 0;
    }

    @Override
    public PackageProfileDTO findById(String idPackageProfile) {
        return mapper.toDto(repository.findById(idPackageProfile).orElseThrow(() -> BaseException.notFoundError(ErrorKey.BAD_REQUEST).build()));
    }

    @Override
    public PackageProfileDTO findByPackageCode(String packageCode) {
        return mapper.toDto(repository.findByPackageCode(packageCode));
    }

    @Override
    public void deleteById(String idPackageProfile) {
        repository.deleteById(idPackageProfile);
    }

    @Override
    public List<PackageProfileDTO> getListPackageProfileFree() {
        return mapper.toListDto(repository.getPackageProfileFree());
    }

    @Override
    public Optional<PackageProfileDTO> findByPckCode(String pckCode) {
        return repository.findByPckCode(pckCode)
                .map(mapper::toDto);
    }

    @Override
    public List<PackageProfileDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Page<PackageProfileDTO> searchPackageProfile(String pckCodeOrPckName, Integer status, Long
            minPrice, Long maxPrice, Pageable pageable) {
        return repository.searchPackageProfile(pckCodeOrPckName, status, minPrice, maxPrice, pageable).map(entity -> mapper.toDto(entity));
    }
}

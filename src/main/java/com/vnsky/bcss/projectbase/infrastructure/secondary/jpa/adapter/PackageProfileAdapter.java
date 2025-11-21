package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.PackageProfileDTO;
import com.vnsky.bcss.projectbase.domain.entity.PackageProfileEntity;
import com.vnsky.bcss.projectbase.domain.mapper.PackageProfileMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageProfileRepoPort;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticOrgResponse;
import com.vnsky.bcss.projectbase.infrastructure.data.response.StatisticResponse;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.PackageProfileRepository;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.utils.DbMapper;
import com.vnsky.common.exception.domain.BaseException;
import jakarta.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.List;

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
        return mapper.toDto(repository.findById(idPackageProfile)
            .orElseThrow(() -> BaseException.notFoundError(ErrorCode.PACKAGE_NOT_EXISTS)
                .message("Gói cước không tồn tại")
                .build()));
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
    public List<PackageProfileDTO> getListPackageProfile(String clientId) {
        return mapper.toListDto(repository.getPackageProfile(clientId));
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

    @Override
    public boolean isExistPckName(String pckName) {
        return repository.existsByPckName(pckName);
    }

    @Override
    public List<PackageProfileDTO> getPackageByClientId(String clientId) {
        return repository.getPackageByClientId(clientId).stream().map(entity -> mapper.toDto(entity)).toList();
    }

    @Override
    public Long totalPackagesSold(String orgCode) {
        return repository.totalPackagesSold(orgCode);
    }

    @Override
    public List<StatisticResponse> statisticPackagesSold(String orgCode, String startDate, String endDate, int granularity) {
        List<Tuple> results = repository.statisticPackagesSold(orgCode, startDate, endDate, granularity);
        return results.stream().map(result -> dbMapper.castSqlResult(result, StatisticResponse.class)).toList();
    }

    @Override
    public List<StatisticOrgResponse> statisticPackagesSoldOrg(String orgCode, String startDate, String endDate) {
        List<Tuple> results = repository.statisticPackagesSoldOrg(orgCode, startDate, endDate);
        return results.stream().map(result -> dbMapper.castSqlResult(result, StatisticOrgResponse.class)).toList();
    }

    @Override
    public Long revenusPackageSold(String orgCode) {
        return repository.revenusPackageSold(orgCode);
    }
}

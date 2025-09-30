package com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.adapter;

import com.vnsky.bcss.projectbase.domain.dto.PackageClientDTO;
import com.vnsky.bcss.projectbase.domain.entity.PackageClientEntity;
import com.vnsky.bcss.projectbase.domain.entity.PackageProfileEntity;
import com.vnsky.bcss.projectbase.domain.mapper.PackageClientMapper;
import com.vnsky.bcss.projectbase.domain.port.secondary.PackageClientRepoPost;
import com.vnsky.bcss.projectbase.infrastructure.data.request.PackageClientRequest;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.PackageClientRepository;
import com.vnsky.bcss.projectbase.infrastructure.secondary.jpa.repository.PackageProfileRepository;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PackageClientAdapter extends BaseJPAAdapterVer2<PackageClientEntity, PackageClientDTO, String, PackageClientMapper, PackageClientRepository> implements PackageClientRepoPost {
    private final PackageProfileRepository packageProfileRepository;

    public PackageClientAdapter(PackageClientRepository repository, PackageClientMapper mapper, PackageProfileRepository packageProfileRepository) {
        super(repository, mapper);
        this.packageProfileRepository = packageProfileRepository;
    }

    @Override
    public void saveAll(PackageClientRequest request) {
        List<String> codes = Optional.ofNullable(request.getPackageCodes())
            .orElse(List.of()).stream()
            .filter(s -> s != null && !s.isBlank())
            .map(String::trim)
            .distinct()
            .toList();

        if (codes.isEmpty()) return;

        // Load 1 lần tất cả profile theo code (tránh N+1)
        List<PackageProfileEntity> profiles = packageProfileRepository.findAllByPackageCodeIn(codes);
        Map<String, PackageProfileEntity> byCode = profiles.stream()
            .collect(Collectors.toMap(PackageProfileEntity::getPckCode, p -> p, (a, b) -> a));

        // Kiểm tra lỗi thiếu mã
        List<String> missing = codes.stream().filter(c -> !byCode.containsKey(c)).toList();
        if (!missing.isEmpty()) {
            throw BaseException.badRequest(ErrorCode.PACKAGE_NOT_EXISTS).build();
        }

        // Map sang entity và saveAll một lần (tận dụng batch nếu bật)
        List<PackageClientEntity> entities = codes.stream().map(c -> {
            PackageProfileEntity p = byCode.get(c);
            return PackageClientEntity.builder()
                .packageId(p.getId())
                .clientId(request.getClientId())
                .name(p.getPckName())
                .status(p.getStatus() != null ? p.getStatus() : Constant.Status.ACTIVE)
                .build();
        }).collect(Collectors.toList());

        repository.saveAll(entities);
    }

    @Override
    public void deleteAllByClient(String clientId) {
        repository.deleteByClientId(clientId);
    }
}

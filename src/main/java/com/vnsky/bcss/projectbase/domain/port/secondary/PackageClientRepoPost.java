package com.vnsky.bcss.projectbase.domain.port.secondary;

import com.vnsky.bcss.projectbase.infrastructure.data.request.PackageClientRequest;

public interface PackageClientRepoPost {

    void saveAll(PackageClientRequest request);

    void deleteAllByClient(String clientId);
}

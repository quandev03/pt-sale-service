package com.vnsky.bcss.projectbase.domain.port.primary;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OrganizationUnitImageServicePort {

    List<String> uploadImages(String orgUnitId, List<MultipartFile> files);

    Resource downloadImage(String orgUnitId, String imageId);

    List<String> updateImages(String orgUnitId, List<MultipartFile> files);

    List<String> getImageUrls(String orgUnitId);
}


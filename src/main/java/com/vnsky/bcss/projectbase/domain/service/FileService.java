package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.port.primary.FileServicePort;
import com.vnsky.minio.dto.DownloadOptionDTO;
import com.vnsky.minio.operation.MinioOperations;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService implements FileServicePort {

	private final MinioOperations minioOperations;

	@Override
	public Resource downloadByUrl(String fileUrl) {
		try {
			DownloadOptionDTO downloadOptionDTO = DownloadOptionDTO.builder()
					.uri(fileUrl)
					.isPublic(false)
					.build();
			Resource resource = this.minioOperations.download(downloadOptionDTO);
			return new InputStreamResource(resource.getInputStream());
		} catch (Exception e) {
			log.error("Error download from minio:", e);
			throw BaseException.notFoundError(ErrorCode.INTERNAL_SERVER_ERROR).build();
		}
	}
}



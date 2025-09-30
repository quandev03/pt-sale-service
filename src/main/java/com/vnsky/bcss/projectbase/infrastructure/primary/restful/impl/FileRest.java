package com.vnsky.bcss.projectbase.infrastructure.primary.restful.impl;

import com.vnsky.bcss.projectbase.domain.port.primary.FileServicePort;
import com.vnsky.bcss.projectbase.infrastructure.primary.restful.FileOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileRest implements FileOperation {

	private final FileServicePort fileServicePort;

	@Override
	public ResponseEntity<Resource> downloadFileByUrl(String fileUrl) {
		Resource resource = this.fileServicePort.downloadByUrl(fileUrl);
		return ResponseEntity.ok(resource);
	}
}



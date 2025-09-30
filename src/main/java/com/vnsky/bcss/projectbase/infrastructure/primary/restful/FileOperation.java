package com.vnsky.bcss.projectbase.infrastructure.primary.restful;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "File Operation", description = "API liên quan đến file")
@RequestMapping({"${application.path.base.public}/files", "${application.path.base.private}/files"})
public interface FileOperation {
	@Operation(summary = "Download file by URL")
	@GetMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	ResponseEntity<Resource> downloadFileByUrl(@RequestParam("fileUrl") String fileUrl);
}



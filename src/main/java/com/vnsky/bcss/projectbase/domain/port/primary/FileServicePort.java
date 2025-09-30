package com.vnsky.bcss.projectbase.domain.port.primary;

import org.springframework.core.io.Resource;

public interface FileServicePort {
	Resource downloadByUrl(String fileUrl);
}



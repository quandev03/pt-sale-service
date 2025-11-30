package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.EKYCOCRResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

public interface OcrServicePort {
    EKYCOCRResponseDTO callOCRAndFaceCheck(int cardType, MultipartFile front, MultipartFile back, MultipartFile portrait, String authenCode);
    Resource genContract() throws Exception;
    Resource genContract(InputStream template, Map<String, String> data) throws Exception;
}

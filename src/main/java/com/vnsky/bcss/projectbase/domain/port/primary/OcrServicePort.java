package com.vnsky.bcss.projectbase.domain.port.primary;

import com.vnsky.bcss.projectbase.domain.dto.EKYCOCRResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OcrServicePort {
    EKYCOCRResponseDTO callOCRAndFaceCheck(int cardType, MultipartFile front, MultipartFile back, MultipartFile portrait, String authenCode);
    Object genContract(MultipartFile template) throws Exception;
}

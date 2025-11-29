package com.vnsky.bcss.projectbase.domain.service;

import com.vnsky.bcss.projectbase.domain.dto.EKYCOCRRequestDTO;
import com.vnsky.bcss.projectbase.domain.dto.EKYCOCRResponseDTO;
import com.vnsky.bcss.projectbase.domain.dto.GeneralDTO;
import com.vnsky.bcss.projectbase.domain.port.primary.OcrServicePort;
import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorSubcriberKey;
import com.vnsky.common.exception.domain.BaseException;
import com.vnsky.common.exception.domain.ErrorKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class OcrService implements OcrServicePort {

    private static final Long MAX_SIZE_IMAGE = 5000 * 1024L * 1024L;
    private final RestOperations restTemplateOperation;
    private static final boolean DEBUG = true;


    public OcrService (@Qualifier("restTemplate") RestOperations restTemplateOperation) {
        this.restTemplateOperation = restTemplateOperation;
    }

    @Value("${third-party.integration.apikey}")
    private String apiKey;

    @Value("${third-party.integration.general}")
    private String urlGeneral;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        return headers;
    }

    @Override
    public EKYCOCRResponseDTO callOCRAndFaceCheck(int cardType, MultipartFile front, MultipartFile back, MultipartFile portrait, String authenCode) {
        log.info("[EKYC INFO] Start getActivationInfo");
        log.info("[EKYC INFO] step1: validateSize Image");
        List<MultipartFile> files = List.of(front, back, portrait);
        checkImage(files);
        // call api ocr
        log.info("[EKYC INFO] step2: call api ocr");
        EKYCOCRRequestDTO requestCallOcr = new EKYCOCRRequestDTO(cardType, convertFileToBase64(front), convertFileToBase64(back));
        return getOCR(requestCallOcr);
    }

    @Override
    public Object genContract(MultipartFile template) throws Exception {
        return fillTemplateToDocx(template.getInputStream(),test());
    }

    private EKYCOCRResponseDTO getOCR(EKYCOCRRequestDTO ekycocrRequestDTO) {
        EKYCOCRResponseDTO ocrInfo;
        try {
            GeneralDTO generalDTO = new GeneralDTO(Constant.IntegrationServiceConstant.EKYC, Constant.IntegrationServiceConstant.OCR, ekycocrRequestDTO);
            log.info("start call api ocr.................");
            ocrInfo = getInfoOcr(generalDTO);
        } catch (BaseException ex) {
            throw BaseException.badRequest(ErrorSubcriberKey.CUSTOM_MESSAGE).addParameter("message", Objects.requireNonNull(ex.problemDetail().getDetail()))
                .build();
        }

        log.info("[EKYC INFO] step2.1: ErrorCode = {}", ocrInfo.getErrCode());
        if (!ocrInfo.getErrCode().equals(Constant.EKYC_SUCCESS_CODE)) {
            log.info("OCR error ocr: {}", ocrInfo.getMessage());
            throw BaseException.badRequest(ErrorKey.INTERNAL_SERVER_ERROR).addParameter(Constant.Error.MESSAGE, ocrInfo.getMessage()).addParameter("idNo", ocrInfo.getDataOcr() == null ? "UNKNOWN" : ocrInfo.getDataOcr().getOcrFront().getId())
                .build();
        }
        return ocrInfo;
    }

    private String convertFileToBase64(MultipartFile file) {
        try {
            if (file == null) {
                return null;
            }
            // convert file binary to base 64 with startwith  data:image/ + type file;base64
            return "data:image/" + file.getContentType() + ";base64," + Base64.getEncoder().encodeToString(file.getBytes());
        } catch (Exception e) {
            throw BaseException.badRequest(ErrorKey.BAD_REQUEST)
                .addParameter(Constant.Error.MESSAGE, Constant.Error.CUS00033_03)
                .build();
        }
    }
    private void checkImage(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (fileSizeCheck(file, MAX_SIZE_IMAGE))
                throw BaseException.internalServerError(ErrorSubcriberKey.FILE_SIZE_OVER_OR_INCORRECT_FORMAT)
                    .build();
        }
    }
    private boolean fileSizeCheck(MultipartFile file, long targetSize) {
        return file.getSize() > targetSize;
    }

    public EKYCOCRResponseDTO getInfoOcr(GeneralDTO generalDTO) {
        try {
            HttpHeaders headers = getHeaders();
            HttpEntity<GeneralDTO> requestEntity = new HttpEntity<>(generalDTO, headers);
            return restTemplateOperation.postForEntity(urlGeneral, requestEntity, EKYCOCRResponseDTO.class).getBody();
        }catch (BaseException e){
            log.info(Constant.EXCEPTION_HAD_MAPPING, e);
            if (e.problemDetail() != null) {
                throw BaseException.badRequest(ErrorSubcriberKey.CUSTOM_MESSAGE)
                    .addParameter(Constant.Error.MESSAGE, Objects.requireNonNull(e.problemDetail().getDetail()))
                    .build();
            }
            throw BaseException.internalServerError(ErrorSubcriberKey.GET_OCR_FAIL)
                .build();
        }

    }



    public Resource fillTemplateToDocx(InputStream templateInputStream, Map<String, String> data) throws IOException {
        XWPFDocument doc = new XWPFDocument(templateInputStream);

        replaceInParagraphs(doc.getParagraphs(), data);

        for (XWPFHeader header : doc.getHeaderList()) {
            replaceInParagraphs(header.getParagraphs(), data);
            replaceInTables(header.getTables(), data);
        }

        for (XWPFFooter footer : doc.getFooterList()) {
            replaceInParagraphs(footer.getParagraphs(), data);
            replaceInTables(footer.getTables(), data);
        }

        replaceInTables(doc.getTables(), data);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.write(out);
        doc.close();

        return new ByteArrayResource(out.toByteArray()) {
            @Override
            public String getFilename() {
                return "generated-document.docx";
            }
        };
    }

    private void replaceInParagraphs(List<XWPFParagraph> paragraphs, Map<String, String> data) {
        if (paragraphs == null) return;
        for (XWPFParagraph paragraph : paragraphs) {
            replaceInParagraph(paragraph, data);
        }
    }

    /**
     * Thay thế placeholder, hỗ trợ cả ${key} và $ { key } (có khoảng trắng).
     */
    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> data) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;

        // Gom toàn bộ text của paragraph (bởi vì placeholder có thể bị split across runs)
        StringBuilder full = new StringBuilder();
        for (XWPFRun r : runs) {
            String t = r.getText(0);
            if (t != null) full.append(t);
        }
        String originalText = full.toString();
        if (originalText.isEmpty()) return;

        if (DEBUG) System.out.println("[DEBUG] Paragraph original: \"" + originalText + "\"");

        // Regex mới: tìm cả ${key} và $ { key } (cho phép space)
        Pattern placeholderPattern = Pattern.compile("\\$\\s*\\{\\s*([^}]+?)\\s*\\}");
        Matcher matcher = placeholderPattern.matcher(originalText);

        boolean found = false;
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            found = true;
            String rawKey = matcher.group(1);           // có thể chứa khoảng trắng
            String key = rawKey.trim();                // loại bỏ khoảng trắng thừa
            String replacement = data.get(key);
            if (replacement == null) {
                if (DEBUG) System.out.println("[DEBUG] No value for key: \"" + key + "\" -> replace with empty string");
                replacement = "";
            } else {
                if (DEBUG) System.out.println("[DEBUG] Replacing ${" + key + "} -> \"" + replacement + "\"");
            }
            // escape replacement để an toàn với appendReplacement
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        if (!found) return; // không tìm placeholder nào

        String replaced = sb.toString();
        if (DEBUG) System.out.println("[DEBUG] Paragraph replaced: \"" + replaced + "\"");

        // Xóa các runs cũ và tạo run mới (preserve style cơ bản từ run đầu)
        XWPFRun firstRun = runs.get(0);
        String font = firstRun.getFontFamily();
        int fontSize = firstRun.getFontSize();
        boolean bold = firstRun.isBold();
        boolean italic = firstRun.isItalic();

        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
        XWPFRun newRun = paragraph.createRun();
        if (font != null) newRun.setFontFamily(font);
        if (fontSize > 0) newRun.setFontSize(fontSize);
        newRun.setBold(bold);
        newRun.setItalic(italic);
        newRun.setText(replaced, 0);
    }

    private void replaceInTables(List<XWPFTable> tables, Map<String, String> data) {
        if (tables == null) return;
        for (XWPFTable table : tables) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    replaceInParagraphs(cell.getParagraphs(), data);
                    replaceInTables(cell.getTables(), data);
                }
            }
        }
    }


    // Example usage
    private Map<String, String> test() {
        Map<String, String> data = new HashMap<>();
        data.put("ownerName", "Nguyễn Văn A");
        data.put("ownerBirth", "01/01/1980");
        data.put("ownerId", "012345678");
        data.put("ownerPhone", "0912345678");

        data.put("tenantName", "Trần Thị B");
        data.put("tenantBirth", "02/02/1995");
        data.put("tenantId", "987654321");
        data.put("tenantPhone", "0987654321");

        data.put("address", "Số 123, Đường ABC, Quận XYZ, Thành phố H");
        data.put("rent", "2.500.000");
        data.put("deposit", "5.000.000");
        data.put("paymentMethod", "Trả tiền mặt hàng tháng");
        data.put("startDate", "01/12/2025");
        data.put("endDate", "30/11/2026");
        return  data;

    }
}


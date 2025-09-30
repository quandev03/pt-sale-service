package com.vnsky.bcss.projectbase.shared.utils;

import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.pdf.ContractUtils;
import com.vnsky.bcss.projectbase.shared.pdf.FillDataPdf;
import com.vnsky.common.exception.domain.BaseException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.*;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.*;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@UtilityClass
@Slf4j
public class PdfContractUtils {

    private static final Integer FINAL_DPI = 120;

    private static final Integer PREVIEW_DPI = 200;

    public static <T> ByteArrayOutputStream fillDataToPdf(byte[] templateData, Object fillingData, Class<T> tClass){
        try (PDDocument document = Loader.loadPDF(templateData);
            InputStream fontStream = PdfContractUtils.class.getResourceAsStream("/fonts/times.ttf")) {

            PDType0Font font = PDType0Font.load(document, fontStream, false);

            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

            if (acroForm != null) {
                PDResources resources = acroForm.getDefaultResources();
                if (resources == null) {
                    resources = new PDResources();
                    acroForm.setDefaultResources(resources);
                }
                String fontName = resources.add(font).getName();

                acroForm.setDefaultAppearance("/" + fontName + " 12 Tf 0 g");

                // Bỏ dòng này đi vì nó không cần thiết cho trường hợp này
//                acroForm.setNeedAppearances(true);

                List<Field> fillPdfFields = getFillPdfFields(tClass);
                for(Field field : fillPdfFields){
                    FillDataPdf fillData = field.getAnnotation(FillDataPdf.class);
                    String getterName = getGetterName(field);
                    PDField nameField = acroForm.getField(fillData.keyFill());

                    Method method = tClass.getMethod(getterName);
                    Object fieldValue = method.invoke(fillingData);
                    if(fieldValue == null){
                        continue;
                    }

                    if(nameField == null){
                        log.warn("[FILL_DATA_TO_PDF]: key fill {} not found", fillData.keyFill());
                    }else if(nameField instanceof PDTextField pdTextField){
                        handleFillText(pdTextField, fontName, fieldValue, fillData, document);

                        handleMultiFieldHasSameName(acroForm, fillData, fontName, fieldValue, document);
                    }else if(nameField instanceof PDCheckBox checkBoxField){
                        handleCheckBoxField(field, fieldValue, checkBoxField);
                    }
                }

                // Thêm dòng này để làm phẳng biểu mẫu sau khi điền dữ liệu
                acroForm.flatten();
            }

            ByteArrayOutputStream oos = new ByteArrayOutputStream();
            document.save(oos);
            return oos;
        }catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
            log.error("[FILL_DATA_TO_PDF]: Error when filling data to pdf", e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message(e.getMessage())
                .addProblemDetail(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage()))
                .build();
        }
    }

    private static void handleMultiFieldHasSameName(PDAcroForm acroForm, FillDataPdf fillDataPdf, String fontName, Object fieldValue, PDDocument document) throws IOException {
        if(!fillDataPdf.isMultiFieldSameName()){
            return;
        }

        List<PDField> fieldsAsSameName = getMultiFieldsByPrefix(acroForm, fillDataPdf.keyFill());
        for (PDField fieldHasSameName : fieldsAsSameName){
            handleFillText((PDTextField) fieldHasSameName, fontName, fieldValue, fillDataPdf, document);
        }
    }

    private static List<PDField> getMultiFieldsByPrefix(PDAcroForm acroForm, String prefix){
        return acroForm.getFields().stream().filter(pdField -> pdField.getFullyQualifiedName().startsWith(prefix + "_")).toList();
    }

    public ByteArrayOutputStream convertPdfToOneJpgFile(byte[] templateData, boolean isFinal){
        try (PDDocument document = Loader.loadPDF(templateData)){
            if (document.getDocumentCatalog().getAcroForm() != null) {
                document.getDocumentCatalog().getAcroForm().flatten();
            }

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();
            List<BufferedImage> images = new ArrayList<>();

            for (int page = 0; page < pageCount; page++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(
                    page,
                    isFinal ? FINAL_DPI : PREVIEW_DPI,
                    ImageType.RGB
                );
                images.add(bim);
            }

            int totalHeight = 0;
            int maxWidth = 0;
            for (BufferedImage img : images) {
                totalHeight += img.getHeight();
                maxWidth = Math.max(maxWidth, img.getWidth());
            }

            BufferedImage combined = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = combined.createGraphics();
            int currentY = 0;
            for (BufferedImage img : images) {
                g2d.drawImage(img, 0, currentY, null);
                currentY += img.getHeight();
            }
            g2d.dispose();

            // Xuất sang JPEG với nén
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(0.7f);

            jpgWriter.setOutput(ImageIO.createImageOutputStream(baos));
            jpgWriter.write(null, new IIOImage(combined, null, null), jpgWriteParam);
            jpgWriter.dispose();

            return baos;
        } catch (IOException e) {
            log.error("[CONVERT_PDF_TO_PNG]: Error when convert pdf to png", e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message(e.getMessage())
                .addProblemDetail(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage()))
                .build();
        }
    }

    private static void handleFillText(PDTextField textField, String fontName, Object fieldValue, FillDataPdf fillData, PDDocument document) throws IOException {
        if(fillData.image()){
            handleSignatureField(textField, document, fieldValue);
        }else{
            textField.setDefaultAppearance("/" + fontName + String.format(" %d Tf 0 g", fillData.fontSize()));
            textField.setValue(fieldValue.toString());
        }
    }

    private static void handleCheckBoxField(Field field, Object fieldValue, PDCheckBox checkBoxField) throws IOException {
        boolean isChecked = ContractUtils.handlePrimitiveValue(field, fieldValue);
        if(isChecked){
            checkBoxField.check();
        }
    }

    private static void handleSignatureField(PDField sigField, PDDocument document, Object signature) throws IOException {
        byte[] signatureData = (byte[]) signature;
        if(signatureData.length == 0){
            return;
        }

        PDAnnotationWidget widget = sigField.getWidgets().get(0);
        PDAppearanceDictionary appearance = new PDAppearanceDictionary();
        PDAppearanceStream appearanceStream = new PDAppearanceStream(document);

        // Cần set bbox và resources cho appearance stream
        PDRectangle rect = widget.getRectangle();
        appearanceStream.setBBox(new PDRectangle(rect.getWidth(), rect.getHeight()));
        appearanceStream.setResources(new PDResources());

        // Vẽ ảnh chữ ký từ byte[]
        PDImageXObject sigImage = PDImageXObject.createFromByteArray(document, signatureData, UUID.randomUUID().toString());

        PDPageContentStream cs = new PDPageContentStream(document, appearanceStream);
        cs.drawImage(sigImage, 0, 0, rect.getWidth(), rect.getHeight());
        cs.close();

        appearance.setNormalAppearance(appearanceStream);
        widget.setAppearance(appearance);
    }

    List<Field> getFillPdfFields(Class<?> clazz) {
        return Stream.of(clazz.getDeclaredFields())
            .filter(f -> f.getAnnotation(FillDataPdf.class) != null)
            .toList();
    }

    public static String getGetterName(Field field){
        Class<?> type = field.getType();
        String prefix = (type == boolean.class) ? "is" : "get";

        return prefix + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
    }
}

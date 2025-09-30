package com.vnsky.bcss.projectbase.shared.pdf;

import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.spire.doc.Document;
import com.spire.doc.documents.ImageType;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.TypeContract;
import com.vnsky.common.exception.domain.BaseException;
import jakarta.xml.bind.JAXBElement;
import kotlin.Pair;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.TraversalUtil;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.finders.RangeFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.w14.CTOnOff;
import org.docx4j.w14.CTSdtCheckbox;
import org.docx4j.wml.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

@Slf4j
public class ContractUtils {
    private static final String MESSAGE = "message";

    public enum TypeHandlerWord{
        MAIL_MERGE,
        CHECK_BOX
    }

    private static final Map<TypeHandlerWord, BiconsumerWithException<WordprocessingMLPackage,Object>> handlerWordMap;

    static {
        handlerWordMap = new EnumMap<>(TypeHandlerWord.class);
        handlerWordMap.put(TypeHandlerWord.MAIL_MERGE, ContractUtils::mailMerge);
        handlerWordMap.put(TypeHandlerWord.CHECK_BOX, ContractUtils::checkboxEnable);
    }

    private static final float[] matrix = {
        1/9f, 1/9f, 1/9f,
        1/9f, 1/9f, 1/9f,
        1/9f, 1/9f, 1/9f
    };

    private static final float[] sharpenMatrix = {
        0.0f, -1.0f, 0.0f,
        -1.0f, 5.0f, -1.0f,
        0.0f, -1.0f, 0.0f
    };


    // Getter để truy cập map từ bên ngoài nếu cần
    public static Map<TypeHandlerWord, BiconsumerWithException<WordprocessingMLPackage,Object>> getHandlerWordMap() {
        return Collections.unmodifiableMap(handlerWordMap);
    }

    private ContractUtils() {
    }

    public static <T> String replaceText(T object, String pageText) {
        try {
            Class<?> clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(FillDataPdf.class)) {
                    pageText = processFieldReplacement(clazz, field, object, pageText);
                }
            }
            return pageText;

        } catch (Exception e) {
            log.error("Error while replacing text: {}", e.getMessage());
            throw new IllegalStateException("Failed to replace text in document", e);
        }
    }

    private static <T> String processFieldReplacement(Class<?> clazz, Field field, T object, String pageText) {
        String getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        try {
            Method getter = clazz.getMethod(getterName);
            return replaceText(pageText, field, getter.invoke(object));
        } catch (NoSuchMethodException e) {
            log.warn("Getter method {} not found for field {}", getterName, field.getName());
            return pageText;
        } catch (Exception e) {
            log.error("Error processing field {}: {}", field.getName(), e.getMessage());
            return pageText;
        }
    }

    private static String replaceText(String pageText, Field field, Object value) {
        if (value == null) {
            return pageText;
        }

        FillDataPdf annotation = field.getAnnotation(FillDataPdf.class);
        String key = annotation.keyFill();

        if (key == null || key.isEmpty()) {
            return pageText;
        }

        if (value instanceof Object[] || value instanceof Collection) {
            return handleWithFieldIsArray(key, value, pageText);
        } else {
            return handleWithFieldIsSingle(key, value, pageText);
        }
    }

    private static String handleWithFieldIsArray(String key, Object value, String pageText) {
        if (value instanceof Object[] arrayValue) {
            for (int i = 0; i < arrayValue.length; i++) {
                pageText = pageText.replace(formatKeyOfPDF(String.format("%s[%d]", key, i)), arrayValue[i].toString());
            }
        } else if (value instanceof Collection<?> collectionValue) {
            int i = 0;
            for (Object item : collectionValue) {
                pageText = pageText.replace(formatKeyOfPDF(String.format("%s[%d]", key, i)), item.toString());
                i++;
            }
        }
        // replace all key index to space
        pageText = pageText.replaceAll(String.format("%s%s.*", "{{", key), "");
        return pageText;
    }

    private static String handleWithFieldIsSingle(String key, Object value, String pageText) {
        if (value != null) {
            pageText = pageText.replace(formatKeyOfPDF(key), value.toString());
        }
        return pageText;
    }

    private static String formatKeyOfPDF(String key) {
        return String.format("%s%s%s", "{{", key, "}}");
    }


    public static void mailMerge(WordprocessingMLPackage documentPart, Object data) {
        try {
            Map<DataFieldName, String> dataMergeField = new HashMap<>();
            Map<ImageKeyFiled, byte[]> dataMergeImage = new HashMap<>();
            objectToDataMergeField(dataMergeField, dataMergeImage, data);
            if (!dataMergeField.isEmpty())
                MailMerger.performMerge(documentPart, dataMergeField, true);
            if (!dataMergeImage.isEmpty()) {
                for (Map.Entry<ImageKeyFiled, byte[]> entry : dataMergeImage.entrySet()) {
                    insertImageAtMergeField(documentPart, entry.getValue(), entry.getKey());
                }
            }
        } catch (Exception e) {
            log.error("error mail merge ");
            log.error(e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .addParameter(MESSAGE, "mail merge fail")
                .build();
        }
    }

    public static void checkboxEnable(WordprocessingMLPackage documentPart, Object data) {
        try {
            log.info("[DOCX_CHECK_BOX] start enable checkbox");
            List<Integer> indexCheckboxNeedEnable = getIndexCheckBox(data);
            log.info("[DOCX_CHECK_BOX] index checkbox need enable : {} ", indexCheckboxNeedEnable);
            enableCheckBox(documentPart, indexCheckboxNeedEnable);
        } catch (Exception e) {
            log.error("error enable checkbox ");
            log.error(e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .addParameter(MESSAGE, "enable checkbox fail")
                .build();
        }
    }


    private static List<Integer> getIndexCheckBox(Object data) throws IllegalAccessException {
        try {
            List<Integer> indexCheckBoxNeedEnable = new ArrayList<>();
            Field[] fields = data.getClass().getDeclaredFields();
            for (Field field : fields) {
                if(field.isAnnotationPresent(CheckboxDocx.class) && valueIsTrue(field, data)){
                    CheckboxDocx checkboxDocx = field.getAnnotation(CheckboxDocx.class);
                    indexCheckBoxNeedEnable.add(checkboxDocx.index());
                }
            }
            return indexCheckBoxNeedEnable;
        } catch (Exception e) {
            log.error("error get index on check box field ..........");
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private static boolean valueIsTrue(Field field, Object data) throws IllegalAccessException {
        try {
            Object value = getValueFromGetter(field, data);
            if (value == null) {
                return false;
            }

            if (field.getType().isPrimitive()) {
                return handlePrimitiveValue(field, value);
            }

            return handleObjectValue(value);
        } catch (Exception e) {
            log.error("Error checking value is true: {}", e.getMessage());
            log.error(e.getMessage(), e);
            throw new IllegalAccessException("valueIsTrue Cannot access field value: " + e.getMessage());
        }
    }

    public static boolean handlePrimitiveValue(Field field, Object value) {
        Class<?> type = field.getType();

        if (type == boolean.class) {
            return (boolean) value;
        }
        if (type == char.class) {
            return (char) value != '0' && (char) value != '\0';
        }

        return handlePrimitiveNumber(type, value);
    }

    private static boolean handlePrimitiveNumber(Class<?> type, Object value) {
        if (type == int.class) return (int) value > 0;
        if (type == long.class) return (long) value > 0;
        if (type == double.class) return (double) value > 0;
        if (type == float.class) return (float) value > 0;
        if (type == byte.class) return (byte) value > 0;
        if (type == short.class) return (short) value > 0;
        return false;
    }

    private static boolean handleObjectValue(Object value) {
        if (value instanceof Boolean boolValue) {
            return boolValue;
        }
        if (value instanceof Number number) {
            return number.doubleValue() > 0;
        }
        if (value instanceof String str) {
            return isStringTrue(str);
        }
        if (value instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }
        if (value instanceof Map<?,?> map) {
            return !map.isEmpty();
        }
        if (value.getClass().isArray()) {
            return Array.getLength(value) > 0;
        }
        return false;
    }

    private static boolean isStringTrue(String str) {
        String strValue = str.trim().toLowerCase();
        return "true".equals(strValue)
            || "1".equals(strValue)
            || "yes".equals(strValue);
    }

    private static Object getValueFromGetter(Field field, Object data) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String fieldName = field.getName();
        String getterPrefix = field.getType().equals(boolean.class) ? "is" : "get";
        String getterName = getterPrefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        if (fieldName.startsWith("is")){
            getterName = field.getName();
        }
        try {
            Method getter = data.getClass().getMethod(getterName);
            return getter.invoke(data);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // Thử với prefix khác nếu không tìm thấy method
            getterPrefix = getterPrefix.equals("is") ? "get" : "is";
            getterName = getterPrefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method getter = data.getClass().getMethod(getterName);
            return getter.invoke(data);
        }
    }


    private static void objectToDataMergeField(Map<DataFieldName, String> dataMergeField, Map<ImageKeyFiled, byte[]> dataImageMerge, Object data) throws IllegalAccessException {
        try {
            Field[] fields = data.getClass().getDeclaredFields();
            for (Field field : fields) {
                addPairMergeFieldToMap(data, field, dataMergeField, dataImageMerge, false, 0);
            }
            log.info("data merge field : {} ", dataMergeField);
            log.info("data image size : {} ", dataImageMerge.size());
        } catch (Exception e) {
            log.error("error convert object data to Map data merge field ..........");
            log.error(e.getMessage(), e);
            throw e;
        }
    }


    public static void objectToDataMergeFieldWithSpire(Map<String, String> dataMergeField, Map<String, String> dataImageMerge, Object data) throws IllegalAccessException {
        try {
            Field[] fields = data.getClass().getDeclaredFields();
            for (Field field : fields) {
                addPairMergeFieldToMapWithSpire(data, field, dataMergeField, dataImageMerge, false, 0);
            }
            log.info("data merge field : {} ", dataMergeField);
            log.info("data image size : {} ", dataImageMerge.size());
        } catch (Exception e) {
            log.error("error convert object data to Map data merge field ..........");
            log.error(e.getMessage(), e);
            throw e;
        }
    }


    /**
     * add pair merge field to map
     *
     * @param data
     * @param field
     * @param result
     * @param indexConcatField
     * @param index
     * @throws IllegalAccessException
     */
    private static void addPairMergeFieldToMap(Object data, Field field, Map<DataFieldName, String> result, Map<ImageKeyFiled, byte[]> dataImage, boolean indexConcatField, int index) throws IllegalAccessException {
        try {
            if (field.isAnnotationPresent(NotCheckMergeField.class)) {
                return;
            }

            // Sử dụng getter method thay vì truy cập trực tiếp field
            String getterName;
            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                getterName = field.getName().startsWith("is") && Character.isUpperCase(field.getName().charAt(2))
                    ? field.getName()
                    : "is" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            } else {
                getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            }

            Method getter;
            try {
                getter = data.getClass().getMethod(getterName);
            } catch (NoSuchMethodException e) {
                getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                getter = data.getClass().getMethod(getterName);
            }
            Object value = getter.invoke(data);

            if (value != null) {
                if (value instanceof Collection<?> collection) {
                    handleWithValueIsCollection(result, dataImage, collection);
                } else {
                    handleWithValueIsSingleValue(field, result, dataImage, indexConcatField, index, value);
                }
            }
        } catch (NoSuchMethodException e) {
            log.warn("Getter method not found for field {}: {}", field.getName(), e.getMessage());
            throw new IllegalAccessException("Cannot find getter method for field: " + field.getName());
        } catch (Exception e) {
            log.error("Error adding merge field to map: {}", e.getMessage());
            throw new IllegalAccessException("Cannot access field value: " + e.getMessage());
        }
    }

    private static void addPairMergeFieldToMapWithSpire(Object data, Field field, Map<String, String> result, Map<String, String> dataImage, boolean indexConcatField, int index) throws IllegalAccessException {
        try {
            // Sử dụng getter method thay vì truy cập trực tiếp field
            String getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
            Method getter = data.getClass().getMethod(getterName);
            Object value = getter.invoke(data);

            if (value != null) {
                if (value instanceof Collection<?> collection) {
                    handleWithValueIsCollectionWithSpire(result, dataImage, collection);
                } else {
                    handleWithValueIsSingleValueWithSpire(field, result, dataImage, indexConcatField, index, value);
                }
            }
        } catch (Exception e) {
            log.error("Error adding merge field to map with Spire: {}", e.getMessage());
            throw new IllegalAccessException("Cannot access field value: " + e.getMessage());
        }
    }


    /**
     * handle with value is single value
     *
     * @param field
     * @param result
     * @param indexConcatField
     * @param index
     * @param value
     */
    private static void handleWithValueIsSingleValue(Field field, Map<DataFieldName, String> result, Map<ImageKeyFiled, byte[]> dataImage, boolean indexConcatField, int index, Object value) {
        String key = null;
        if (field.isAnnotationPresent(FillDataPdf.class)) {
            key = field.getAnnotation(FillDataPdf.class).keyFill();
        }
        // get Annotation of field
        FillDataPdf annotationFillData = field.getAnnotation(FillDataPdf.class);
        if (key != null && !key.isEmpty()) {
            if (indexConcatField) {
                key = String.format("%s_%d", key, index);
            }
            if (annotationFillData.image())
                dataImage.put(new ImageKeyFiled(key,annotationFillData.maxWidth(), 0), (byte[]) value);
            else
                result.put(new DataFieldName(key), value.toString());
        }
    }


    private static void handleWithValueIsSingleValueWithSpire(Field field, Map<String, String> result, Map<String, String> dataImage, boolean indexConcatField, int index, Object value) {
        String key = field.getAnnotation(FillDataPdf.class).keyFill();
        // get Annotation of field
        FillDataPdf annotationFillData = field.getAnnotation(FillDataPdf.class);
        if (key != null && !key.isEmpty()) {
            if (indexConcatField) {
                key = String.format("%s_%d", key, index);
            }
            if (annotationFillData.image())
                dataImage.put(key, value.toString());
            else
                result.put(key, value.toString());
        }
    }

    /**
     * handle with value is collection
     *
     * @param result
     * @param value
     * @throws IllegalAccessException
     */
    private static void handleWithValueIsCollection(Map<DataFieldName, String> result, Map<ImageKeyFiled, byte[]> dataImage, Collection<?> value) throws IllegalAccessException {
        Collection<?> collectionValue = value;
        int i = 1;
        for (Object item : collectionValue) {
            Field[] fields = item.getClass().getDeclaredFields();
            for (Field fieldItem : fields) {
                addPairMergeFieldToMap(item, fieldItem, result, dataImage, true, i);
            }
            i++;
        }
    }


    private static void handleWithValueIsCollectionWithSpire(Map<String, String> result, Map<String, String> dataImage, Collection<?> value) throws IllegalAccessException {
        Collection<?> collectionValue = value;
        int i = 1;
        for (Object item : collectionValue) {
            Field[] fields = item.getClass().getDeclaredFields();
            for (Field fieldItem : fields) {
                addPairMergeFieldToMapWithSpire(item, fieldItem, result, dataImage, true, i);
            }
            i++;
        }
    }

    /**
     * convert word to pdf byte array output stream
     *
     * @param dataDocx
     * @return
     */
    public static ByteArrayOutputStream convertWordToPdfByteArrayOutputStream(InputStream dataDocx, TypeContract type) {
        Document document = new Document(dataDocx);
        try {
            if (type == TypeContract.PDF) {
                return convertWordToPdf(document);
            } else if (type == TypeContract.PNG) {
                return convertWordToImagePNG(document);
            }
            return null;
        } catch (Exception e) {
            log.error("error convert word to pdf byte array output stream");
            log.error(e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .addParameter(MESSAGE, "convert word to pdf fail")
                .build();
        }finally {
            document.close();
            document.dispose();
        }
    }

    public static Pair<ByteArrayOutputStream, ByteArrayOutputStream> convertWordToPdfAndPng(Document document) {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

        try {
            // Khởi tạo file PDF đầu ra
            com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4);
            PdfWriter.getInstance(pdfDoc, pdfOutputStream);
            pdfDoc.open();

            int pageCount = document.getPageCount();
            BufferedImage[] images = document.saveToImages(0, document.getPageCount(), ImageType.Bitmap, 200, 200);

            //combieImage
            BufferedImage bufferedImageCombie = ImageUtils.combineImages(images);
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ImageIO.write(filterImage(bufferedImageCombie), "png", imageOutputStream);
            pngOutputStream.write(imageOutputStream.toByteArray());

            for (int i = 0; i < pageCount; i++) {
                // Chuyển từng trang thành ảnh
                BufferedImage image = images[i];

                // Chuyển ảnh thành byte[]
                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", imageStream);

                // Thêm ảnh vào PDF
                com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(imageStream.toByteArray());

                // Tự động scale vừa khổ A4
                pdfImage.scaleToFit(PageSize.A4.getWidth() - 20, PageSize.A4.getHeight() - 20);
                pdfImage.setAlignment(Element.ALIGN_CENTER);

                pdfDoc.add(pdfImage);

                // Nếu không phải trang cuối thì thêm trang mới
                if (i < pageCount - 1) {
                    pdfDoc.newPage();
                }
            }

            pdfDoc.close();
            return new Pair<>(pdfOutputStream, pngOutputStream);

        } catch (Exception e) {
            log.error("Exception when convert docx to pdf");
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message("Lỗi khi chuyển Word sang PDF qua PNG")
                .build();
        }
    }

    public static ByteArrayOutputStream convertWordToPdf(Document document) {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

        try {
            // Khởi tạo file PDF đầu ra
            com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document(PageSize.A4);
            PdfWriter.getInstance(pdfDoc, pdfOutputStream);
            pdfDoc.open();

            int pageCount = document.getPageCount();
            BufferedImage[] images = document.saveToImages(0, document.getPageCount(), ImageType.Bitmap, 200, 200);

            for (int i = 0; i < pageCount; i++) {
                // Chuyển từng trang thành ảnh
                BufferedImage image = images[i];

                // Chuyển ảnh thành byte[]
                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", imageStream);

                // Thêm ảnh vào PDF
                com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(imageStream.toByteArray());

                // Tự động scale vừa khổ A4
                pdfImage.scaleToFit(PageSize.A4.getWidth() - 20, PageSize.A4.getHeight() - 20);
                pdfImage.setAlignment(Element.ALIGN_CENTER);

                pdfDoc.add(pdfImage);

                // Nếu không phải trang cuối thì thêm trang mới
                if (i < pageCount - 1) {
                    pdfDoc.newPage();
                }
            }

            pdfDoc.close();
            return pdfOutputStream;

        } catch (Exception e) {
            log.error("Exception when convert docx to pdf");
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .message("Lỗi khi chuyển Word sang PDF qua PNG")
                .build();
        }
    }

    public static ByteArrayOutputStream convertWordToImagePNG(Document document) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BufferedImage[] images = document.saveToImages(0, document.getPageCount(), ImageType.Bitmap, 200, 200);
            //combieImage
            BufferedImage bufferedImageCombie = ImageUtils.combineImages(images);
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            ImageIO.write(filterImage(bufferedImageCombie), "png", imageOutputStream);
            outputStream.write(imageOutputStream.toByteArray());
            return outputStream;
        } catch (Exception e) {
            log.error("error convert word to image byte array output stream");
            log.error(e.getMessage(), e);
            throw BaseException.badRequest(ErrorCode.GEN_CONTRACT_FAIL)
                .addParameter(MESSAGE, "convert word to image fail")
                .build();
        }
    }

    public static void insertImageAtMergeField(WordprocessingMLPackage wordMLPackage, byte[] imageData,ImageKeyFiled imageKeyFiled) throws Exception {
        if(imageData.length == 0)
            return;
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
        org.docx4j.wml.Document wmlDoc = mainDocumentPart.getJaxbElement();
        Body body = wmlDoc.getBody();
        List<Object> paragraphs = body.getContent();

        RangeFinder rt = new RangeFinder();
        new TraversalUtil(paragraphs, rt);
        for (CTBookmark bm : rt.getStarts()) {
            // This can be done on a single bookmark, or all bookmarks can be processed with a map
            if (bm.getName().equals(imageKeyFiled.getFieldName())) {
                // Read the image and convert it to a byte array, because docx4j can only insert images in a byte array
                // wear an inline image
                BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imageData);
                // The most one is to limit the width of the image, the basis for scaling
                Inline inline = imagePart.createImageInline(null, null, 0, 1, false, imageKeyFiled.getMaxWith());
                inline.getCNvGraphicFramePr().getGraphicFrameLocks().setNoChangeAspect(true);
                // Get the parent paragraph of the bookmark
                P p = (P) (bm.getParent());
                if (p == null) {
                    p = Context.getWmlObjectFactory().createP();
                    ((ContentAccessor) bm.getParent()).getContent().add(p);
                }

                // Tạo một Run mới chứa hình ảnh
                ObjectFactory factory = Context.getWmlObjectFactory();
                R run = factory.createR();
                // drawing is understood as a canvas?
                Drawing drawing = factory.createDrawing();
                drawing.getAnchorOrInline().add(inline);
                run.getContent().add(drawing);
                p.getContent().add(run);
            }

        }
    }

    public static void enableCheckBox(WordprocessingMLPackage wordprocessingMLPackage, List<Integer> indexEnables) throws XPathBinderAssociationIsPartialException, jakarta.xml.bind.JAXBException {
        MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
        List<Object> list = mainDocumentPart.getJAXBNodesViaXPath("//w14:checkbox", false);
        for(int indexEnable : indexEnables){
            if(indexEnable >= list.size())
                continue;
            JAXBElement<CTSdtCheckbox> elementCheckBox = (JAXBElement<CTSdtCheckbox>) list.get(indexEnable);
            CTSdtCheckbox checkbox = elementCheckBox.getValue();
            List<Object> list2 = mainDocumentPart.getJAXBNodesViaXPath("../..//w:t", elementCheckBox, false);
            Text chkSymbol = ((JAXBElement<Text>) list2.get(0)).getValue();
            CTOnOff checkedVal = checkbox.getChecked();
            if (checkedVal.getVal().compareTo("0") == 0) {
                checkedVal.setVal("1");
                chkSymbol.setValue(new String(Character.toChars(0x2612)));
            } else {
                checkedVal.setVal("0");
                chkSymbol.setValue(new String(Character.toChars(0x2610)));
            }
        }
    }

    private static BufferedImage filterImage(BufferedImage images) {
        Kernel kernel = new Kernel(3, 3, matrix);

        ConvolveOp op = new ConvolveOp(kernel);

        BufferedImage blurredImage = op.filter(images, null);


        Kernel sharpenKernel = new Kernel(3, 3, sharpenMatrix);

        ConvolveOp sharpenOp = new ConvolveOp(sharpenKernel);

        return sharpenOp.filter(blurredImage, null);
    }
}

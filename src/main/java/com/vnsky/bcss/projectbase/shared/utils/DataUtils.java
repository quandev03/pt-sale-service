package com.vnsky.bcss.projectbase.shared.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.common.exception.domain.BaseException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Base64;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
@UtilityClass
@SuppressWarnings("all")
public class DataUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();


    public static String object2Json(Object object) {
        try {
            if ((ObjectUtils.isEmpty(object))) {
                return null;
            }
            JsonNode jsonNode;
            if (object instanceof String) {
                jsonNode = OBJECT_MAPPER.readTree((String) object);
            } else {
                jsonNode = OBJECT_MAPPER.valueToTree(object);
            }
            return jsonNode.toString();
        } catch (Exception e) {
            log.error("object2Json error: {}", e.getMessage());
            return null;
        }
    }

    public static String camelToSnake(String str) {

        // Empty String
        StringBuilder result = new StringBuilder();

        // Append first character(in lower case)
        // to result string
        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));

        // Traverse the string from
        // ist index to last index
        for (int i = 1; i < str.length(); i++) {

            char ch = str.charAt(i);

            // Check if the character is upper case
            // then append '_' and such character
            // (in lower case) to result string
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            }

            // If the character is lower case then
            // add such character into result string
            else {
                result.append(ch);
            }
        }
        // return the result
        return result.toString();
    }

    public static MediaType getMediaTypeForFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "png":
            case "PNG":
                return MediaType.IMAGE_PNG;
            case "jpg":
            case "JPG":
            case "jpeg":
            case "JPEG":
                return MediaType.IMAGE_JPEG;
            case "gif":
                return MediaType.IMAGE_GIF;
            case "pdf":
            case "PDF":
                return MediaType.APPLICATION_PDF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public static String formatName(String input) {
        String[] words = input.trim().split("\\s+");
        StringBuilder formattedName = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
            }
        }
        return formattedName.toString().trim();
    }

    public int parseIntWithDefault(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String hashWithMD5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(text.getBytes());

            // Convert byte array to hexadecimal String
            StringBuilder sb = new StringBuilder();
            for (byte b : hashInBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public static String removeAccent(String s) {
        if (s == null) return null;
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    // Nén JSON và trả về chuỗi base64
    public static String compress(String json) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(byteStream)) {
                gzip.write(json.getBytes(StandardCharsets.UTF_8));
            }
            byte[] compressed = byteStream.toByteArray();
            return Base64.getEncoder().encodeToString(compressed);
        } catch (IOException e) {
            log.error("Error while compressing JSON: ", e);
            throw BaseException.badRequest(ErrorCode.COMPRESS_JSON_FAIL)
                .message("Lỗi khi nén dữ liệu cho chuỗi: " + json)
                .build();
        }
    }

    // Giải nén JSON từ chuỗi base64
    public static String decompress(String compressedBase64){
        byte[] compressed = Base64.getDecoder().decode(compressedBase64);
        try(GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            return new String(gzip.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error when decompress JSON: ", e);
            throw BaseException.badRequest(ErrorCode.DE_COMPRESS_JSON_FAIL)
                .message("Lỗi khi giải nén dữ liệu cho chuỗi" + compressedBase64)
                .build();
        }
    }
}

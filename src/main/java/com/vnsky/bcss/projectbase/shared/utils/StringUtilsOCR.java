package com.vnsky.bcss.projectbase.shared.utils;

import lombok.experimental.UtilityClass;

import java.text.Normalizer;
import java.util.regex.Pattern;

@UtilityClass
public class StringUtilsOCR {

    public String removeVietnameseAccent(String inputName) {
        String normalizedString = Normalizer.normalize(inputName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String noAccentString = pattern.matcher(normalizedString).replaceAll("");
        // Replace "Đ" and "đ" cause this character can't normalize
        noAccentString = noAccentString.replace("Đ", "D").replace("đ", "d");
        noAccentString = noAccentString.replaceAll("\\s+", "").toUpperCase();
        return noAccentString;
    }

    public String toFilePath(String... parts) {
        return String.join("/", parts);
    }

    public String buildLikeOperator(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return "%" + s + "%";
    }
}

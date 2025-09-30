package com.vnsky.bcss.projectbase.domain.dto;

import com.vnsky.bcss.projectbase.shared.constant.Constant;
import com.vnsky.bcss.projectbase.shared.enumeration.domain.ErrorCode;
import com.vnsky.bcss.projectbase.shared.utils.ErrorTranslator;
import com.vnsky.excel.dto.ExcelData;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;


public interface NumberUploadDTO {

    String getIsdn();

    String getResult();

    String getReason();

    void setReason(String reason);

    void setResult(String result);

    long getIsdnTruncated();

    default long doIsdnTruncated() {
        try {
            if (getIsdn() == null) return -1;
            String isdnStr = getIsdn();
            if (isdnStr.length() <= 9) {
                return Long.parseLong(isdnStr);
            } else if (isdnStr.startsWith("84")) {
                return Long.parseLong(isdnStr.substring(2));
            } else {
                return Long.parseLong(isdnStr);
            }
        } catch (Exception e) {
            try {
                return (long) Double.parseDouble(getIsdn());
            } catch (Exception ex) {
                return -1;
            }
        }
    }

    default boolean isIsdnPrefixWith(List<String> prefixes) {
        String number = String.valueOf(getIsdnTruncated());
        return prefixes.stream()
            .map(Integer::parseInt)
            .map(String::valueOf)
            .anyMatch(number::startsWith);
    }

    Set<String> getErrors();

    void appendError(String error);

    void appendError(Set<String> errors);

    void finalizeResult();

    static <I extends NumberUploadDTO> int collectNumberErrors(Long isdn, List<I> isdnUploads,
                                                               ErrorTranslator errorTranslator,
                                                               BiConsumer<I, Set<String>> additionalValidator) {
        // validate isdn
        AtomicInteger errorCounter = new AtomicInteger();
        Set<String> messageErrors = new HashSet<>();
        if (isdnUploads.size() > 1 && isdn >= 0) {
            messageErrors.add(errorTranslator.apply(ErrorCode.ISDN_RESOURCE_NOT_UNIQUE));
        } else if (isdn < 0) { // isdn after being trimmed must have thier length from 9 to 10 digits
            messageErrors.add(errorTranslator.apply(ErrorCode.ISDN_INVALID));
        } else if (isdn <= 99999999L || isdn > 9999999999L) {
            messageErrors.add(errorTranslator.apply(ErrorCode.ISDN_INVALID_LENGTH));
        }
        // validate each entry with same isdn
        isdnUploads.forEach(isdnUpload -> {
            isdnUpload.appendError(messageErrors);
            Set<String> additionalMessageErrors = new HashSet<>();
            additionalValidator.accept(isdnUpload, additionalMessageErrors);
            isdnUpload.appendError(additionalMessageErrors);
            if (isdnUpload.getIsdn().length() > 12 || isdnUpload.getIsdn().length() < 9) { // 10-11 digits length at original form
                isdnUpload.appendError(errorTranslator.apply(ErrorCode.ISDN_INVALID_LENGTH));
            }
            if (!isdnUpload.getErrors().isEmpty()) {
                errorCounter.incrementAndGet();
            }
        });
        return errorCounter.get();
    }

    static <C extends NumberUploadDTO> Map<Long, List<C>> fromExcel(ExcelData<C> excelData) {
        Map<Long, List<C>> hmIsdn = new HashMap<>();
        excelData.getDataLines().forEach(isdnUploadDTO -> {
            if (!hmIsdn.containsKey(isdnUploadDTO.getIsdnTruncated())) {
                hmIsdn.put(isdnUploadDTO.getIsdnTruncated(), new ArrayList<>());
            }
            hmIsdn.get(isdnUploadDTO.getIsdnTruncated()).add(isdnUploadDTO);
        });
        return hmIsdn;
    }

}

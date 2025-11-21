package com.vnsky.bcss.projectbase.shared.utils;

import lombok.experimental.UtilityClass;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

@UtilityClass
public class VNPaySignatureUtils {

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String HASH_PARAM = "vnp_SecureHash";

    public String buildQueryString(Map<String, String> params) {
        return buildQueryString(params, false);
    }

    public String buildSignedQueryString(Map<String, String> params, String hashSecret) {
        String query = buildQueryString(params);
        String secureHash = generateSignature(params, hashSecret);
        return query + "&" + HASH_PARAM + "=" + secureHash;
    }

    public String buildSignedUrl(String baseUrl, Map<String, String> params, String hashSecret) {
        return baseUrl + "?" + buildSignedQueryString(params, hashSecret);
    }

    public String generateSignature(Map<String, String> params, String hashSecret) {
        String data = buildQueryString(params, true);
        return hmacSHA512(hashSecret, data);
    }

    public boolean validateSignature(Map<String, String> params, String hashSecret) {
        String receivedHash = params.get(HASH_PARAM);
        if (receivedHash == null) {
            return false;
        }

        SortedMap<String, String> filtered = prepareParams(params, true);
        String calculated = hmacSHA512(hashSecret, buildQueryString(filtered));
        return receivedHash.equalsIgnoreCase(calculated);
    }

    private String buildQueryString(Map<String, String> params, boolean excludeHashParam) {
        SortedMap<String, String> filtered = prepareParams(params, excludeHashParam);
        return buildQueryString(filtered);
    }

    private String buildQueryString(SortedMap<String, String> params) {
        return params.entrySet()
            .stream()
            .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
            .collect(Collectors.joining("&"));
    }

    private SortedMap<String, String> prepareParams(Map<String, String> params, boolean excludeHashParam) {
        SortedMap<String, String> sorted = new TreeMap<>();
        params.forEach((key, value) -> {
            if (value == null || value.isBlank()) {
                return;
            }
            if (excludeHashParam && HASH_PARAM.equals(key)) {
                return;
            }
            sorted.put(key, value);
        });
        return sorted;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
            .replaceAll("\\+", "%20");
    }

    private String hmacSHA512(String secret, String data) {
        try {
            Mac hmac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA512);
            hmac.init(secretKey);
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to generate VNPay signature", ex);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}


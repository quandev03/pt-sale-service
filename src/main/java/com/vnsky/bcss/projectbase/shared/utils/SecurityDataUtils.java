package com.vnsky.bcss.projectbase.shared.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
@Slf4j
public class SecurityDataUtils {
    private static final String TRANSFORMATION_CODE = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    public static final String PRIVATE_KEY_FIX = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC7VF8l1A9dZL64VpoG+41RlJB4vecCFeqLiBzCg6EQF0PsRBkGvkRiLuVTbXIdEXqayq/Nx5P10aH21AYig+dwf7EURUx83PdI+1BpUGnFxheaBXM5Mm/3+aggho2pRoQjLpL4khqUhTXxS4HBkpg7O8eaUT4RUxLqKvqSpm84/M+PnZpEU6RvftP0YxEUJzMM3/l0wDkFGPDmigDeQSCPGk9EvbZvy1p3N3j+khP+twVxzewQrRTq6bSLEyhctn3fzXduRKebl7qh0559Ydu3ccs9d0n6sLugOmhuh6ZArZK6IGIKIqs10DWi1tdCyq24Co8Si5hCJ4L+XQVLhobzAgMBAAECggEAB5T0HudU0d5ce8CBjmvK/fTJ3iEtgt+ZNRmGtHa4Y5rVdHATJTs5MFIelQBWgIevEynF8Mm1mtrnt8JAF1CIYvzAEYZ2gI0+8vS8/e4zwb3xQZob3kJE1uADZoGbpTDF81iUuTvMmJtFoiUAtPF2eNtECmJkZTYcC18+8gWLdd5vvIh3v8edvgl/k5Z+7Exbw/QakEVyL0bpuTxxHDruLyuPP5w5/P8HC/Y5k0k7/384upLGVrRJCYF+JcH6ymIViGYt5zYJ8mofGG2lyw1e9++78FvoQUbTBIVT1TGnFf33q4i8HV3nHQzss/jhSVAa+zlgQxW2P0h9VRwgBbLRxQKBgQD2ZoDZ0Gk+BWw2ExZn844AztS9cT2rs+5uzerY27qPpTxXuAIYG5q/8We51EcFphIBitfi8AQa9l4JzNNNeyWeKXh5yljqKL65wXHTPNWkYht/k+S49fJMWP08eOQHiAyKyBEpB7iMAtAasdycjzFTDCnJKVLmM3315rKdLoFs7wKBgQDCoLhZ7rFb4THHhFnJC29aEm7JIJuNlcb0i77TyTFzOeac3BGaD5FafM9CtgODP8V4UPreqfxXD9sVBxqxZNXXH8zIOylx1dz1oMG0yywivxhxY88FTHH329XSGqc2VCd1Jkgy97MvFH+rrA8yJD1lvRIwXks+AE6sWlwrne2OPQKBgQDtNEKo7XTTaz8AU6HHUvWKm5K8CS8Yg/BupTeHqwhPzv9nZtJFSRXR4GE9yTjSHEzLRVGNvAgHuNNQ1ek5dusA9uVvQbgVPEJX6v1cIP1+8Z+NUt/mm4a1rxmixLKo+XUxiWbTvAYSa4bRnBZIdaVDULtNV8XhrQJ+3qdmn+Wi6wKBgCX/cN6F/nR8u9ZDHbBlniDMtnpn9lanJke6PNBmWFQxurdnM7yLS4qKEYDpvjfSrH/393KfZ1esDc7GK0z07dbpK3bwLgA+i7wc3ZSpkf1ap7ADl3hmW13TuqQhdgHiXi2+ApyIxZ/sdZyTpAfbEW2ag0GUN0Zsku+NWK9fTvDVAoGBAKwxAt/woHuwrEXaEAGD/+MNA50hpd00jmafYqsSnp5zGFCo4FN/U32n7pAJ3BxYSvcbybB3QKR6sNiBmDH62qvweYWhE85YzQGhZ8mvLnaThdvkJtGzA0qw/PYxTlORkH83x6JngnZoQn8F5dHTv8+Ftf4kdubmMyq9SUzqbgLi";
    public static final String PUBLIC_KEY_FIX = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1RfJdQPXWS+uFaaBvuNUZSQeL3nAhXqi4gcwoOhEBdD7EQZBr5EYi7lU21yHRF6msqvzceT9dGh9tQGIoPncH+xFEVMfNz3SPtQaVBpxcYXmgVzOTJv9/moIIaNqUaEIy6S+JIalIU18UuBwZKYOzvHmlE+EVMS6ir6kqZvOPzPj52aRFOkb37T9GMRFCczDN/5dMA5BRjw5ooA3kEgjxpPRL22b8tadzd4/pIT/rcFcc3sEK0U6um0ixMoXLZ93813bkSnm5e6odOefWHbt3HLPXdJ+rC7oDpoboemQK2SuiBiCiKrNdA1otbXQsqtuAqPEouYQieC/l0FS4aG8wIDAQAB";
    private static final String SHA_KEY = "SHA-256";
    private static final String MGF1 = "MGF1";
    public static PrivateKey genRSAPrivateKey(String privateKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey genRSAPublicKey(String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static String encryptAndEncodeUrl(String data, String publicKeyBase64) {
        try {
            PublicKey pubKey = genRSAPublicKey(publicKeyBase64);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION_CODE);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                SHA_KEY, MGF1, MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.ENCRYPT_MODE, pubKey, oaepParams);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            String base64 = Base64.getEncoder().encodeToString(encrypted);
            return URLEncoder.encode(base64, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    // URLDecode + Base64 + Decrypt
    public static String decodeUrlAndDecrypt(String urlParam, String privateKeyBase64) throws InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        PrivateKey privKey = genRSAPrivateKey(privateKeyBase64);
        byte[] encrypted = Base64.getDecoder().decode(urlParam);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION_CODE);
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
            SHA_KEY, MGF1, MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT
        );
        cipher.init(Cipher.DECRYPT_MODE, privKey, oaepParams);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}

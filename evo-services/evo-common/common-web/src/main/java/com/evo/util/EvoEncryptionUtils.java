package com.evo.util;

import com.evo.exception.AppException;
import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Log4j2
public final class EvoEncryptionUtils {

    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    public static String encrypt(String sSrc, String sKey) {
        if (sKey == null || sKey.length() != 16) {
            return null;
        }
        try {

            byte[] rawKey = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, "AES");

            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);

            byte[] encryptedBytes = cipher.doFinal(sSrc.getBytes(StandardCharsets.UTF_8));

            byte[] encryptedWithIv = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedWithIv, iv.length, encryptedBytes.length);

            // update return Base64.toString()
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedWithIv);

        } catch (Exception e) {
            log.error("Encrypt token fail {}", e);
            throw new RuntimeException("exception.encrypt.fail" + e);
        }
    }

    public static String decrypt(String encryptedBase64, String sKey) {
        if (sKey == null || sKey.length() != 16) {
            return null;
        }
        try {
            byte[] rawKey = sKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(rawKey, "AES");

            // update
            byte[] encryptedWithIv = Base64.getUrlDecoder().decode(encryptedBase64);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedBytes = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decrypt token fail {}", e);
            throw new RuntimeException("exception.decrypt.fail" + e);
        }
    }
}

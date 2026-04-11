package de.hahnphilipp.littleminus.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import android.util.Base64;

public class PKCEUtil {

    public static class PKCEPair {
        public final String codeVerifier;
        public final String codeChallenge;

        public PKCEPair(String codeVerifier, String codeChallenge) {
            this.codeVerifier = codeVerifier;
            this.codeChallenge = codeChallenge;
        }
    }

    public static PKCEPair generatePKCE() {
        try {
            SecureRandom secureRandom = new SecureRandom();

            // 64 Bytes → 86 Zeichen Verifier (komfortabel im erlaubten Bereich)
            byte[] code = new byte[64];
            secureRandom.nextBytes(code);

            String codeVerifier = base64UrlEncode(code);

            // SHA-256 des Verifiers
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));

            String codeChallenge = base64UrlEncode(hashed);

            return new PKCEPair(codeVerifier, codeChallenge);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 nicht verfügbar", e);
        }
    }

    private static String base64UrlEncode(byte[] data) {
        return Base64.encodeToString(data,
                        Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP)
                .replace("=", ""); // explizites Sicherheitsnetz
    }
}

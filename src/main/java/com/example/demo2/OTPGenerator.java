package com.example.demo2;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

public class OTPGenerator {
    public static void main(String[] args) {
        String originalSecret = "xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx";
        String secret = base32Decode(removeSpaces(originalSecret));
        long input = getCurrentUnixTime() / 30;
        int smallInteger = generateOTP(secret, input);

        System.out.println("Small Integer: " + smallInteger);
    }

    public static String removeSpaces(String str) {
        return str.replaceAll("\\s+", "");
    }

    public static String base32Decode(String str) {
        byte[] decodedBytes = Base64.getDecoder().decode(str.toUpperCase());
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static long getCurrentUnixTime() {
        return Instant.now().getEpochSecond();
    }

    public static int generateOTP(String secret, long input) {
        try {
            byte[] secretKey = secret.getBytes(StandardCharsets.UTF_8);
            byte[] inputBytes = String.valueOf(input).getBytes(StandardCharsets.UTF_8);
            byte[] hmacBytes = calculateHMAC(secretKey, inputBytes);
            int lastByte = hmacBytes[hmacBytes.length - 1] & 0xFF;
            int lastFourBytes = ((hmacBytes[lastByte] & 0x7F) << 24) |
                    ((hmacBytes[lastByte + 1] & 0xFF) << 16) |
                    ((hmacBytes[lastByte + 2] & 0xFF) << 8) |
                    (hmacBytes[lastByte + 3] & 0xFF);

            return lastFourBytes % 1_000_000;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static byte[] calculateHMAC(byte[] secretKey, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA1");
        hmac.init(secretKeySpec);
        return hmac.doFinal(data);
    }
}
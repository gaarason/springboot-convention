package gaarason.convention.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * @author xt
 */
public final class EncryptionRc4Utils {

    private EncryptionRc4Utils() {
    }

    private static final Integer MAX_INT = 256;

    public static String encrypt(String plaintext, String key) {
        byte[] result = EncryptionRc4Utils.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(result);
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        byte[] s = new byte[EncryptionRc4Utils.MAX_INT];
        if (key.length < 1 || key.length > EncryptionRc4Utils.MAX_INT) {
            throw new IllegalArgumentException("key must be between 1 and MAX_INT bytes");
        } else {
            int keyLen = key.length;
            byte[] t = new byte[EncryptionRc4Utils.MAX_INT];
            for (int i = 0; i < EncryptionRc4Utils.MAX_INT; i++) {
                s[i] = (byte) i;
                t[i] = key[i % keyLen];
            }
            int j = 0;
            for (int i = 0; i < EncryptionRc4Utils.MAX_INT; i++) {
                j = (j + s[i] + t[i]) & 0xFF;
                s[i] ^= s[j];
                s[j] ^= s[i];
                s[i] ^= s[j];
            }
        }
        byte[] ciphertext = new byte[plaintext.length];
        int i = 0;
        int j = 0;
        int k;
        int t;
        for (int counter = 0; counter < plaintext.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + s[i]) & 0xFF;
            s[i] ^= s[j];
            s[j] ^= s[i];
            s[i] ^= s[j];
            t = (s[i] + s[j]) & 0xFF;
            k = s[t];
            ciphertext[counter] = (byte) (plaintext[counter] ^ k);
        }
        return ciphertext;
    }

}

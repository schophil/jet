package be.chipit.jet.domain.service;

import be.chipit.jet.common.JetException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;

@Slf4j
@Component
public class CryptoService {

    private static final String ALGO = "AES/CBC/PKCS5Padding";
    private final HashMap<String, SecretKey> encryptionKeys = new HashMap<>();

    record CipherText(String content, IvParameterSpec iv) {
        public String encode() {
            return String.format("%s|%s", Hex.encodeHexString(iv.getIV()), content);
        }

        public static CipherText decode(String encoded) throws DecoderException {
            String[] parts = encoded.split("\\|");
            return new CipherText(parts[1], new IvParameterSpec(Hex.decodeHex(parts[0])));
        }
    }

    public String encrypt(String text, String password, String salt) {
        IvParameterSpec iv = generateIv();
        try {
            SecretKey secretKey = getKey(password, salt);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] cipherText = cipher.doFinal(text.getBytes());
            return new CipherText(Base64.getEncoder().encodeToString(cipherText), iv).encode();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new JetException("Unable to encrypt", e);
        }
    }

    public String decrypt(String text, String password, String salt) {
        try {
            CipherText cipherText = CipherText.decode(text);
            SecretKey secretKey = getKey(password, salt);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, cipherText.iv());
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText.content()));
            return new String(plainText);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | DecoderException e) {
            throw new JetException("Unable to encrypt", e);
        }
    }

    private SecretKey getKey(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String mapKey = mapKey(password, salt);
        if (!encryptionKeys.containsKey(mapKey)) {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            encryptionKeys.put(mapKey, secret);
            return secret;
        }
        return encryptionKeys.get(mapKey);
    }

    private String mapKey(String password, String salt) {
        return String.format("%s:%s", password, salt);
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}

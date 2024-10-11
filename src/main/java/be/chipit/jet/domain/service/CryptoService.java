package be.chipit.jet.domain.service;

import be.chipit.jet.common.JetException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CryptoService {

    private static final String ALGO = "AES/CBC/PKCS5Padding";
    private static final Pattern CIPHER_TEXT_PATTERN = Pattern.compile("^\\[(.*)\\]\\[(.*)\\]\\[(.*)\\]$");
    private final HashMap<String, SecretKey> encryptionKeys = new HashMap<>();

    record CipherText(String salt, IvParameterSpec iv, String content) {
        public String encode() {
            String base = String.format("[%s][%s][%s]", salt, Hex.encodeHexString(iv.getIV()), content);
            return Base64.getEncoder().encodeToString(base.getBytes(StandardCharsets.UTF_8));
        }

        public static CipherText decode(String encoded) throws DecoderException {
            String base = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            Matcher matcher = CIPHER_TEXT_PATTERN.matcher(base);
            if (!matcher.matches()) {
                throw new JetException("Invalid cipher text");
            }
            return new CipherText(matcher.group(1), new IvParameterSpec(Hex.decodeHex(matcher.group(2))), matcher.group(3));
        }
    }

    public String encrypt(String text, String password) {
        IvParameterSpec iv = generateIv();
        try {
            String salt = RandomStringUtils.randomAlphanumeric(10, 25);
            SecretKey secretKey = getKey(password, salt);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] cipherText = cipher.doFinal(text.getBytes());
            return new CipherText(salt, iv, Hex.encodeHexString(cipherText)).encode();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new JetException("Unable to encrypt", e);
        }
    }

    public String decrypt(String text, String password) {
        try {
            CipherText cipherText = CipherText.decode(text);
            SecretKey secretKey = getKey(password, cipherText.salt());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, cipherText.iv());
            byte[] plainText = cipher.doFinal(Hex.decodeHex(cipherText.content()));
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

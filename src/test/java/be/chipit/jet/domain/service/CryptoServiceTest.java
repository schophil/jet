package be.chipit.jet.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CryptoServiceTest {

    @Test
    void encryptAndDecryptValue() {
        CryptoService cryptoService = new CryptoService();
        String value = "test";
        String password = "password";
        String salt = "salt";
        String encryptedValue = cryptoService.encrypt(value, password);

        log.info("Value: {} - Encrypted: {}", value, encryptedValue);
        assertNotEquals(value, encryptedValue);

        String decryptedValue = cryptoService.decrypt(encryptedValue, password);
        assertEquals(value, decryptedValue);
    }
}

package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncryptSnippet {

    private final CryptoService cryptoService;

    public Snippet execute(Snippet snippet, String password, String salt) {
        if (snippet.isEncrypted()) {
            return snippet;
        }
        snippet.setCommand(cryptoService.encrypt(snippet.getCommand(), password, salt));
        snippet.setEncrypted(true);
        return snippet;
    }
}

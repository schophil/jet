package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecryptSnippet {

    private final CryptoService cryptoService;

    public Snippet execute(Snippet snippet, String password, String salt) {
        if (!snippet.isEncrypted()) {
            return snippet;
        }
        snippet.setCommand(cryptoService.decrypt(snippet.getCommand(), password, salt));
        snippet.setDescription(cryptoService.decrypt(snippet.getDescription(), password, salt));
        snippet.setEncrypted(false);
        return snippet;
    }
}

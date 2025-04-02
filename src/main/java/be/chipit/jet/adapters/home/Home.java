package be.chipit.jet.adapters.home;

import be.chipit.jet.common.JetException;
import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.ports.DeleteSnippetPort;
import be.chipit.jet.domain.ports.ListSnippetsPort;
import be.chipit.jet.domain.ports.SaveSnippetPort;
import be.chipit.jet.domain.usecases.DecryptSnippet;
import be.chipit.jet.domain.usecases.EncryptSnippet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Home implements ListSnippetsPort, SaveSnippetPort, DeleteSnippetPort {

    private final JetStoreParser jetStoreParser;
    private final Path jetStorePath;
    private final DecryptSnippet decryptSnippet;
    private final EncryptSnippet encryptSnippet;

    private JetStore jetStore;

    @Override
    public List<Snippet> listAll() {
        return jetStore.getSnippets();
    }

    @Override
    public void save(Snippet snippet) {
        jetStore.register(snippet);
        writeConfigToFile();
    }

    @Override
    public void delete(Snippet snippet) {
        jetStore.getSnippets().remove(snippet);
        writeConfigToFile();
    }

    public JetStore readOrCreateNew() {
        Path path = jetStorePath;
        File file = path.toFile();
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    log.error("Could not create store directory {}", parentFile);
                    throw new JetException("Could not create config directory");
                }
            }
            jetStore = JetStore.createNew();
        } else {
            jetStore = jetStoreParser.read(path.toFile());
        }
        return jetStore;
    }

    public void writeConfigToFile() {
        beforeStorage(jetStore);
        jetStoreParser.write(jetStorePath.toFile(), jetStore);
        afterStorage(jetStore);
    }

    protected void beforeStorage(JetStore jetStore) {
        jetStore.getSnippets().forEach(snippet -> encryptSnippet.execute(snippet, jetStore.getPassword()));
    }

    protected void afterStorage(JetStore jetStore) {
        jetStore.getSnippets().forEach(snippet -> decryptSnippet.execute(snippet, jetStore.getPassword()));
    }
}

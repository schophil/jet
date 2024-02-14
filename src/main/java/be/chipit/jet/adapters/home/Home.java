package be.chipit.jet.adapters.home;

import be.chipit.jet.common.JetException;
import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.ports.DeleteSnippetPort;
import be.chipit.jet.domain.ports.ListSnippetsPort;
import be.chipit.jet.domain.ports.SaveSnippetPort;
import jakarta.annotation.PostConstruct;
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

    private final JetConfigParser jetConfigParser;
    private final Path jetConfigPath;
    private JetConfig jetConfig;

    @Override
    public List<Snippet> listAll() {
        return jetConfig.getSnippets();
    }

    @Override
    public void save(Snippet snippet) {
        jetConfig.register(snippet);
        writeConfigToFile();
    }

    @Override
    public void delete(Snippet snippet) {
        jetConfig.getSnippets().remove(snippet);
        writeConfigToFile();
    }

    @PostConstruct
    public void initialize() {
        Path path = jetConfigPath;
        File file = path.toFile();
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    log.error("Could not create config directory {}", parentFile);
                    throw new JetException("Could not create config directory");
                }
            }
            jetConfig = JetConfig.createNew();
            writeConfigToFile();
        } else {
            jetConfig = jetConfigParser.read(path.toFile());
        }
    }

    private void writeConfigToFile() {
        jetConfigParser.write(jetConfigPath.toFile(), jetConfig);
    }
}

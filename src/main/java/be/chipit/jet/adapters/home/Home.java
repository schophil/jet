package be.chipit.jet.adapters.home;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.ports.ListSnippetsPort;
import be.chipit.jet.domain.ports.SaveSnippetPort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
public class Home implements ListSnippetsPort, SaveSnippetPort {

    private JetConfigParser jetConfigParser = new JetConfigParser();
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

    @PostConstruct
    public void initialize() {
        Path path = getPath();
        if (!path.toFile().exists()) {
            jetConfig = new JetConfig();
            jetConfig.register(Snippet.builder()
                    .description("Prints Hello World")
                    .command("echo 'hello-world'")
                    .build());
            writeConfigToFile();
        } else {
            jetConfig = jetConfigParser.read(path.toFile());
        }
    }

    private void writeConfigToFile() {
        Path path = getPath();
        jetConfigParser.write(path.toFile(), jetConfig);
    }

    private Path getPath() {
        return Path.of(System.getProperty("user.home"), ".config", "jet.yml");
    }
}

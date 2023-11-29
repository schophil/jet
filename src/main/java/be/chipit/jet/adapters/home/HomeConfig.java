package be.chipit.jet.adapters.home;

import be.chipit.jet.common.JetException;
import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.ports.ListSnippetsPort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
public class HomeConfig implements ListSnippetsPort {

    private Yaml yaml;
    private JetConfig jetConfig;

    @Override
    public List<Snippet> listAll() {
        return jetConfig.getSnippets();
    }

    @PostConstruct
    public void initialize() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setProcessComments(false);
        yaml = new Yaml(options);

        Path path = getPath();
        if (!path.toFile().exists()) {
            jetConfig = new JetConfig();
            jetConfig.register(Snippet.builder()
                    .description("Prints Hello World")
                    .command("echo 'hello-world'")
                    .build());
            writeConfigToFile();
        } else {
            try (var reader = new InputStreamReader(new FileInputStream(path.toFile()))) {
                jetConfig = yaml.loadAs(reader, JetConfig.class);
            } catch (IOException e) {
                log.error("Error reading config {}", path, e);
                throw new JetException("Could not read config to file", e);
            }
        }
    }

    private void writeConfigToFile() {
        Path path = getPath();
        try (var writer = new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8)) {
            String content = yaml.dumpAs(jetConfig, Tag.MAP, FlowStyle.BLOCK);
            writer.write(content);
        } catch (IOException e) {
            throw new JetException("Could not write config to file", e);
        }
    }

    private Path getPath() {
        return Path.of(System.getProperty("user.home"), ".config", "jet.yml");
    }
}

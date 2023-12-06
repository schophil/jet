package be.chipit.jet.adapters.home;

import be.chipit.jet.common.JetException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
class JetConfigParser {
    @Setter
    private Yaml yaml;
    @Setter
    private Charset charset = StandardCharsets.UTF_8;

    JetConfigParser() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setProcessComments(false);
        yaml = new Yaml(options);
    }

    JetConfig read(File file) {
        try (var reader = new InputStreamReader(new FileInputStream(file), charset)) {
            return yaml.loadAs(reader, JetConfig.class);
        } catch (IOException e) {
            log.error("Error reading config {}", file, e);
            throw new JetException("Could not read config to file", e);
        }
    }

    void write(File file, JetConfig jetConfig) {
        try (var writer = new OutputStreamWriter(new FileOutputStream(file), charset)) {
            yaml.dump(jetConfig, writer);
        } catch (IOException e) {
            log.error("Error writing config {}", file, e);
            throw new JetException("Could not write config to file", e);
        }
    }
}

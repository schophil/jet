package be.chipit.jet.adapters.home;

import be.chipit.jet.common.JetException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

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
    private ObjectMapper objectMapper;
    @Setter
    private Charset charset = StandardCharsets.UTF_8;

    JetConfigParser() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(Feature.WRITE_DOC_START_MARKER);
        objectMapper = new ObjectMapper(yamlFactory);
        objectMapper.findAndRegisterModules();
    }

    JetConfig read(File file) {
        try (var reader = new InputStreamReader(new FileInputStream(file), charset)) {
            return objectMapper.readValue(reader, JetConfig.class);
        } catch (IOException e) {
            log.error("Error reading config {}", file, e);
            throw new JetException("Could not read config to file", e);
        }
    }

    void write(File file, JetConfig jetConfig) {
        try (var writer = new OutputStreamWriter(new FileOutputStream(file), charset)) {
            objectMapper.writeValue(writer, jetConfig);
        } catch (IOException e) {
            log.error("Error writing config {}", file, e);
            throw new JetException("Could not write config to file", e);
        }
    }
}

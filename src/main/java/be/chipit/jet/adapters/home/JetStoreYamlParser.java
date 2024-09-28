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

@Setter
@Component
@Slf4j
public class JetStoreYamlParser implements JetStoreParser {
    private ObjectMapper objectMapper;
    private Charset charset = StandardCharsets.UTF_8;

    public JetStoreYamlParser() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(Feature.WRITE_DOC_START_MARKER);
        objectMapper = new ObjectMapper(yamlFactory);
        objectMapper.findAndRegisterModules();
    }

    @Override
    public JetStore read(File file) {
        try (var reader = new InputStreamReader(new FileInputStream(file), charset)) {
            return objectMapper.readValue(reader, JetStore.class);
        } catch (IOException e) {
            log.error("Error reading config {}", file, e);
            throw new JetException("Could not read config to file", e);
        }
    }

    @Override
    public void write(File file, JetStore jetStore) {
        try (var writer = new OutputStreamWriter(new FileOutputStream(file), charset)) {
            objectMapper.writeValue(writer, jetStore);
        } catch (IOException e) {
            log.error("Error writing store {}", file, e);
            throw new JetException("Could not write store to file", e);
        }
    }
}

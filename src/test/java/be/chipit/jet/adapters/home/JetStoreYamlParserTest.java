package be.chipit.jet.adapters.home;

import be.chipit.jet.domain.entities.Snippet;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class JetStoreYamlParserTest {
    @Test
    void readCommandWithQuotes() throws IOException {
        String content = """
            snippets:
            - command: echo "hello world"
              description: Prints Hello World
                """;
        File tempFile = File.createTempFile("jet-test", ".yml");
        tempFile.deleteOnExit();
        FileUtils.writeStringToFile(tempFile, content, StandardCharsets.UTF_8);

        JetStoreYamlParser parser = new JetStoreYamlParser();
        JetStore jetStore = parser.read(tempFile);

        assertThat(jetStore).isNotNull();
        assertThat(jetStore.getSnippets()).containsOnly(Snippet.builder()
                .description("Prints Hello World")
                .command("echo \"hello world\"")
                .build());
    }

    @Test
    void readCommandWithEscapedQuotes() throws IOException {
        String content = """
            snippets:
            - command: echo \"hello world\"
              description: Prints Hello World
                """;
        File tempFile = File.createTempFile("jet-test", ".yml");
        tempFile.deleteOnExit();
        FileUtils.writeStringToFile(tempFile, content, StandardCharsets.UTF_8);

        JetStoreYamlParser parser = new JetStoreYamlParser();
        JetStore jetStore = parser.read(tempFile);

        assertThat(jetStore).isNotNull();
        assertThat(jetStore.getSnippets()).containsOnly(Snippet.builder()
                .description("Prints Hello World")
                .command("echo \"hello world\"")
                .build());
    }
}

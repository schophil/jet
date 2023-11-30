package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.samskivert.mustache.Mustache;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GetParametersTest {

    private Mustache.Compiler compiler = Mustache.compiler();

    @Test
    @DisplayName("If snippit does not contain parameters, then return empty list")
    void noParameters() {
        Snippet snippet = Snippet.builder()
                .command("echo hello world")
                .description("Displays hello world")
                .build();

        List<String> parameters = new GetParameters(compiler).getParameters(snippet);
        assertThat(parameters).isEmpty();
    }

    @Test
    @DisplayName("If snippit does not contain parameters, then return empty list")
    void withParameters() {
        Snippet snippet = Snippet.builder()
                .command("echo {{item}}: {{value}}")
                .description("Displays item")
                .build();

        List<String> parameters = new GetParameters(compiler).getParameters(snippet);
        assertThat(parameters).containsExactlyInAnyOrder("item", "value");
    }

    @Test
    @DisplayName("An empty command should return an empty list")
    void emptyCommand() {
        Snippet snippet = Snippet.builder()
                .description("Displays item")
                .build();

        List<String> parameters = new GetParameters(compiler).getParameters(snippet);
        assertThat(parameters).isEmpty();
    }
}

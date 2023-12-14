package be.chipit.jet.domain.usecases;

import be.chipit.jet.common.JetException;
import be.chipit.jet.domain.entities.Snippet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.samskivert.mustache.Mustache;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCommandTest {

    @Mock
    private GetParameters getParameters;

    @Test
    void createSuccess() {
        var snippet = Snippet.builder()
                .command("echo {{name}}")
                .build();
        when(getParameters.getParameters(snippet)).thenReturn(List.of("name"));

        var createCommand = new CreateCommand(getParameters, Mustache.compiler());

        assertThat(createCommand.create(snippet, Map.of("name", "Jet"))).isEqualTo("echo Jet");
    }

    @Test
    void createFailure() {
        var snippet = Snippet.builder()
                .command("echo {{name}}")
                .build();
        when(getParameters.getParameters(snippet)).thenReturn(List.of("name"));

        var createCommand = new CreateCommand(getParameters, Mustache.compiler());

        Map<String, Object> context = Map.of("names", "Jet");
        assertThatThrownBy(() -> createCommand.create(snippet, context))
                .isInstanceOf(JetException.class);
    }
}

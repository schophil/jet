package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.ports.SaveSnippetPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterSnippetTest {

    @Mock
    private SaveSnippetPort saveSnippetPort;

    @Test
    void execute() {
        new RegisterSnippet(saveSnippetPort).execute("command", "description");

        var expected = Snippet.builder()
            .description("description")
            .command("command")
            .build();

        verify(saveSnippetPort).save(expected);
    }
}

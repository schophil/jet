package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.ports.SaveSnippetPort;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterSnippet {
    private final SaveSnippetPort saveSnippetPort;

    public Snippet execute(String command, String description) {
        Snippet snippet = Snippet.builder()
                .command(command)
                .description(description)
                .build();
        saveSnippetPort.save(snippet);
        return snippet;
    }
}

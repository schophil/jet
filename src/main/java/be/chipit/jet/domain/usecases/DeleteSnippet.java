package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.ports.DeleteSnippetPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSnippet {

    private final DeleteSnippetPort deleteSnippetPort;

    public void execute(Snippet snippet) {
        deleteSnippetPort.delete(snippet);
    }
}

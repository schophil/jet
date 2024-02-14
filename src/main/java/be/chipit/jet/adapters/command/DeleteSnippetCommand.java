package be.chipit.jet.adapters.command;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.usecases.DeleteSnippet;
import be.chipit.jet.domain.usecases.ListSnippets;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.Optional;

@Command
@RequiredArgsConstructor
public class DeleteSnippetCommand extends AbstractShellComponent {

    private final ListSnippets listSnippets;
    private final DeleteSnippet deleteSnippet;

    @Command(group = "snippets", alias = "d", command = "delete", description = "Delete snippet")
    public void delete() {
        var snippet = selectSnippet();
        snippet.ifPresent(deleteSnippet::execute);
    }

    private Optional<Snippet> selectSnippet() {
        return SnippetSelector.builder()
            .templateExecutor(getTemplateExecutor())
            .terminal(getTerminal())
            .resourceLoader(getResourceLoader())
            .build()
            .selectSnippet("Select snippet to delete", listSnippets::execute);
    }
}

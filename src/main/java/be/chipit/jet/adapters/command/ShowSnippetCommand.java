package be.chipit.jet.adapters.command;

import java.util.Optional;

import org.jline.terminal.Terminal;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.standard.AbstractShellComponent;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.usecases.ListSnippets;
import lombok.RequiredArgsConstructor;

@Command
@RequiredArgsConstructor
public class ShowSnippetCommand extends AbstractShellComponent {

    private final ListSnippets listSnippets;
    private final Terminal terminal;

    @Command(group = "snippets", alias = "s", command = "show", description = "Show snippet details")
    public void show() {
        var snippet = selectSnippet();
        snippet.ifPresent(this::printSnippet);
    }

    private void printSnippet(Snippet snippet) {
        terminal.writer().println(snippet.getDescription());
        terminal.writer().println("\t" + snippet.getCommand());
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

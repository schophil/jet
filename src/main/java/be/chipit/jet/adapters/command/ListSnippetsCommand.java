package be.chipit.jet.adapters.command;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.usecases.ListSnippets;
import lombok.RequiredArgsConstructor;

import org.jline.terminal.Terminal;
import org.springframework.shell.command.annotation.Command;

@Command
@RequiredArgsConstructor
public class ListSnippetsCommand {
    private final ListSnippets listSnippets;
    private final Terminal terminal;

    @Command(command = "list", group = "snippets", description = "List snippets")
    public void execute() {
        listSnippets.execute().stream()
                .forEach(this::printSnippet);
        terminal.flush();
    }

    private void printSnippet(Snippet snippet) {
        terminal.writer().println(snippet.getDescription());
        terminal.writer().println("\t" + snippet.getCommand());
    }
}

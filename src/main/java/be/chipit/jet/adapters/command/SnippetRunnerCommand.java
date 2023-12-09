package be.chipit.jet.adapters.command;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.usecases.CreateCommand;
import be.chipit.jet.domain.usecases.GetParameters;
import be.chipit.jet.domain.usecases.ListSnippets;
import lombok.RequiredArgsConstructor;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.StringInput.StringInputContext;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

@Command
@RequiredArgsConstructor
public class SnippetRunnerCommand extends AbstractShellComponent {

    private final ListSnippets listSnippets;
    private final GetParameters getParameters;
    private final CreateCommand createCommand;

    @Command(group = "snippets", alias = "cp", command = "copy", description = "Copy snippet to clipboard")
    public void copy() {
        getCommand().ifPresent(command -> {
            InstructionFiles.writeCopyFile(command);
            System.exit(0);
        });
    }

    @Command(group = "snippets", alias = "exec", command = "execute", description = "Execute a snippet")
    public void execute() {
        getCommand().ifPresent(command -> {
            InstructionFiles.writeCommandFile(command);
            System.exit(0);
        });
    }

    private Optional<String> getCommand() {
        var snippet = selectSnippet();
        if (snippet.isPresent()) {
            var arguments = collectParameters(snippet.get());
            return Optional.of(createCommand.create(snippet.get(), arguments));
        }
        return Optional.empty();
    }

    private Optional<Snippet> selectSnippet() {
        List<SelectorItem<Snippet>> items = listSnippets.list()
                .stream()
                .map(this::createSelectorItem)
                .toList();
        SingleItemSelector<Snippet, SelectorItem<Snippet>> component = new SingleItemSelector<>(getTerminal(), items,
                "Select snippet to execute",
                null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        component.setItemMapper(Snippet::getDescription);
        SingleItemSelectorContext<Snippet, SelectorItem<Snippet>> context = component
                .run(SingleItemSelectorContext.empty());
        return context.getResultItem().map(SelectorItem::getItem);
    }

    private Map<String, Object> collectParameters(Snippet snippet) {
        List<String> parameters = getParameters.getParameters(snippet);
        if (parameters.isEmpty()) {
            return Map.of();
        }
        return parameters.stream()
                .map(parameter -> Map.entry(parameter, collectParameter(parameter)))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    private String collectParameter(String parameter) {
        StringInput component = new StringInput(getTerminal(), parameter, null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        StringInputContext context = component.run(StringInputContext.empty());
        return context.getResultValue();
    }

    private SelectorItem<Snippet> createSelectorItem(Snippet snippet) {
        return SelectorItem.of(getDisplayString(snippet), snippet);
    }

    private String getDisplayString(Snippet snippet) {
        return String.format("%s [ %s ]", snippet.getDescription(),
                snippet.getCommand()
                        .replace("{", "\\{")
                        .replace("}", "\\}"));
    }
}

package be.chipit.jet.adapters.command;

import be.chipit.jet.common.JetException;
import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.usecases.CreateCommand;
import be.chipit.jet.domain.usecases.GetParameters;
import be.chipit.jet.domain.usecases.ListSnippets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.jline.terminal.Terminal;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.SingleItemSelector.SingleItemSelectorContext;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.StringInput.StringInputContext;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

@Command
@Component
@RequiredArgsConstructor
@Slf4j
public class ExecuteCommand extends AbstractShellComponent {

    private final ListSnippets listSnippets;
    private final GetParameters getParameters;
    private final CreateCommand createCommand;
    private final Terminal terminal;

    @Command(group = "snippets", alias = "exec", command = "execute", description = "Execute a snippet")
    public void execute() {
        var snippet = selectSnippet();
        if (snippet.isPresent()) {
            var arguments = collectParameters(snippet.get());
            String command = createCommand.create(snippet.get(), arguments);
            witeCommandFile(command);
            System.exit(0);
        }
    }

    private Optional<Snippet> selectSnippet() {
        List<SelectorItem<Snippet>> items = listSnippets.list()
                .stream()
                .map(this::createSelectorItem)
                .toList();
        SingleItemSelector<Snippet, SelectorItem<Snippet>> component = new SingleItemSelector<>(terminal, items,
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

    private void witeCommandFile(String command) {
        // write command to temporary file
        try {
            File tempFile = Path.of(System.getProperty("java.io.tmpdir"), "jet.command").toFile();
            log.debug("Writing command to {}", tempFile.getAbsolutePath());
            try (var fout = new FileOutputStream(tempFile)) {
                IOUtils.write(command, fout, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new JetException("Error creating command file", e);
        }
    }
}

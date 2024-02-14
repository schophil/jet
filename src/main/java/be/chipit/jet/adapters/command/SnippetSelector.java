package be.chipit.jet.adapters.command;

import be.chipit.jet.domain.entities.Snippet;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.core.io.ResourceLoader;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.style.TemplateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Builder
@RequiredArgsConstructor
class SnippetSelector {

    private final ResourceLoader resourceLoader;
    private final TemplateExecutor templateExecutor;
    private final Terminal terminal;

    public Optional<Snippet> selectSnippet(String message, Supplier<List<Snippet>> listSnippets) {
        List<SelectorItem<Snippet>> items = listSnippets.get()
            .stream()
            .map(this::createSelectorItem)
            .toList();
        SingleItemSelector<Snippet, SelectorItem<Snippet>> component = new SingleItemSelector<>(terminal, items,
            message,
            null);
        component.setResourceLoader(resourceLoader);
        component.setTemplateExecutor(templateExecutor);
        component.setItemMapper(Snippet::getDescription);
        SingleItemSelector.SingleItemSelectorContext<Snippet, SelectorItem<Snippet>> context = component
            .run(SingleItemSelector.SingleItemSelectorContext.empty());
        return context.getResultItem().map(SelectorItem::getItem);
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

package be.chipit.jet.adapters.command;

import be.chipit.jet.adapters.home.Home;
import be.chipit.jet.adapters.home.JetStore;
import be.chipit.jet.domain.usecases.DecryptSnippet;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.standard.AbstractShellComponent;

@Command
@RequiredArgsConstructor
public class OpenCommand extends AbstractShellComponent {

    private final Home home;
    private final DecryptSnippet decryptSnippet;

    @Command(group = "snippets", alias = "o", command = "open", description = "Open the snippet file")
    public void open() {
        JetStore jetStore = home.readOrCreateNew();
        if (jetStore.isNew()) {
            String password = collectPassword("Creating new snippet store, please enter password for new store:");
            jetStore.setPassword(password);
            home.writeConfigToFile();
        } else if (JetStore.VERSION_1.equals(jetStore.getVersion())) {
            String password = collectPassword("Upgrading store, please enter password for snippet store:");
            jetStore.setPassword(password);
            jetStore.setVersion(JetStore.LATEST_VERSION);
            home.writeConfigToFile();
        } else {
            String password = collectPassword("Please enter password for snippet store:");
            jetStore.setPassword(password);
            jetStore.getSnippets().forEach(snippet -> {
                snippet.setEncrypted(true);
                decryptSnippet.execute(snippet, jetStore.getPassword());
            });
        }
    }

    private String collectPassword(String message) {
        StringInput stringInput = new StringInput(getTerminal(), message, null);
        stringInput.setResourceLoader(getResourceLoader());
        stringInput.setTemplateExecutor(getTemplateExecutor());
        stringInput.setMaskCharacter('*');
        StringInput.StringInputContext context = stringInput.run(StringInput.StringInputContext.empty());
        return context.getResultValue();
    }
}

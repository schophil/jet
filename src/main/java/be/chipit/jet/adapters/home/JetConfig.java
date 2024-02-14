package be.chipit.jet.adapters.home;

import be.chipit.jet.domain.entities.Snippet;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JetConfig {

    public static JetConfig createNew() {
        var jetConfig = new JetConfig();
        jetConfig.register(Snippet.builder()
            .description("Prints Hello World")
            .command("echo 'hello-world'")
            .build());
        return jetConfig;
    }

    private List<Snippet> snippets = new ArrayList<>();

    public void register(Snippet snippet) {
        snippets.add(snippet);
    }
}

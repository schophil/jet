package be.chipit.jet.adapters.home;

import be.chipit.jet.domain.entities.Snippet;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JetStore {

    public static final String VERSION_1 = "1";
    public static final String VERSION_2 = "2";
    public static final String LATEST_VERSION = VERSION_2;

    public static JetStore createNew() {
        var store = new JetStore();
        store.isNew = true;
        store.version = LATEST_VERSION;
        store.register(Snippet.builder()
            .description("Prints Hello World")
            .command("echo 'hello-world'")
            .build());
        return store;
    }

    private List<Snippet> snippets = new ArrayList<>();
    private String version = "1";
    private String salt;
    @JsonIgnore
    private boolean isNew = false;
    @JsonIgnore
    private String password;

    public void register(Snippet snippet) {
        snippets.add(snippet);
    }
}

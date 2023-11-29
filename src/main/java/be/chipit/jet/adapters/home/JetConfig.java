package be.chipit.jet.adapters.home;

import be.chipit.jet.domain.entities.Snippet;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JetConfig {
    private List<Snippet> snippets = new ArrayList<>();

    public void register(Snippet snippet) {
        snippets.add(snippet);
    }
}

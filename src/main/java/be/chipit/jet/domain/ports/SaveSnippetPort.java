package be.chipit.jet.domain.ports;

import be.chipit.jet.domain.entities.Snippet;

public interface SaveSnippetPort {
    void save(Snippet snippet);
}

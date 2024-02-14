package be.chipit.jet.domain.ports;

import be.chipit.jet.domain.entities.Snippet;

public interface DeleteSnippetPort {
    void delete(Snippet snippet);
}

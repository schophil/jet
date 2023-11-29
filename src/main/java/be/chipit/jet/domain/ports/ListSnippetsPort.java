package be.chipit.jet.domain.ports;

import be.chipit.jet.domain.entities.Snippet;

import java.util.List;

public interface ListSnippetsPort {
    List<Snippet> listAll();
}

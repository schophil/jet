package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;
import be.chipit.jet.domain.ports.ListSnippetsPort;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListSnippets {

    private final ListSnippetsPort listSnippetsPort;

    public List<Snippet> list() {
        return listSnippetsPort.listAll();
    }
}

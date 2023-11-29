package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.samskivert.mustache.Mustache;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateCommand {

    private final Mustache.Compiler compiler;

    public String create(Snippet snippet, Map<String, Object> parameters) {
        var template = compiler.compile(snippet.getCommand());
        return template.execute(parameters);
    }
}

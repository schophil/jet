package be.chipit.jet.domain.usecases;

import be.chipit.jet.domain.entities.Snippet;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Visitor;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetParameters {

    private final Mustache.Compiler compiler;

    public List<String> getParameters(Snippet snippet) {
        if (snippet.getCommand() == null) {
            return List.of();
        }
        var template = compiler.compile(snippet.getCommand());
        List<String> parameters = new ArrayList<>();
        template.visit(new Visitor() {
            @Override
            public void visitText(String text) {
                // ignore
            }

            @Override
            public void visitVariable(String name) {
                parameters.add(name);
            }

            @Override
            public boolean visitInclude(String name) {
                return true;
            }

            @Override
            public boolean visitSection(String name) {
                return true;
            }

            @Override
            public boolean visitInvertedSection(String name) {
                return true;
            }
        });
        return parameters;
    }
}

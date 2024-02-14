package be.chipit.jet.adapters.command;

import be.chipit.jet.domain.usecases.RegisterSnippet;
import lombok.RequiredArgsConstructor;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.context.ComponentContext;
import org.springframework.shell.component.flow.ComponentFlow;
import org.springframework.shell.component.flow.ComponentFlow.ComponentFlowResult;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Command
@Component
@RequiredArgsConstructor
public class RegisterCommand extends AbstractShellComponent {

    private final RegisterSnippet registerSnippet;
    private final ComponentFlow.Builder componentFlowBuilder;

    @Command(command = "add", group = "snippets", description = "Add snippet")
    public void execute() {
        Map<String, String> parameters = collectParameters();
        registerSnippet.execute(parameters.get("command"), parameters.get("description"));
    }

    private Map<String, String> collectParameters() {
        ComponentFlow flow = componentFlowBuilder.clone().reset()
                .withStringInput("command")
                .name("Command")
                .and()
                .withStringInput("description")
                .name("Description")
                .and()
                .build();
        ComponentFlowResult result = flow.run();
        ComponentContext<?> context = result.getContext();
        return context.stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
    }
}

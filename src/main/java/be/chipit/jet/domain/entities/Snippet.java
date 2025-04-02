package be.chipit.jet.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Snippet {
    private String description;
    private String command;
    @JsonIgnore
    private boolean encrypted = false;
}

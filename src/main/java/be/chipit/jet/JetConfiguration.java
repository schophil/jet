package be.chipit.jet;

import be.chipit.jet.adapters.home.JetStoreParser;
import be.chipit.jet.adapters.home.JetStoreYamlParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Path;

@Configuration
public class JetConfiguration {

    @Bean
    public JetStoreParser jetStoreParser() {
        return new JetStoreYamlParser();
    }

    @Bean("jetStorePath")
    @Profile("default")
    public Path defaultStorePath() {
        return Path.of(System.getProperty("user.home"), ".config", "jet", "jet.yml");
    }

    @Bean("jetStorePath")
    @Profile("dev")
    public Path devStorePath() {
        return Path.of(".","dev-jet.yml");
    }
}

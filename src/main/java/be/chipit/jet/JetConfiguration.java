package be.chipit.jet;

import be.chipit.jet.adapters.home.JetConfigParser;
import be.chipit.jet.adapters.home.JetConfigYamlParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Path;

@Configuration
public class JetConfiguration {

    @Bean
    public JetConfigParser jetConfigParser() {
        return new JetConfigYamlParser();
    }

    @Bean("jetConfigPath")
    @Profile("default")
    public Path defaultConfigPath() {
        return Path.of(System.getProperty("user.home"), ".config", "jet", "jet.yml");
    }

    @Bean("jetConfigPath")
    @Profile("dev")
    public Path devConfigPath() {
        return Path.of(".","dev-jet.yml");
    }
}

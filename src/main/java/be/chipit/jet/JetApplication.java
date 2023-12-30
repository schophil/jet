package be.chipit.jet;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan(value = "be.chipit.jet.adapters.command")
@Slf4j
public class JetApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(JetApplication.class, args);
        } catch (Exception e) {
            log.error("Application error", e);
        }
    }
}

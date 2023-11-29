package be.chipit.jet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@SpringBootApplication
@CommandScan(value = "be.chipit.jet.adapters.command")
public class JetApplication {

    public static void main(String[] args) {
        SpringApplication.run(JetApplication.class, args);
    }
}

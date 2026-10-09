package banca.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "banca")
public class BancaDigitalApplication {
    public static void main(String[] args) {
        SpringApplication.run(BancaDigitalApplication.class, args);
    }
}

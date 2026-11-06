package facilbank.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "facilbank")
public class FacilBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(FacilBankApplication.class, args);
    }
}

package aurora.supply_wok.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PurchasingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PurchasingServiceApplication.class, args);
    }

}

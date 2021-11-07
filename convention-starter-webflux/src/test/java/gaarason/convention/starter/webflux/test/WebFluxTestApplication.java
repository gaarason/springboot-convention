package gaarason.convention.starter.webflux.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xt
 */
@SpringBootApplication(scanBasePackages = {"gaarason.convention.test"})
public class WebFluxTestApplication {
    public static void main(final String[] args) {
        SpringApplication.run(WebFluxTestApplication.class, args);
    }


}

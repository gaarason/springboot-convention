package gaarason.convention.starter.webmvc.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xt
 */
@SpringBootApplication(scanBasePackages = {"gaarason.convention.test"})
public class WebMvcTestApplication {
    public static void main(final String[] args) {
        SpringApplication.run(WebMvcTestApplication.class, args);
    }


}

package gaarason.convention.starter.webflux.test;

import gaarason.convention.test.common.web.run.AbstractHttpServiceHttpClientTest;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xt
 */
@SpringBootTest(classes = WebFluxTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebFluxTest extends AbstractHttpServiceHttpClientTest {

}

package gaarason.convention.starter.webmvc.test;

import gaarason.convention.test.common.web.run.AbstractHttpServiceHttpClientTest;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xt
 */
@SpringBootTest(classes = WebMvcTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebMvcTest extends AbstractHttpServiceHttpClientTest {

}

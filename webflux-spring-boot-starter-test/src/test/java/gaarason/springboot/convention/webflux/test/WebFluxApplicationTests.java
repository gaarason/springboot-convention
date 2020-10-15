package gaarason.springboot.convention.webflux.test;

import gaarason.springboot.convention.common.pojo.ResultVO;
import gaarason.springboot.convention.common.pojo.StatusCode;
import gaarason.springboot.convention.webflux.WebFluxApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebFluxApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.JVM)
@Slf4j
public class WebFluxApplicationTests {

    @Resource
    WebTestClient webTestClient;

    @Test
    public void 正常响应() {
        String uri = "/test/no/error";

        String responseBody = webTestClient.get().uri(uri)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).returnResult().getResponseBody();

        System.out.println(responseBody);

        Assert.assertEquals("{\"code\":0,\"message\":\"OK\",\"data\":\"这是一个正常响应结果\"}", responseBody);
    }

    @Test
    public void 业务异常(){
        String uri = "/test/business/error";

        ResultVO<?> responseBody = webTestClient.get().uri(uri)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ResultVO.class).returnResult().getResponseBody();

        System.out.println(responseBody);

        Assert.assertNotNull(responseBody);
        Assert.assertEquals(responseBody.getCode(), StatusCode.E400001.getCode());
        Assert.assertEquals(responseBody.getMessage(), StatusCode.E400001.getMessage());
    }

    @Test
    public void 受检异常(){
        String uri = "/test/checked/error";

        ResultVO<?> responseBody = webTestClient.get().uri(uri)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ResultVO.class).returnResult().getResponseBody();

        System.out.println(responseBody);

        Assert.assertNotNull(responseBody);
        Assert.assertEquals(responseBody.getCode(), StatusCode.INTERNAL_ERROR.getCode());
        Assert.assertEquals(responseBody.getMessage(), StatusCode.INTERNAL_ERROR.getMessage());
    }


}

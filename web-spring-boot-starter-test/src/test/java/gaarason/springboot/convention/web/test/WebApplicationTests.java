package gaarason.springboot.convention.web.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

import static java.nio.charset.StandardCharsets.UTF_8;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.JVM)
@Slf4j
@WebMvcTest
public class WebApplicationTests {

    @Resource
    private MockMvc mockMvc;

    @Test
    public void 正常响应() throws Exception {
        String uri = "/test/no/error";

        // 构建请求
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(uri)
            .contentType("text/html")
            .accept(MediaType.APPLICATION_JSON);

        // 发送请求，获取请求结果
        ResultActions perform = mockMvc.perform(request);

        // 请求结果校验
        perform.andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult mvcResult = perform.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        String responseString = response.getContentAsString(UTF_8);

        System.out.println(responseString);

        Assert.assertEquals("{\"code\":0,\"message\":\"OK\",\"data\":\"这是一个正常响应结果\"}", responseString);
    }

//    @Test
//    public void 业务异常() {
//        String uri = "/test/business/error";
//
//        ResultVO<?> responseBody = webTestClient.get().uri(uri)
//            .exchange()
//            .expectStatus().isOk()
//            .expectBody(ResultVO.class).returnResult().getResponseBody();
//
//        System.out.println(responseBody);
//
//        Assert.assertNotNull(responseBody);
//        Assert.assertEquals(responseBody.getCode(), StatusCode.E400001.getCode());
//        Assert.assertEquals(responseBody.getMessage(), StatusCode.E400001.getMessage());
//    }
//
//    @Test
//    public void 受检异常() {
//        String uri = "/test/checked/error";
//
//        ResultVO<?> responseBody = webTestClient.get().uri(uri)
//            .exchange()
//            .expectStatus().isOk()
//            .expectBody(ResultVO.class).returnResult().getResponseBody();
//
//        System.out.println(responseBody);
//
//        Assert.assertNotNull(responseBody);
//        Assert.assertEquals(responseBody.getCode(), StatusCode.INTERNAL_ERROR.getCode());
//        Assert.assertEquals(responseBody.getMessage(), StatusCode.INTERNAL_ERROR.getMessage());
//    }


}

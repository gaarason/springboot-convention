package gaarason.convention.test.common.web.service.impl.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.util.EncryptionRc4Utils;
import gaarason.convention.common.util.HttpUtils;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.util.SpringUtils;
import gaarason.convention.test.common.web.controller.TestController;
import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import gaarason.convention.test.common.web.run.AbstractHttpServiceHttpClientTest;
import gaarason.convention.test.common.web.service.TestHttpProviderService;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author xt
 */
public abstract class BaseTestAbstractHttpProviderServiceHttpClientImpl extends AbstractHttpServiceHttpClientTest implements TestHttpProviderService {

    private static final Logger LOGGER       = getLogger();

    private static final String TIMESTAMP    = "timestamp";

    private static final String VALID_TIME   = "valid_time";

    private static final String SIGN         = "sign";

    private static final String ID           = "id";

    private static final Long   CURRENT_TIME = System.currentTimeMillis() / 1000;

    @Value("${server.port:8080}")
    private int serverPort;

    @Test
    public static void getScanBasePackages() {
        List<String> scanBasePackages = SpringUtils.getScanBasePackages();
        Assertions.assertTrue(scanBasePackages.contains("com.mingyuanyun.convention.common.test"));
    }

    @Test
    public static void rc4() {
        String s1 = (EncryptionRc4Utils.encrypt("request_time:1620897722", "1a8ce7910f7bb946d06dd3154f2f6991"));
        String s2 = (EncryptionRc4Utils.encrypt("request_time:1620897722", "1a8ce7910f7bb946d06dd3154f2f6991"));

        BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info(s1);
        Assertions.assertEquals(s1, "fTJ1lmPXo9Hlo3uhJoxpj6iFjALbfKs=");
        BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info(s2);
        Assertions.assertEquals(s2, "fTJ1lmPXo9Hlo3uhJoxpj6iFjALbfKs=");
    }

    @Test
    public static void httpClient() {
        Map<String, Object> map = new HashMap<>(16);
        map.put("ssss", null);
        map.put(null, null);
        HttpUtils.HttpResult exec = HttpUtils.request().url("https://baidu.com", map).exec(3);

        boolean successful = exec.getResponse().isSuccessful();
        BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info(successful);

        ResponseBody body = exec.getResponse().body();
        BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info(body);

        Headers headers = exec.getResponse().headers();
        BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info(headers);

    }

    /**
     * 同步get请求, gzip支持
     * curl -X GET 'http://nacos.local/nacos/v1/cs/configs?dataId=xxx.yaml&group=xx'
     */
    @Test
    public static void request() {
        try {
            HttpUtils.HttpResult exec = HttpUtils.request().setAcceptEncodingGzip(true)
                .url(
                    "http://nacos.local/nacos/v1/cs/configs?dataId=xxx.yaml&group=xx").exec(
                    0);
            Assertions.assertTrue(exec.getBodyString().contains("# 暴露端点"));
        } catch (RuntimeException e) {
            e.getStackTrace();
        }

    }

    /**
     * 异步请求
     * @throws InterruptedException 中断
     */
    @Test
    public static void asyncRequest() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(2);

        final String url = "http://127.0.0.1:8989";
        HttpUtils.request().url(url).setHeader("ssss", "s").exec(3, (httpResult, throwable) -> {
            BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info(String.valueOf(httpResult));
            BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info("{}", throwable.getCause().getMessage(), throwable);
            countDownLatch.countDown();
        });

        final String url2 = "http://baidu.com";
        HttpUtils.request().url(url2).exec(3, (httpResult, throwable) -> {
            BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info(String.valueOf(httpResult));
            BaseTestAbstractHttpProviderServiceHttpClientImpl.LOGGER.info("", throwable);
            countDownLatch.countDown();
        });

        countDownLatch.await();
    }

    @Test
    @Override
    public void normal() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url("/test/no/error")).exec(0).getBodyString());

        Assertions.assertNotNull(resultVO);
        Assertions.assertEquals(resultVO.getCode(), StatusCode.SUCCESS.getCode());
        Assertions.assertEquals(resultVO.getMessage(), StatusCode.SUCCESS.getMessage());
        Assertions.assertEquals(resultVO.getData(), TestController.RESPONSE_STRING);
    }

    @Test
    @Override
    public void normalMany() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url("/test/no/error/many")).exec(0).getBodyString());

        Assertions.assertNotNull(resultVO);
        Assertions.assertEquals(resultVO.getCode(), StatusCode.SUCCESS.getCode());
        Assertions.assertEquals(resultVO.getMessage(), StatusCode.SUCCESS.getMessage());
        Assertions.assertEquals(resultVO.getData(), TestController.RESPONSE_LIST);
    }

    @Test
    @Override
    public void businessError() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url("/test/business/error")).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.SPECIAL_ERROR.getCode());
        Assertions.assertEquals(resultVO.getMessage(), StatusCode.SPECIAL_ERROR.getMessage());
        Assertions.assertTrue(resultVO.getStackTrace().contains(TestController.BUSINESS_EXCEPTION_MESSAGE));
    }

    @Test
    @Override
    public void exceptionError() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url("/test/checked/error")).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.INTERNAL_ERROR.getCode());
        Assertions.assertEquals(resultVO.getMessage(), StatusCode.INTERNAL_ERROR.getMessage());
        Assertions.assertTrue(resultVO.getStackTrace().contains(TestController.CHECKED_EXCEPTION_MESSAGE));
    }

    @Test
    @Override
    public void uriNotFound() {
        ResultVO<?> resultVO =
            responseFormatAndCheck(HttpUtils.request().url(url("/test/no/error")).setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.HTTP_METHOD_ERROR.getCode());
    }

    @Test
    @Override
    public void methodNotFound() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url("/test/12313131312/error")).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.HTTP_NOT_FOUND.getCode());
    }

    @Test
    @Override
    public void exclude() {

        final String name0 = "ccccc";
        final String name1 = "吧";

        ResultVO<?> resultVO = responseFormatAndCheck(
            HttpUtils.request().url(url("/test/parameter/exclude")).addEncodedFormBody(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME,
                    "111")
                .addEncodedFormBody(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP,
                    BaseTestAbstractHttpProviderServiceHttpClientImpl.CURRENT_TIME.toString())
                .addEncodedFormBody("names[0]", name0).addEncodedFormBody("names[1]", name1).setMethod(FinalVariable.Http.Method.POST).exec(
                    0).getBodyString());

        LinkedHashMap<String, Object> resMap = ObjectUtils.typeCast(resultVO.getData());
        List<String> names = ObjectUtils.typeCast(resMap.get("names"));

        Assertions.assertNotNull(resultVO.getData());
        Assertions.assertNull(resMap.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.ID));
        Assertions.assertNull(resMap.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.SIGN));
        Assertions.assertEquals(resMap.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME), 0);
        Assertions.assertEquals(resMap.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP),
            BaseTestAbstractHttpProviderServiceHttpClientImpl.CURRENT_TIME);
        Assertions.assertEquals(names.get(0), name0);
        Assertions.assertEquals(names.get(1), name1);
    }

    @Test
    @Override
    public void requestTypeError() {
        final String name0 = "ccccc";
        final String name1 = "吧";

        ResultVO<?> resultVO = responseFormatAndCheck(
            HttpUtils.request().url(url("/test/parameter/exclude")).addEncodedFormBody(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME,
                    "111")
                .addEncodedFormBody(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP,
                    BaseTestAbstractHttpProviderServiceHttpClientImpl.CURRENT_TIME.toString())
                .addEncodedFormBody("names[0]", name0).addEncodedFormBody("names[1]", name1).setMethod(FinalVariable.Http.Method.POST).exec(
                    0).getBodyString());

        LinkedHashMap<String, Object> resMap = ObjectUtils.typeCast(resultVO.getData());
        List<String> names = ObjectUtils.typeCast(resMap.get("names"));

        Assertions.assertNotNull(resultVO.getData());
        Assertions.assertNull(resMap.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.ID));
        Assertions.assertNull(resMap.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.SIGN));
        Assertions.assertEquals(resMap.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME), 0);
        Assertions.assertEquals(resMap.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP),
            BaseTestAbstractHttpProviderServiceHttpClientImpl.CURRENT_TIME);
        Assertions.assertEquals(names.get(0), name0);
        Assertions.assertEquals(names.get(1), name1);
    }

    @Test
    @Override
    public void noUnifiedResponse1() {
        String responseString = HttpUtils.request().url(url("/test/html/1")).exec(0).getBodyString();
        Assertions.assertEquals(responseString, TestController.RESPONSE_STRING);
    }

    @Test
    @Override
    public void noUnifiedResponse2() {
        String responseString = HttpUtils.request().url(url("/test/html/2")).exec(0).getBodyString();
        Assertions.assertEquals(responseString, TestController.RESPONSE_STRING);
    }

    @Test
    @Override
    public void noUnifiedResponse3() {
        JwtTokenRequestDto requestDto = new JwtTokenRequestDto();
        requestDto.setId("1223");
        requestDto.setNames(Arrays.asList("ss", "a"));
        String responseString =
            HttpUtils.request().url(url("/test/html/3")).setJsonBody(requestDto).setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString();

        // 不走统一响应的情况下, 使用的是spring的json解析器, 其中的 ObjectMapper == Jackson2ObjectMapperBuilder.json().build()

        try {
            Assertions.assertEquals(responseString, Jackson2ObjectMapperBuilder.json().build().writeValueAsString(requestDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Override
    public void noUnifiedResponse4() {
        String responseString = HttpUtils.request().url(url("/test/html/4")).exec(0).getBodyString();
        Assertions.assertEquals(responseString, "true");
    }

    @Test
    @Override
    public void noResponse() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url("/test/no/response")).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.SUCCESS.getCode());
        Assertions.assertNull(resultVO.getData());
    }

    @Test
    @Override
    public void paramGetQuery() {
        final String id = "39e0f74f-93fd-224c-0078-988542600fd3";
        long timestamp = System.currentTimeMillis() / 1000;
        final String validTime = "2222";
        final String sign = "wwsdsdadsdsdsa";

        HashMap<String, Object> parameters = new HashMap<>(16);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.ID, id);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP, String.valueOf(timestamp));
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME, validTime);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.SIGN, sign);

        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url("/test/parameter/error"), parameters).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.SUCCESS.getCode());
        LinkedHashMap<String, Object> data = ObjectUtils.typeCast(resultVO.getData());
        Assertions.assertEquals(data.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.ID), id);
        Assertions.assertEquals(data.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP), timestamp);
        Assertions.assertEquals(data.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME), validTime);
        Assertions.assertEquals(data.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.SIGN), sign);
    }

    @Test
    @Override
    public void paramGetString() {
        final String id = "39e0f74f-93fd-224c-0078-988542600fd3";
        final String validTime = "2222";
        final String sign = "wwsdsdadsdsdsa";

        HashMap<String, Object> parameters = new HashMap<>(16);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.ID, id);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP,
            String.valueOf(BaseTestAbstractHttpProviderServiceHttpClientImpl.CURRENT_TIME));
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME, validTime);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.SIGN, sign);
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url("/test/parameter/string"), parameters).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.SUCCESS.getCode());
        // 目前不支持反射, 形参为 String.class 的参数
        Assertions.assertEquals(resultVO.getData(), "");
    }

    @Test
    @Override
    public void paramPostJsonValidatedError() {
        ResultVO<?> resultVO =
            responseFormatAndCheck(
                HttpUtils.request().url(url("/test/parameter/error")).setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.PARAMETER_VALIDATION_ERROR.getCode());
    }

    @Test
    @Override
    public void connectionErrorIllegalCharacter() {
        final String u = "/test/request/mapping?id=123&timestamp=4434&validTime=30&sign=get_query|";

        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(url(u)).exec(0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.HTTP_CLIENT_BAD_REQUEST.getCode());
        Assertions.assertTrue(resultVO.getMessage().contains(u));
    }

    @Test
    @Override
    public void connectionErrorInvalidUri() {
    }

    @Test
    @Override
    public void paramGetForm() {

    }

    @Test
    @Override
    public void paramPostJson() {
        final String id = "39e0f74f-93fd-224c-0078-988542600fd3";
        final String validTime = "2222";
        final String sign = "wwsdsdadsdsdsa";

        HashMap<String, String> parameters = new HashMap<>(16);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.ID, id);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP,
            String.valueOf(BaseTestAbstractHttpProviderServiceHttpClientImpl.CURRENT_TIME));
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME, validTime);
        parameters.put(BaseTestAbstractHttpProviderServiceHttpClientImpl.SIGN, sign);

        ResultVO<?> resultVO = responseFormatAndCheck(
            HttpUtils.request().url(url("/test/parameter/error")).setMethod(FinalVariable.Http.Method.POST).setJsonBody(parameters).exec(
                0).getBodyString());

        Assertions.assertEquals(resultVO.getCode(), StatusCode.SUCCESS.getCode());
        assert resultVO.getData() != null;
        LinkedHashMap<String, Object> data = ObjectUtils.typeCast(resultVO.getData());
        Assertions.assertEquals(data.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.ID), id);
        Assertions.assertEquals(data.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.TIMESTAMP),
            BaseTestAbstractHttpProviderServiceHttpClientImpl.CURRENT_TIME);
        Assertions.assertEquals(data.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.VALID_TIME), validTime);
        Assertions.assertEquals(data.get(BaseTestAbstractHttpProviderServiceHttpClientImpl.SIGN), sign);
    }

    @Override
    public void paramGetQueryValidated() {

    }

    @Override
    public void paramGetFormValidated() {

    }

    @Override
    public void paramPostJsonValidated() {

    }

    @Override
    public void paramGetQueryValidatedError() {

    }

    @Override
    public void paramGetFormValidatedError() {

    }

}

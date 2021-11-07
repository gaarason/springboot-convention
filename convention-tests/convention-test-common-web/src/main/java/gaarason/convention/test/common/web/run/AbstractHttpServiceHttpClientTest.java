package gaarason.convention.test.common.web.run;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.provider.LogProvider;
import gaarason.convention.common.util.HttpUtils;
import gaarason.convention.common.util.JsonUtils;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.util.StringUtils;
import gaarason.convention.test.common.web.contract.HttpServiceTestsInterface;
import gaarason.convention.test.common.web.controller.TestController;
import gaarason.convention.test.common.web.pojo.BaseRequestDto;
import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import gaarason.convention.test.common.web.pojo.LocalDateTimeDto;
import gaarason.convention.test.common.web.pojo.RequestDto;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * http服务测试类
 * @author xt
 */
public abstract class AbstractHttpServiceHttpClientTest implements HttpServiceTestsInterface {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String HOST = "http://127.0.0.1";

    @Value("${server.port:8080}")
    protected int serverPort;

    @Resource
    protected LogProvider logProvider;

    protected static final ObjectMapper MAPPER = JsonUtils.getMapper();

    @BeforeEach
    public void bef() {
        LogProvider.clear();
    }

    protected String urlTest(String uri) {
        return AbstractHttpServiceHttpClientTest.HOST + ":" + serverPort + "/test/" + StringUtils.ltrim(uri, "/");
    }

    protected String url(String uri) {
        return AbstractHttpServiceHttpClientTest.HOST + ":" + serverPort + "/" + StringUtils.ltrim(uri, "/");
    }



    /**
     * 响应格式化
     *
     * @param responseString 响应原文
     * @return 响应对象
     */
    protected <T> ResultVO<T> responseFormatAndCheck(String responseString) {
        AbstractHttpServiceHttpClientTest.LOGGER.info("响应原文 : " + responseString);
        try {
            ResultVO<T> resultVO = AbstractHttpServiceHttpClientTest.MAPPER.readValue(responseString, new TypeReference<ResultVO<T>>() {
            });
            AbstractHttpServiceHttpClientTest.LOGGER.info("响应对象 : " + resultVO);

            // 属性不为 null
            Assertions.assertNotNull(resultVO);
            Assertions.assertNotNull(resultVO.getMessage());
            Assertions.assertNotNull(resultVO.getStackTrace());
            Assertions.assertNotNull(resultVO.getTraceId());
            Assertions.assertNotNull(resultVO.getRequestUrl());
            Assertions.assertTrue(resultVO.getRequestUrl().contains(String.valueOf(serverPort)));
            Assertions.assertNotNull(resultVO.getRequestDatetime());
            Assertions.assertNotNull(resultVO.getResponseDatetime());
            Assertions.assertNotNull(resultVO.getApplicationName());

            // 响应时间大于请求时间
            Assertions.assertFalse(LocalDateTime.parse(resultVO.getResponseDatetime()).isBefore(LocalDateTime.parse(resultVO.getRequestDatetime())));

            // 非正确响应, data 应该为 null
            if (resultVO.getCode() != StatusCode.SUCCESS.getCode()) {
                Assertions.assertNull(resultVO.getData());
            }
            return resultVO;
        } catch (Throwable e) {
            throw new BusinessException(e);
        }

    }

    // -------------- 以下是测试 ---------------//

    @Test
    @Override
    public void faviconIco() {
        HttpUtils.HttpResult httpResult = HttpUtils.request().url(url("/favicon.ico")).exec(0);
        Assertions.assertTrue(httpResult.getResponse().isSuccessful());
        Assertions.assertEquals("", httpResult.getBodyString());
    }

    @Test
    @Override
    public void httpUtils() {

        Map<String, Object> map = new HashMap<>(16);
        map.put("ssss", null);
        map.put(null, null);
        HttpUtils.HttpResult exec = HttpUtils.request().url("https://baidu.com", map).exec(3);

        boolean successful = exec.getResponse().isSuccessful();
        AbstractHttpServiceHttpClientTest.LOGGER.info(successful);

        ResponseBody body = exec.getResponse().body();
        AbstractHttpServiceHttpClientTest.LOGGER.info(body);

        Headers headers = exec.getResponse().headers();
        AbstractHttpServiceHttpClientTest.LOGGER.info(headers);
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityString() {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseResponseEntityString")).exec(0).getBodyString();
        Assertions.assertEquals(TestController.RESPONSE_STRING, bodyString);
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityObject() throws JsonProcessingException {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseResponseEntityObject")).exec(0).getBodyString();

        //
        JwtTokenRequestDto jwtTokenRequestDto = Jackson2ObjectMapperBuilder.json().build().readValue(bodyString, JwtTokenRequestDto.class);

        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, jwtTokenRequestDto);
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityBoolean() {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseResponseEntityBoolean")).exec(0).getBodyString();
        Assertions.assertEquals(TestController.BOOL.toString(), bodyString);
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityVoid() {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseResponseEntityVoid")).exec(0).getBodyString();
        Assertions.assertTrue(bodyString.isEmpty());
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityNumber() {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseResponseEntityNumber")).exec(0).getBodyString();
        Assertions.assertEquals(TestController.NUMBER.toString(), bodyString);
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityMap() throws JsonProcessingException {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseResponseEntityMap")).exec(0).getBodyString();
        //
        Map<Object, Object> map = Jackson2ObjectMapperBuilder.json().build().readValue(bodyString, new TypeReference<Map<Object, Object>>() {
        });

        Assertions.assertEquals(TestController.RESPONSE_MAP, map);
    }

    @Test
    @Override
    public void excludeUnifiedResponseString() {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseString")).exec(0).getBodyString();
        Assertions.assertEquals(TestController.RESPONSE_STRING, bodyString);

        Map<String, Object> map = new HashMap<>(16);
        map.put("validTime", "12339");
        String str = HttpUtils.request().url(url("/AnnotationExcludeUnifiedResponseController/test"), map).exec(0).getBodyString();
        Assertions.assertEquals(str, str);
    }

    @Test
    @Override
    public void excludeUnifiedResponseObject() throws JsonProcessingException {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseObject")).exec(0).getBodyString();
        //
        JwtTokenRequestDto jwtTokenRequestDto = Jackson2ObjectMapperBuilder.json().build().readValue(bodyString, JwtTokenRequestDto.class);

        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, jwtTokenRequestDto);
    }

    @Test
    @Override
    public void excludeUnifiedResponseBoolean() {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseBoolean")).exec(0).getBodyString();
        Assertions.assertEquals(TestController.BOOL.toString(), bodyString);
    }

    @Test
    @Override
    public void excludeUnifiedResponseVoid() {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseVoid")).exec(0).getBodyString();
        Assertions.assertTrue(bodyString.isEmpty());
    }

    @Test
    @Override
    public void excludeUnifiedResponseNumber() {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseNumber")).exec(0).getBodyString();
        Assertions.assertEquals(TestController.NUMBER.toString(), bodyString);
    }

    @Test
    @Override
    public void excludeUnifiedResponseMap() throws JsonProcessingException {
        String bodyString = HttpUtils.request().url(urlTest("excludeUnifiedResponseMap")).exec(0).getBodyString();
        //
        Map<Object, Object> map = Jackson2ObjectMapperBuilder.json().build().readValue(bodyString, new TypeReference<Map<Object, Object>>() {
        });

        Assertions.assertEquals(TestController.RESPONSE_MAP, map);
    }

    @Test
    @Override
    public void unifiedResponseThrowBusinessException() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedResponseThrowBusinessException")).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.DEFAULT_ERROR.getCode(), resultVO.getCode());
        Assertions.assertEquals(TestController.BUSINESS_EXCEPTION_MESSAGE, resultVO.getMessage());
    }

    @Test
    @Override
    public void unifiedResponseThrowException() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedResponseThrowException")).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.INTERNAL_ERROR.getCode(), resultVO.getCode());
        Assertions.assertNotEquals(TestController.CHECKED_EXCEPTION_MESSAGE, resultVO.getMessage());
        Assertions.assertEquals(StatusCode.INTERNAL_ERROR.getMessage(), resultVO.getMessage());

    }

    @Test
    @Override
    public void unifiedResponseString() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedResponseString")).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Assertions.assertEquals(TestController.RESPONSE_STRING, resultVO.getData().toString());

        Map<String, Object> map = new HashMap<>(16);
        map.put("validTime", "12339");
        ResultVO<Object> objectResultVO =
                responseFormatAndCheck(HttpUtils.request().url(url("/AnnotationUnifiedResponseController/test"), map).exec(0).getBodyString());
        Assertions.assertEquals("12339", objectResultVO.getData().toString());
    }

    @Test
    @Override
    public void unifiedResponseObject() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedResponseObject")).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Assertions.assertNotNull(resultVO.getData());
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, ObjectUtils.typeCast(resultVO.getData(), JwtTokenRequestDto.class));
    }

    @Test
    @Override
    public void unifiedResponseBoolean() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedResponseBoolean")).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Assertions.assertEquals(TestController.BOOL, resultVO.getData());

    }

    @Test
    @Override
    public void unifiedResponseVoid() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedResponseVoid")).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Assertions.assertNull(resultVO.getData());
    }

    @Test
    @Override
    public void unifiedResponseNumber() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedResponseNumber")).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Assertions.assertEquals(TestController.NUMBER, resultVO.getData());
    }

    @Test
    @Override
    public void unifiedResponseMap() {
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedResponseMap")).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Assertions.assertEquals(ObjectUtils.typeCast(TestController.RESPONSE_MAP, Map.class), resultVO.getData());
    }

    @Test
    @Override
    public void unifiedRequestMapGetQuery() {

        final String id = "39f2a5fc-c2b6-cb51-701e-1c5bd8f3765d";
        final String timestamp = "1605179956";
        final String name0 = "ccccc";
        final String name1 = "11";
        HashMap<String, Object> parameters = new HashMap<>(16);
        parameters.put("id", id);
        parameters.put("validTime", "111");
        parameters.put("timestamp", timestamp);
        parameters.put("names[0]", name0);
        parameters.put("names[1]", name1);

        // 目前不支持, get参数解析到map
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestMap"), parameters).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.INTERNAL_ERROR.getCode(), resultVO.getCode());

    }

    @Test
    @Override
    public void unifiedRequestObjectGetQuery() {
        final String id = "39f2a5fc-c2b6-cb51-701e-1c5bd8f3765d";
        final String timestamp = "1605179956";
        final String name0 = "ccccc";
        final String name1 = "11";
        HashMap<String, Object> parameters = new HashMap<>(16);
        parameters.put("id", id);
        parameters.put("validTime", "111");
        parameters.put("timestamp", timestamp);
        parameters.put("names[0]", name0);
        parameters.put("names[1]", name1);

        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestObject"), parameters).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Map<String, Object> resMap = ObjectUtils.typeCast(resultVO.getData(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertEquals(parameters.get("id"), resMap.get("id"));
        Assertions.assertEquals(Integer.parseInt((String) parameters.get("timestamp")), Integer.parseInt(resMap.get("timestamp").toString()));

        // 参数传递的驼峰, 可以序列化到对象中, 返回的规范依然是全小写下划线
        Assertions.assertEquals(parameters.get("validTime"), String.valueOf(resMap.get("valid_time")));

        List<String> names = ObjectUtils.typeCast(resMap.get("names"));
        AbstractHttpServiceHttpClientTest.LOGGER.info(names);

        Assertions.assertEquals(parameters.get("names[0]"), names.get(0));
        Assertions.assertEquals(parameters.get("names[1]"), names.get(1));

        // 验证控制器里面的, 类上注解 @UnifiedRequest 是否生效
        Map<String, Object> map = new HashMap<>(16);
        map.put("validTime", "12345");
        ResultVO<?> resultVoTwo =
                responseFormatAndCheck(HttpUtils.request().url(url("/AnnotationUnifiedRequestController/test"), map).exec(0).getBodyString());
        Assertions.assertEquals("12345", String.valueOf(resultVoTwo.getData()));

        // 验证控制器里面的, 类上注解 @UnifiedRequest 是否生效
        Map<String, Object> map2 = new HashMap<>(16);
        map2.put("valid_time", "12345");
        ResultVO<?> resultVoThird =
                responseFormatAndCheck(HttpUtils.request().url(url("/AnnotationUnifiedRequestController/test"), map2).exec(0).getBodyString());
        Assertions.assertEquals("12345", String.valueOf(resultVoThird.getData()));
    }

    @Test
    @Override
    public void unifiedRequestObjectGetQueryUnderline() {

        final String id = "39f2a5fc-c2b6-cb51-701e-1c5bd8f3765d";
        final String timestamp = "1605179956";
        final String name0 = "ccccc";
        final String name1 = "吧";
        HashMap<String, Object> parameters = new HashMap<>(16);
        parameters.put("id", id);
        parameters.put("valid_time", "111");
        parameters.put("timestamp", timestamp);
        parameters.put("names[0]", name0);
        parameters.put("names[1]", name1);

        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestObject"), parameters).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Map<String, Object> resMap = ObjectUtils.typeCast(resultVO.getData(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertEquals(parameters.get("id"), resMap.get("id"));
        Assertions.assertEquals(Integer.parseInt((String) parameters.get("timestamp")), Integer.parseInt(resMap.get("timestamp").toString()));
        Assertions.assertEquals(parameters.get("valid_time"), String.valueOf(resMap.get("valid_time")));

        List<String> names = ObjectUtils.typeCast(resMap.get("names"));

        Assertions.assertEquals(parameters.get("names[0]"), names.get(0));
        Assertions.assertEquals(parameters.get("names[1]"), names.get(1));
    }

    @Test
    @Override
    public void unifiedRequestObjectGetQueryMixed() {
        final String id = "39f2a5fc-c2b6-cb51-701e-1c5bd8f3765d";
        final String timestamp = "1605179956";
        final String name0 = "ccccc";
        final String name1 = "吧";
        ArrayList<String> list = new ArrayList<>();
        list.add(name0);
        list.add(name1);
        HashMap<String, Object> parameters = new HashMap<>(16);
        parameters.put("id", id);
        parameters.put("validTime", "222");
        parameters.put("timestamp", timestamp);
        parameters.put("names", list);

        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestObject"), parameters).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.SUCCESS.getCode(), resultVO.getCode());
        Map<String, Object> resMap = ObjectUtils.typeCast(resultVO.getData(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertEquals(parameters.get("id"), resMap.get("id"));
        Assertions.assertEquals(Integer.parseInt(parameters.get("timestamp").toString()), Integer.parseInt(resMap.get("timestamp").toString()));
        Assertions.assertEquals(parameters.get("validTime"), String.valueOf(resMap.get("valid_time")));

        List<String> names = ObjectUtils.typeCast(resMap.get("names"));
        Assertions.assertEquals(list.get(0), names.get(0));
        Assertions.assertEquals(list.get(1), names.get(1));
    }

    @Test
    @Override
    public void unifiedRequestObjectPostJson() {
        JwtTokenRequestDto requestDto = TestController.JWT_TOKEN_REQUEST_DTO;
        ResultVO<?> resultVO = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("unifiedRequestObject")).setJsonBody(requestDto).setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        ResultVO<JwtTokenRequestDto> responseDto = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<JwtTokenRequestDto>>() {
        });
        Assertions.assertEquals(requestDto, responseDto.getData());
    }

    @Test
    @Override
    public void unifiedRequestGenericObjectPostJson() {
        RequestDto requestDto = new RequestDto();
        requestDto.setData("xxccdd");
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestGenericObject")).setJsonBody(requestDto)
                .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        ResultVO<RequestDto> requestDtoResultVO = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<RequestDto>>() {
        });
        Assertions.assertEquals(requestDto.getData(), requestDtoResultVO.getData().getData());

        BaseRequestDto<String> r2 = new BaseRequestDto<>();
        r2.setData("xxccdd");
        ResultVO<?> resultVoTwo = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("unifiedRequestGenericObject2")).setJsonBody(r2).setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        ResultVO<String> baseRequestDtoResultVO = ObjectUtils.typeCast(resultVoTwo, new TypeReference<ResultVO<String>>() {
        });
        Assertions.assertEquals(r2.getData(), baseRequestDtoResultVO.getData());
    }

    @Test
    @Override
    public void unifiedRequestGenericObjectGet() {

        ResultVO<?> resultVO =
                responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestGenericObject") + "?data=rrtt").exec(0).getBodyString());
        ResultVO<RequestDto> requestDtoResultVO = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<RequestDto>>() {
        });
        Assertions.assertEquals("rrtt", requestDtoResultVO.getData().getData());

        ResultVO<?> resultVoTwo =
                responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestGenericObject2") + "?data=rrtt").exec(0).getBodyString());
        ResultVO<String> baseRequestDtoResultVO = ObjectUtils.typeCast(resultVoTwo, new TypeReference<ResultVO<String>>() {
        });
        Assertions.assertEquals("rrtt", baseRequestDtoResultVO.getData());
    }

    @Test
    @Override
    public void unifiedRequestMapPostJson() {
        HashMap<Object, Object> requestDto = TestController.RESPONSE_MAP;
        ResultVO<?> resultVO = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("unifiedRequestMap")).setJsonBody(requestDto).setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        ResultVO<HashMap<Object, Object>> responseDto = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<HashMap<Object, Object>>>() {
        });

        Assertions.assertEquals(ObjectUtils.typeCast(requestDto, new TypeReference<HashMap<Object, Object>>() {
        }), responseDto.getData());
    }

    @Test
    @Override
    public void unifiedRequestObjectPostFormData() {

        ResultVO<?> resultVO = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("unifiedRequestObject")).addMultipartBodyFormDataPart("id", TestController.JWT_TOKEN_REQUEST_DTO.getId())
                        .addMultipartBodyFormDataPart("validTime", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getValidTime()))
                        .addMultipartBodyFormDataPart("timestamp", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getTimestamp()))
                        .addMultipartBodyFormDataPart("method", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getMethod()))
                        .addMultipartBodyFormDataPart("sign", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getSign()))
                        .addMultipartBodyFormDataPart("names[0]", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getNames().get(0)))
                        .addMultipartBodyFormDataPart("names[1]", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getNames().get(1)))
                        .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());

        ResultVO<JwtTokenRequestDto> responseDto = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<JwtTokenRequestDto>>() {
        });
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, responseDto.getData());

    }

    @Test
    @Override
    public void unifiedRequestObjectPostFormDataUnderline() {
        ResultVO<?> resultVO = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("unifiedRequestObject")).addMultipartBodyFormDataPart("id", TestController.JWT_TOKEN_REQUEST_DTO.getId())
                        .addMultipartBodyFormDataPart("valid_time", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getValidTime()))
                        .addMultipartBodyFormDataPart("timestamp", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getTimestamp()))
                        .addMultipartBodyFormDataPart("method", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getMethod()))
                        .addMultipartBodyFormDataPart("sign", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getSign()))
                        .addMultipartBodyFormDataPart("names[0]", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getNames().get(0)))
                        .addMultipartBodyFormDataPart("names[1]", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getNames().get(1)))
                        .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());

        ResultVO<JwtTokenRequestDto> responseDto = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<JwtTokenRequestDto>>() {
        });
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, responseDto.getData());
    }

    @Test
    @Override
    public void unifiedRequestObjectPostFormDataMixed() {
        unifiedRequestObjectPostFormData();
    }

    @Test
    @Override
    public void unifiedRequestObjectPostUrlencoded() {
        ResultVO<?> resultVO = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("unifiedRequestObject")).addEncodedFormBody("id", TestController.JWT_TOKEN_REQUEST_DTO.getId())
                        .addEncodedFormBody("validTime", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getValidTime()))
                        .addEncodedFormBody("timestamp", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getTimestamp()))
                        .addEncodedFormBody("method", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getMethod()))
                        .addEncodedFormBody("sign", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getSign()))
                        .addEncodedFormBody("names[0]", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getNames().get(0)))
                        .addEncodedFormBody("names[1]", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getNames().get(1)))
                        .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());

        ResultVO<JwtTokenRequestDto> responseDto = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<JwtTokenRequestDto>>() {
        });
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, responseDto.getData());
    }

    @Test
    @Override
    public void unifiedRequestObjectPostUrlencodedUnderline() {
        ResultVO<?> resultVO = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("unifiedRequestObject")).addEncodedFormBody("id", TestController.JWT_TOKEN_REQUEST_DTO.getId())
                        .addEncodedFormBody("valid_time", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getValidTime()))
                        .addEncodedFormBody("timestamp", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getTimestamp()))
                        .addEncodedFormBody("method", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getMethod()))
                        .addEncodedFormBody("sign", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getSign()))
                        .addEncodedFormBody("names[0]", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getNames().get(0)))
                        .addEncodedFormBody("names[1]", String.valueOf(TestController.JWT_TOKEN_REQUEST_DTO.getNames().get(1)))
                        .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());

        ResultVO<JwtTokenRequestDto> responseDto = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<JwtTokenRequestDto>>() {
        });
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, responseDto.getData());
    }

    @Test
    @Override
    public void unifiedRequestObjectPostUrlencodedMixed() {
        unifiedRequestObjectPostUrlencoded();
    }

    @Test
    @Override
    public void unifiedRequestLocalDateTime() {
        LocalTime localTime = LocalTime.now();
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.now();

        // post json
        LocalDateTimeDto localDateTimeDto = new LocalDateTimeDto();
        localDateTimeDto.setTheLocalTime(localTime);
        localDateTimeDto.setTheLocalDate(localDate);
        localDateTimeDto.setTheLocalDateTime(localDateTime);
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestLocalDateTime")).setJsonBody(localDateTimeDto)
                .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        ResultVO<LocalDateTimeDto> localDateTimeDtoResultVO = ObjectUtils.typeCast(resultVO, new TypeReference<ResultVO<LocalDateTimeDto>>() {
        });
        Assertions.assertEquals(localDateTimeDto, localDateTimeDtoResultVO.getData());

        // get query
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("the_local_date", localDate.toString());
        map.put("the_local_time", localTime.toString());
        map.put("the_local_date_time", DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT).format(localDateTime));

        ResultVO<?> resultVoTwo = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestLocalDateTime"), map).exec(0).getBodyString());
        ResultVO<LocalDateTimeDto> localDateTimeDtoResultVoTwo = ObjectUtils.typeCast(resultVoTwo, new TypeReference<ResultVO<LocalDateTimeDto>>() {
        });
        Assertions.assertNotNull(localDateTimeDtoResultVoTwo.getData());
        Assertions.assertEquals(map.get("the_local_date"), localDateTimeDtoResultVoTwo.getData().getTheLocalDate().toString());
        Assertions.assertEquals(map.get("the_local_time"), localDateTimeDtoResultVoTwo.getData().getTheLocalTime().toString());
        Assertions.assertEquals(map.get("the_local_date_time"),
                DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT).format(localDateTimeDtoResultVoTwo.getData().getTheLocalDateTime()));

        // post FormData
        ResultVO<?> resultVoThird = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("unifiedRequestLocalDateTime")).addMultipartBodyFormDataPart("the_local_date", (String) map.get("the_local_date"))
                        .addMultipartBodyFormDataPart("the_local_time", (String) map.get("the_local_time"))
                        .addMultipartBodyFormDataPart("the_local_date_time", (String) map.get("the_local_date_time")).setMethod(FinalVariable.Http.Method.POST).exec(0)
                        .getBodyString());
        ResultVO<LocalDateTimeDto> localDateTimeDtoResultVoThird =
                ObjectUtils.typeCast(resultVoThird, new TypeReference<ResultVO<LocalDateTimeDto>>() {
                });
        Assertions.assertNotNull(localDateTimeDtoResultVoThird.getData());
        Assertions.assertEquals(map.get("the_local_date"), localDateTimeDtoResultVoThird.getData().getTheLocalDate().toString());
        Assertions.assertEquals(map.get("the_local_time"), localDateTimeDtoResultVoThird.getData().getTheLocalTime().toString());
        Assertions.assertEquals(map.get("the_local_date_time"),
                DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT).format(localDateTimeDtoResultVoThird.getData().getTheLocalDateTime()));

        // post urlencode
        ResultVO<?> resultVoFour = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestLocalDateTime"))
                .addEncodedFormBody("the_local_date", (String) map.get("the_local_date")).addEncodedFormBody("the_local_time", (String) map.get("the_local_time"))
                .addEncodedFormBody("the_local_date_time", (String) map.get("the_local_date_time")).setMethod(FinalVariable.Http.Method.POST).exec(0)
                .getBodyString());
        ResultVO<LocalDateTimeDto> localDateTimeDtoResultVoFour = ObjectUtils.typeCast(resultVoFour, new TypeReference<ResultVO<LocalDateTimeDto>>() {
        });
        Assertions.assertNotNull(localDateTimeDtoResultVoFour.getData());
        Assertions.assertEquals(map.get("the_local_date"), localDateTimeDtoResultVoFour.getData().getTheLocalDate().toString());
        Assertions.assertEquals(map.get("the_local_time"), localDateTimeDtoResultVoFour.getData().getTheLocalTime().toString());
        Assertions.assertEquals(map.get("the_local_date_time"),
                DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT).format(localDateTimeDtoResultVoFour.getData().getTheLocalDateTime()));

    }

    @Test
    @Override
    public void unifiedRequestValidatedNow() {
        // 全新的请求对象
        JwtTokenRequestDto requestDto = ObjectUtils.deepCopy(TestController.JWT_TOKEN_REQUEST_DTO);
        final Integer timestamp = 1234;
        // 设置一个错误的时间戳
        requestDto.setTimestamp(timestamp);

        ResultVO<?> warningResultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestValidatedObject")).setJsonBody(requestDto)
                .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        Assertions.assertEquals(warningResultVO.getCode(), StatusCode.PARAMETER_VALIDATION_ERROR.getCode());
        Assertions.assertTrue(warningResultVO.getMessage().contains(String.valueOf(timestamp)));

        // 设置一个正确的时间戳
        requestDto.setTimestamp(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));
        ResultVO<?> rightResultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestValidatedObject")).setJsonBody(requestDto)
                .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());

        ResultVO<JwtTokenRequestDto> responseDto = ObjectUtils.typeCast(rightResultVO, new TypeReference<ResultVO<JwtTokenRequestDto>>() {
        });
        Assertions.assertEquals(requestDto, responseDto.getData());
    }

    @Override
    public void unifiedRequestValidatedDateTimeFormat() {

    }

    @Override
    public void unifiedRequestValidatedFormat() {

    }

    @Test
    @Override
    public void unifiedRequestValidatedNumberRange() {
        // 全新的请求对象
        JwtTokenRequestDto requestDto = ObjectUtils.deepCopy(TestController.JWT_TOKEN_REQUEST_DTO);
        final int validTime = 12343;
        // 设置一个错误的时间戳
        requestDto.setValidTime(validTime);

        ResultVO<?> warningResultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestValidatedObject")).setJsonBody(requestDto)
                .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        Assertions.assertEquals(warningResultVO.getCode(), StatusCode.PARAMETER_VALIDATION_ERROR.getCode());
        Assertions.assertTrue(warningResultVO.getMessage().contains(String.valueOf(validTime)));

        // 设置一个正确的时间戳
        requestDto.setValidTime(122);
        ResultVO<?> rightResultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestValidatedObject")).setJsonBody(requestDto)
                .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        ResultVO<JwtTokenRequestDto> responseDto = ObjectUtils.typeCast(rightResultVO, new TypeReference<ResultVO<JwtTokenRequestDto>>() {
        });
        Assertions.assertEquals(requestDto, responseDto.getData());
    }

    @Test
    @Override
    public void unifiedRequestValidatedSpring() {
        // 全新的请求对象
        JwtTokenRequestDto requestDto = ObjectUtils.deepCopy(TestController.JWT_TOKEN_REQUEST_DTO);
        final String id = "12343";
        // 设置一个错误的id
        requestDto.setId(id);

        ResultVO<?> warningResultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestValidatedObject")).setJsonBody(requestDto)
                .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        Assertions.assertEquals(warningResultVO.getCode(), StatusCode.PARAMETER_VALIDATION_ERROR.getCode());
        Assertions.assertTrue(warningResultVO.getMessage().contains("id"));

        // 设置一个正确的id
        requestDto.setId(UUID.randomUUID().toString());
        ResultVO<?> rightResultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("unifiedRequestValidatedObject")).setJsonBody(requestDto)
                .setMethod(FinalVariable.Http.Method.POST).exec(0).getBodyString());
        ResultVO<JwtTokenRequestDto> responseDto = ObjectUtils.typeCast(rightResultVO, new TypeReference<ResultVO<JwtTokenRequestDto>>() {
        });
        Assertions.assertEquals(requestDto, responseDto.getData());
    }

    @Test
    @Override
    public void excludeUnifiedRequestGetObject() {
        // 原生的spring 参数解析没有下划线到驼峰的能力
        // 所以这里传递的 valid_time=1, 只能被接收对象的 valid_time 接收/赋值
        // 但是 接收对象定义的属性时 validTime 所以, 接口欧的返回值中validTime值为0 (非包装类型不为null, 去取编译默认值0)
        HashMap<String, Object> requestMap = new HashMap<>(16);
        requestMap.put("valid_time", "1");
        ResultVO<?> resultVO = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("excludeUnifiedRequestGetObject"), requestMap).setMethod(FinalVariable.Http.Method.GET).exec(0).getBodyString());

        Map<String, String> responseMap = ObjectUtils.typeCast(resultVO.getData(), new TypeReference<Map<String, String>>() {
        });
        Assertions.assertEquals(resultVO.getCode(), StatusCode.SUCCESS.getCode());
        Assertions.assertNotEquals(requestMap.get("valid_time"), responseMap.get("valid_time"));

        // 与上面相仿
        HashMap<String, Object> requestMap1 = new HashMap<>(16);
        requestMap1.put("validTime", "1");
        ResultVO<?> resultVoOne = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("excludeUnifiedRequestGetObject"), requestMap1).setMethod(FinalVariable.Http.Method.GET).exec(0).getBodyString());

        Map<String, String> responseMap1 = ObjectUtils.typeCast(resultVoOne.getData(), new TypeReference<Map<String, String>>() {
        });
        Assertions.assertEquals(resultVoOne.getCode(), StatusCode.SUCCESS.getCode());
        Assertions.assertEquals(requestMap1.get("validTime"), responseMap1.get("valid_time"));

        // 验证控制器里面的, 类上注解 @ExcludeUnifiedRequest 是否生效
        Map<String, Object> map = new HashMap<>(16);
        map.put("validTime", "12345");
        ResultVO<?> resultVoTwo =
                responseFormatAndCheck(HttpUtils.request().url(url("/AnnotationExcludeUnifiedRequestController/test"), map).exec(0).getBodyString());
        Assertions.assertEquals("12345", String.valueOf(resultVoTwo.getData()));

        // 验证控制器里面的, 类上注解 @ExcludeUnifiedRequest 是否生效
        Map<String, Object> map2 = new HashMap<>(16);
        map2.put("valid_time", "12345");
        ResultVO<?> resultVoThird =
                responseFormatAndCheck(HttpUtils.request().url(url("/AnnotationExcludeUnifiedRequestController/test"), map2).exec(0).getBodyString());
        Assertions.assertNotEquals("12345", String.valueOf(resultVoThird.getData()));
    }

    @Test
    @Override
    public void excludeUnifiedRequestGetMap() {
        // 原生的spring 参数解析没有下划线到驼峰的能力
        // 所以这里传递的 valid_time=1, 只能被接收对象的 valid_time 接收/赋值
        // 但是 接收对象定义的属性时 validTime 所以, 接口欧的返回值中validTime值为0 (非包装类型不为null, 去取编译默认值0)
        HashMap<String, Object> requestMap = new HashMap<>(16);
        requestMap.put("valid_time", "1");

        // 目前不支持 get参数映射到map
        ResultVO<?> resultVO = responseFormatAndCheck(
                HttpUtils.request().url(urlTest("excludeUnifiedRequestGetMap"), requestMap).setMethod(FinalVariable.Http.Method.GET).exec(0).getBodyString());
        Assertions.assertEquals(StatusCode.INTERNAL_ERROR.getCode(), resultVO.getCode());
    }

    @Test
    @Override
    public void excludeUnifiedRequestValidatedGetObject() {
        final String id = "39f2a5fc-c2b6-cb51-701e-1c5bd8f3765d";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        final String name0 = "ccccc";
        final String name1 = "吧";

        HashMap<String, Object> parameters = new HashMap<>(16);
        parameters.put("id", id);
        parameters.put("validTime", "222");
        parameters.put("timestamp", timestamp);
        parameters.put("names[0]", name0);
        parameters.put("names[1]", name1);
        ResultVO<?> resultVO = responseFormatAndCheck(HttpUtils.request().url(urlTest("excludeUnifiedRequestValidatedGetObject"), parameters)
                .setMethod(FinalVariable.Http.Method.GET).exec(0).getBodyString());
        Assertions.assertEquals(resultVO.getCode(), StatusCode.SUCCESS.getCode());

        // 参数传递时,是使用的虽然是 HashMap<String, Object> 对象, 但是解析到对象时, 会将里面的 names[0], names[1] 换成 names = list()
        Assertions.assertThrows(RuntimeException.class, () -> {
            Map<String, String> responseMap = ObjectUtils.typeCast(resultVO.getData(), new TypeReference<Map<String, String>>() {
            });
            Assertions.assertNotEquals(parameters.get("valid_time"), responseMap.get("valid_time"));
        });
        Map<String, Object> responseMap2 = ObjectUtils.typeCast(resultVO.getData(), new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertNotEquals(parameters.get("valid_time"), responseMap2.get("valid_time"));
    }

    @Override
    public void excludeUnifiedRequestPostObjectJson() {

    }

    @Override
    public void excludeUnifiedRequestPostObjectJsonFormData() {

    }

    @Override
    public void excludeUnifiedRequestPostObjectJsonUrlencoded() {

    }

    @Override
    public void excludeUnifiedRequestPostMapJson() {

    }

    @Override
    public void excludeUnifiedRequestPostMapFormData() {

    }

    @Override
    public void excludeUnifiedRequestPostMapFormUrlencoded() {

    }

    @Override
    public void excludeUnifiedRequestValidatedPostObjectNow() {

    }

    @Override
    public void excludeUnifiedRequestValidatedPostObjectDateTimeFormat() {

    }

    @Override
    public void excludeUnifiedRequestValidatedPostObjectFormat() {

    }

    @Override
    public void excludeUnifiedRequestValidatedPostObjectRange() {

    }

    @Override
    public void excludeUnifiedRequestValidatedPostObjectSpring() {

    }
}

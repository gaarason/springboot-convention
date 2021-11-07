package gaarason.convention.test.common.web.controller;

import gaarason.convention.common.appointment.CommonVariable;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.annotation.web.ExcludeUnifiedRequest;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.util.HttpUtils;
import gaarason.convention.test.common.web.contract.TestControllerInterface;
import gaarason.convention.test.common.web.pojo.*;
import org.apache.logging.log4j.Logger;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * @author xt
 */
@RestController
@RequestMapping("/test")
public class TestController implements TestControllerInterface {

    public static final HashMap<Object, Object> RESPONSE_MAP    = new HashMap<>(16);

    public static final String                  RESPONSE_STRING = "这是一个正常响应结果";

    public static final String CHECKED_EXCEPTION_MESSAGE = "这是一个受检异常";

    public static final String BUSINESS_EXCEPTION_MESSAGE = "这是一个业务异常";

    public static final  List<String> RESPONSE_LIST = Arrays.asList("数组第一个值", "数组第二个值");

    private static final Logger       LOGGER        = getLogger();

    public static final Number NUMBER = 888;

    public static final Boolean BOOL = true;

    public static final JwtTokenRequestDto JWT_TOKEN_REQUEST_DTO;

    static {
        JWT_TOKEN_REQUEST_DTO = new JwtTokenRequestDto();
        TestController.JWT_TOKEN_REQUEST_DTO.setId(UUID.randomUUID().toString());
        TestController.JWT_TOKEN_REQUEST_DTO.setMethod(FinalVariable.Http.Method.PATCH);
        TestController.JWT_TOKEN_REQUEST_DTO.setNames(TestController.RESPONSE_LIST);
        TestController.JWT_TOKEN_REQUEST_DTO.setSign("abcdefg");
        TestController.JWT_TOKEN_REQUEST_DTO.setTimestamp(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));
        TestController.JWT_TOKEN_REQUEST_DTO.setValidTime(1234);

        TestController.RESPONSE_MAP.put("one", "oneV");
        TestController.RESPONSE_MAP.put("two", TestController.RESPONSE_LIST);
        TestController.RESPONSE_MAP.put("three", TestController.NUMBER);
        TestController.RESPONSE_MAP.put("four", TestController.BOOL);
        TestController.RESPONSE_MAP.put("five", null);
    }

    public static Map<String, Object> many(FormatCollectionValidatorRequestDTO formatCollectionValidatorRequestDTO,
        FormatValidatorRequestDTO formatValidatorRequestDTO) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("one", formatCollectionValidatorRequestDTO);
        map.put("two", formatValidatorRequestDTO);
        return map;
    }

    @Override
    public String businessError() {
        try {
            try {
                throw new BusinessException(StatusCode.SPECIAL_ERROR, map -> {
                    map.put("姓名", "测试小伙");
                    map.put("msg", TestController.BUSINESS_EXCEPTION_MESSAGE);
                    map.put("性别", "小伙");
                    map.put("这是内层", "异常");
                });
            } catch (Throwable e) {
                throw new BusinessException(StatusCode.SPECIAL_ERROR, map -> {
                    map.put("msg", TestController.BUSINESS_EXCEPTION_MESSAGE);
                    map.put("这是中层", "异常");
                }, e);
            }
        } catch (Throwable e) {
            throw new BusinessException(StatusCode.SPECIAL_ERROR, map -> {
                map.put("msg", TestController.BUSINESS_EXCEPTION_MESSAGE);
                map.put("这是外层", "异常");
            }, e);
        }
    }

    @Override
    public String checkedError() throws Exception {
        throw new Exception(TestController.CHECKED_EXCEPTION_MESSAGE);
    }

    @Override
    public String noError() {
        TestController.LOGGER.info("准备正常响应咯, 这是一个正常响应结果");
        return TestController.RESPONSE_STRING;
    }

    @Override
    public void noResponse() {

    }

    @Override
    public List<String> noErrorMany() {
        return TestController.RESPONSE_LIST;
    }

    @Override
    public ResponseEntity<String> html1() {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(TestController.RESPONSE_STRING);
    }

    @Override
    public String html2() {
        return TestController.RESPONSE_STRING;
    }

    @Override
    public ResponseEntity<JwtTokenRequestDto> html3(JwtTokenRequestDto jwtTokenRequestDTO) {
        TestController.LOGGER.info("request : {}", jwtTokenRequestDTO);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jwtTokenRequestDTO);
    }

    @Override
    public ResponseEntity<Boolean> html4() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(true);
    }

    @Override
    public String parameterAuthenticationError() {
        return "OK";
    }

    @Override
    public JwtTokenRequestDto parameterError(@Validated JwtTokenRequestDto jwtTokenRequestDTO) {
        return jwtTokenRequestDTO;
    }

    @Override
    public String parameterString(String str) {
        TestController.LOGGER.info("str : {}", str);
        return str;
    }

    @Override
    public JwtTokenRequestDto parameterErrorGet(@Validated JwtTokenRequestDto jwtTokenRequestDTO) {
        return jwtTokenRequestDTO;
    }

    @Override
    public JwtTokenRequestDto parameterNoError(@RequestBody JwtTokenRequestDto jwtTokenRequestDTO) {
        new Thread(RunnableWrapper.of(() -> LOGGER.info("子线程的信息"))).start();
        return jwtTokenRequestDTO;
    }

    @Override
    public JwtTokenRequestDto parameterExclude(@ExcludeUnifiedRequest JwtTokenRequestDto jwtTokenRequestDTO) {
        return jwtTokenRequestDTO;
    }

    @Override
    public String appName() {
        return CommonVariable.APPLICATION_NAME;
    }

    @Override
    public FormatValidatorRequestDTO validationSingle(@Validated FormatValidatorRequestDTO formatValidatorRequestDTO) {
        return formatValidatorRequestDTO;
    }

    @Override
    public FormatCollectionValidatorRequestDTO validationCollection(
        @Validated FormatCollectionValidatorRequestDTO formatCollectionValidatorRequestDTO) {
        return formatCollectionValidatorRequestDTO;
    }

    @Override
    public void request() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        final String url = "http://127.0.0.1:8989";
        HttpUtils.request().url(url).setHeader("ssss", "s").exec(3, (httpResult, throwable) -> {
            TestController.LOGGER.info(String.valueOf(httpResult));
            TestController.LOGGER.info("{}", throwable.getCause().getMessage(), throwable);
            countDownLatch.countDown();
        });

        final String url2 = "http://baidu.com";
        HttpUtils.request().url(url2).exec(3, (httpResult, throwable) -> {
            TestController.LOGGER.info(String.valueOf(httpResult));
            TestController.LOGGER.info("", throwable);
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }

    // -------------------- 以下是新的, 当完全覆盖上面的时候, 移除上面的就ok啦 ------------------//

    @Override
    public ResponseEntity<String> excludeUnifiedResponseResponseEntityString() {
        return ResponseEntity.ok().body(TestController.RESPONSE_STRING);
    }

    @Override
    public ResponseEntity<JwtTokenRequestDto> excludeUnifiedResponseResponseEntityObject() {
        return ResponseEntity.ok().body(TestController.JWT_TOKEN_REQUEST_DTO);
    }

    @Override
    public ResponseEntity<Boolean> excludeUnifiedResponseResponseEntityBoolean() {
        return ResponseEntity.ok().body(TestController.BOOL);
    }

    @Override
    public ResponseEntity<Void> excludeUnifiedResponseResponseEntityVoid() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Number> excludeUnifiedResponseResponseEntityNumber() {
        return ResponseEntity.ok().body(TestController.NUMBER);
    }

    @Override
    public ResponseEntity<Map<?, ?>> excludeUnifiedResponseResponseEntityMap() {
        return ResponseEntity.ok().body(TestController.RESPONSE_MAP);
    }

    @Override
    public String excludeUnifiedResponseString() {
        return TestController.RESPONSE_STRING;
    }

    @Override
    public JwtTokenRequestDto excludeUnifiedResponseObject() {
        return TestController.JWT_TOKEN_REQUEST_DTO;
    }

    @Override
    public Boolean excludeUnifiedResponseBoolean() {
        return TestController.BOOL;
    }

    @Override
    public void excludeUnifiedResponseVoid() {

    }

    @Override
    public Number excludeUnifiedResponseNumber() {
        return TestController.NUMBER;
    }

    @Override
    public Map<?, ?> excludeUnifiedResponseMap() {
        return TestController.RESPONSE_MAP;
    }

    @Override
    public void unifiedResponseThrowBusinessException() {
        throw new BusinessException(TestController.BUSINESS_EXCEPTION_MESSAGE);
    }

    @Override
    public void unifiedResponseThrowException() throws Exception {
        throw new Exception(TestController.CHECKED_EXCEPTION_MESSAGE);
    }

    @Override
    public String unifiedResponseString() {
        return TestController.RESPONSE_STRING;
    }

    @Override
    public JwtTokenRequestDto unifiedResponseObject() {
        return TestController.JWT_TOKEN_REQUEST_DTO;
    }

    @Override
    public Boolean unifiedResponseBoolean() {
        return TestController.BOOL;
    }

    @Override
    public void unifiedResponseVoid() {
    }

    @Override
    public Number unifiedResponseNumber() {
        return TestController.NUMBER;
    }

    @Override
    public Map<Object, Object> unifiedResponseMap() {
        return TestController.RESPONSE_MAP;
    }

    @Override
    public JwtTokenRequestDto unifiedRequestObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }

    @Override
    public JwtTokenRequestDto unifiedRequestGetObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }

    @Override
    public Map<Object, Object> unifiedRequestMap(Map<Object, Object> requestMap) {
        return requestMap;
    }

    @Override
    public JwtTokenRequestDto unifiedRequestValidatedObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }

    @Override
    public RequestDto unifiedRequestGenericObject(RequestDto requestDto) {
        return requestDto;
    }

    @Override
    public String unifiedRequestGenericObject2(BaseRequestDto<String> requestDto) {
        return requestDto.getData();
    }

    @Override
    public LocalDateTimeDto unifiedRequestLocalDateTime(LocalDateTimeDto requestDto) {
        return requestDto;
    }

    @Override
    public JwtTokenRequestDto excludeUnifiedRequestGetObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }

    @Override
    public Map<Object, Object> excludeUnifiedRequestGetMap(Map<Object, Object> requestMap) {
        return requestMap;
    }

    @Override
    public JwtTokenRequestDto excludeUnifiedRequestValidatedGetObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }

    @Override
    public JwtTokenRequestDto excludeUnifiedRequestPostObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }

    @Override
    public Map<Object, Object> excludeUnifiedRequestPostMap(Map<Object, Object> requestMap) {
        return requestMap;
    }

    @Override
    public JwtTokenRequestDto excludeUnifiedRequestValidatedPostObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }

    @Override
    public JwtTokenRequestDto excludeUnifiedRequestObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }

    @Override
    public Map<Object, Object> excludeUnifiedRequestMap(Map<Object, Object> requestMap) {
        return requestMap;
    }

    @Override
    public JwtTokenRequestDto excludeUnifiedRequestValidatedObject(JwtTokenRequestDto jwtTokenRequestDto) {
        return jwtTokenRequestDto;
    }
}

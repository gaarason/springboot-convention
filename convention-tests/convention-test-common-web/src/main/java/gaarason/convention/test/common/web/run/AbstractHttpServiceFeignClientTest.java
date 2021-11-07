package gaarason.convention.test.common.web.run;

import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.util.JsonUtils;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.web.controller.BlankFaviconIcoController;
import gaarason.convention.test.common.web.contract.HttpServiceTestsInterface;
import gaarason.convention.test.common.web.contract.TestControllerInterface;
import gaarason.convention.test.common.web.controller.TestController;
import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import gaarason.convention.test.common.web.pojo.LocalDateTimeDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * openfeign服务测试类
 * @author xt
 */
public abstract class AbstractHttpServiceFeignClientTest implements HttpServiceTestsInterface {

    private static final Logger LOGGER = LogManager.getLogger();

    @BeforeEach
    public void bef() {
        ChainProvider.clear();
    }

    /**
     * 服务对象
     * @return BlankFaviconIcoController
     */
    protected abstract BlankFaviconIcoController getServiceOfBlankFaviconIcoController();

    /**
     * 服务对象
     * @return TestControllerInterface
     */
    protected abstract TestControllerInterface getServiceOfTestControllerInterface();

    // ---------------------- 以下是测试 ----------------------//

    @Test
    @Override
    public void faviconIco() {
        ResponseEntity<String> res = getServiceOfBlankFaviconIcoController().favicon();
        Assertions.assertTrue(Objects.requireNonNull(res.getBody()).isEmpty());
    }

    @Override
    public void httpUtils() {

    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityString() {
        ResponseEntity<String> stringResponseEntity = getServiceOfTestControllerInterface().excludeUnifiedResponseResponseEntityString();
        Assertions.assertNotNull(stringResponseEntity);
        Assertions.assertEquals(TestController.RESPONSE_STRING, stringResponseEntity.getBody());
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityObject() {
        ResponseEntity<JwtTokenRequestDto> jwtTokenRequestDtoResponseEntity =
            getServiceOfTestControllerInterface().excludeUnifiedResponseResponseEntityObject();
        Assertions.assertNotNull(jwtTokenRequestDtoResponseEntity);
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, jwtTokenRequestDtoResponseEntity.getBody());
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityBoolean() {
        ResponseEntity<Boolean> booleanResponseEntity = getServiceOfTestControllerInterface().excludeUnifiedResponseResponseEntityBoolean();
        Assertions.assertNotNull(booleanResponseEntity);
        Assertions.assertEquals(TestController.BOOL, booleanResponseEntity.getBody());
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityVoid() {
        ResponseEntity<Void> responseEntity = getServiceOfTestControllerInterface().excludeUnifiedResponseResponseEntityVoid();
        Assertions.assertNotNull(responseEntity);
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityMap() {
        ResponseEntity<Map<?, ?>> mapResponseEntity = getServiceOfTestControllerInterface().excludeUnifiedResponseResponseEntityMap();
        Assertions.assertNotNull(mapResponseEntity);
        Assertions.assertEquals(TestController.RESPONSE_MAP, mapResponseEntity.getBody());
    }

    @Test
    @Override
    public void excludeUnifiedResponseResponseEntityNumber() {
        ResponseEntity<Number> responseEntity = getServiceOfTestControllerInterface().excludeUnifiedResponseResponseEntityNumber();
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(TestController.NUMBER, responseEntity.getBody());
    }

    @Test
    @Override
    public void excludeUnifiedResponseString() {
        String responseString = getServiceOfTestControllerInterface().excludeUnifiedResponseString();
        Assertions.assertNotNull(responseString);
        Assertions.assertEquals(TestController.RESPONSE_STRING, responseString);
    }

    @Override
    public void excludeUnifiedResponseObject() {
        JwtTokenRequestDto responseObject = getServiceOfTestControllerInterface().excludeUnifiedResponseObject();
        Assertions.assertNotNull(responseObject);
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, responseObject);
    }

    @Test
    @Override
    public void excludeUnifiedResponseBoolean() {
        Boolean bool = getServiceOfTestControllerInterface().excludeUnifiedResponseBoolean();
        Assertions.assertNotNull(bool);
        Assertions.assertEquals(TestController.BOOL, bool);
    }

    @Test
    @Override
    public void excludeUnifiedResponseVoid() {
        getServiceOfTestControllerInterface().excludeUnifiedResponseVoid();
    }

    @Test
    @Override
    public void excludeUnifiedResponseNumber() {
        Number number = getServiceOfTestControllerInterface().excludeUnifiedResponseNumber();
        Assertions.assertNotNull(number);
        Assertions.assertEquals(TestController.NUMBER, number);
    }

    @Test
    @Override
    public void excludeUnifiedResponseMap() {
        Map<?, ?> map = getServiceOfTestControllerInterface().excludeUnifiedResponseMap();
        Assertions.assertNotNull(map);
        Assertions.assertEquals(TestController.RESPONSE_MAP, map);
    }

    @Override
    public void unifiedRequestMapGetQuery() {

    }

    @Test
    @Override
    public void unifiedRequestObjectGetQuery() {
        AbstractHttpServiceFeignClientTest.LOGGER.info(JsonUtils.objectToJson(TestController.JWT_TOKEN_REQUEST_DTO));
        JwtTokenRequestDto res = getServiceOfTestControllerInterface().unifiedRequestGetObject(TestController.JWT_TOKEN_REQUEST_DTO);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, res);
    }

    @Test
    @Override
    public void unifiedRequestObjectGetQueryUnderline() {

    }

    @Test
    @Override
    public void unifiedRequestObjectGetQueryMixed() {

    }

    @Test
    @Override
    public void unifiedRequestObjectPostJson() {
        JwtTokenRequestDto responseDto = getServiceOfTestControllerInterface().unifiedRequestObject(TestController.JWT_TOKEN_REQUEST_DTO);
        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, responseDto);
    }

    @Override
    public void unifiedRequestGenericObjectPostJson() {

    }

    @Override
    public void unifiedRequestGenericObjectGet() {

    }

    @Test
    @Override
    public void unifiedRequestMapPostJson() {
    }

    @Test
    @Override
    public void unifiedRequestObjectPostFormData() {
    }

    @Test
    @Override
    public void unifiedRequestObjectPostFormDataUnderline() {
    }

    @Test
    @Override
    public void unifiedRequestObjectPostFormDataMixed() {

    }

    @Test
    @Override
    public void unifiedRequestObjectPostUrlencoded() {

    }

    @Test
    @Override
    public void unifiedRequestObjectPostUrlencodedUnderline() {
    }

    @Test
    @Override
    public void unifiedRequestObjectPostUrlencodedMixed() {

    }

    @Test
    @Override
    public void unifiedRequestLocalDateTime() {
        LocalTime localTime = LocalTime.now();
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.now();

        LocalDateTimeDto localDateTimeDto = new LocalDateTimeDto();
        localDateTimeDto.setTheLocalTime(localTime);
        localDateTimeDto.setTheLocalDate(localDate);
        localDateTimeDto.setTheLocalDateTime(localDateTime);
        LocalDateTimeDto response = getServiceOfTestControllerInterface().unifiedRequestLocalDateTime(localDateTimeDto);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(localDateTimeDto, response);
    }

    @Test
    @Override
    public void unifiedRequestValidatedNow() {
        // 全新的请求对象
        JwtTokenRequestDto requestDto = ObjectUtils.deepCopy(TestController.JWT_TOKEN_REQUEST_DTO);
        final Integer timestamp = 1234;
        // 设置一个错误的时间戳
        requestDto.setTimestamp(timestamp);

        Assertions.assertThrows(RuntimeException.class, () -> {
            getServiceOfTestControllerInterface().unifiedRequestValidatedObject(requestDto);
        });

        // 设置一个正确的时间戳
        requestDto.setTimestamp(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));
        JwtTokenRequestDto responseDto = getServiceOfTestControllerInterface().unifiedRequestValidatedObject(requestDto);

        Assertions.assertNotNull(responseDto);
        Assertions.assertEquals(requestDto, responseDto);
    }

    @Test
    @Override
    public void unifiedRequestValidatedDateTimeFormat() {

    }

    @Test
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

        Assertions.assertThrows(RuntimeException.class, () -> {
            getServiceOfTestControllerInterface().unifiedRequestValidatedObject(requestDto);
        });

        // 设置一个正确的时间戳
        requestDto.setValidTime(122);
        JwtTokenRequestDto responseDto = getServiceOfTestControllerInterface().unifiedRequestValidatedObject(requestDto);
        Assertions.assertEquals(requestDto, responseDto);
    }

    @Test
    @Override
    public void unifiedRequestValidatedSpring() {
        // 全新的请求对象
        JwtTokenRequestDto requestDto = ObjectUtils.deepCopy(TestController.JWT_TOKEN_REQUEST_DTO);
        final String id = "12343";
        // 设置一个错误的id
        requestDto.setId(id);

        Assertions.assertThrows(RuntimeException.class, () -> getServiceOfTestControllerInterface().unifiedRequestValidatedObject(requestDto));

        // 设置一个正确的id
        requestDto.setId(UUID.randomUUID().toString());
        JwtTokenRequestDto responseDto = getServiceOfTestControllerInterface().unifiedRequestValidatedObject(requestDto);
        Assertions.assertEquals(requestDto, responseDto);
    }

    @Test
    @Override
    public void unifiedResponseThrowBusinessException() {
        Assertions.assertThrows(BusinessException.class, () -> {
            getServiceOfTestControllerInterface().unifiedResponseThrowBusinessException();
        });
    }

    @Test
    @Override
    public void unifiedResponseThrowException() {
        Assertions.assertThrows(Throwable.class, () -> {
            getServiceOfTestControllerInterface().unifiedResponseThrowException();
            AbstractHttpServiceFeignClientTest.LOGGER.info("不应该出现的文本！！");
        });
    }

    @Test
    @Override
    public void unifiedResponseString() {
        String s = getServiceOfTestControllerInterface().unifiedResponseString();
        Assertions.assertEquals(TestController.RESPONSE_STRING, s);
    }

    @Test
    @Override
    public void unifiedResponseObject() {
        JwtTokenRequestDto responseObject = getServiceOfTestControllerInterface().unifiedResponseObject();
        Assertions.assertEquals(TestController.JWT_TOKEN_REQUEST_DTO, responseObject);
    }

    @Test
    @Override
    public void unifiedResponseBoolean() {
        Boolean aBoolean = getServiceOfTestControllerInterface().unifiedResponseBoolean();
        Assertions.assertEquals(TestController.BOOL, aBoolean);
    }

    @Test
    @Override
    public void unifiedResponseVoid() {
        getServiceOfTestControllerInterface().unifiedResponseVoid();
    }

    @Test
    @Override
    public void unifiedResponseNumber() {
        Number number = getServiceOfTestControllerInterface().unifiedResponseNumber();
        Assertions.assertEquals(TestController.NUMBER, number);
    }

    @Test
    @Override
    public void unifiedResponseMap() {
        Map<Object, Object> responseMap = getServiceOfTestControllerInterface().unifiedResponseMap();
        Assertions.assertEquals(TestController.RESPONSE_MAP, responseMap);
    }
}

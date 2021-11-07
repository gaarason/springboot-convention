package gaarason.convention.test.common.web.contract.trait;

import gaarason.convention.test.common.web.pojo.BaseRequestDto;
import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import gaarason.convention.test.common.web.pojo.LocalDateTimeDto;
import gaarason.convention.test.common.web.pojo.RequestDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 统一参数解析相关
 * @author xt
 */
public interface TestControllerUnifiedRequestTrait {

    /**
     * 统一参数解析
     * @param jwtTokenRequestDto 请求
     * @return 响应
     */
    @RequestMapping("unifiedRequestObject")
    JwtTokenRequestDto unifiedRequestObject(JwtTokenRequestDto jwtTokenRequestDto);

    /**
     * 统一参数解析, 指定get请求方式
     * @param jwtTokenRequestDto 请求
     * @return 响应
     */
    @GetMapping("unifiedRequestGetObject")
    JwtTokenRequestDto unifiedRequestGetObject(JwtTokenRequestDto jwtTokenRequestDto);

    /**
     * 统一参数解析
     * @param requestMap 请求
     * @return 响应
     */
    @RequestMapping("unifiedRequestMap")
    Map<Object, Object> unifiedRequestMap(Map<Object, Object> requestMap);

    /**
     * 统一参数解析, 参数认证
     * @param jwtTokenRequestDto 请求
     * @return 响应
     */
    @RequestMapping("unifiedRequestValidatedObject")
    JwtTokenRequestDto unifiedRequestValidatedObject(@Validated JwtTokenRequestDto jwtTokenRequestDto);

    /**
     * 统一参数解析, 参数是泛型
     * @param requestDto 请求
     * @return 响应
     */
    @RequestMapping("unifiedRequestGenericObject")
    RequestDto unifiedRequestGenericObject(RequestDto requestDto);

    /**
     * 统一参数解析, 参数是泛型
     * @param requestDto 请求
     * @return 响应
     */
    @RequestMapping("unifiedRequestGenericObject2")
    String unifiedRequestGenericObject2(BaseRequestDto<String> requestDto);

    /**
     * 统一参数解析, 参数是含 LocalDate, LocalTime, LocalDateTime
     * @param requestDto 请求
     * @return 响应
     */
    @RequestMapping("unifiedRequestLocalDateTime")
    LocalDateTimeDto unifiedRequestLocalDateTime(LocalDateTimeDto requestDto);

}

package gaarason.convention.test.common.web.contract.trait;

import gaarason.convention.common.model.annotation.web.ExcludeUnifiedRequest;
import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author xt
 */
public interface TestControllerExcludeUnifiedRequestTrait {

    /**
     * 非统一参数解析, 请求方式get, 参数签名为对象
     * @param jwtTokenRequestDto 请求
     * @return JwtTokenRequestDto
     */
    @GetMapping("excludeUnifiedRequestGetObject")
    JwtTokenRequestDto excludeUnifiedRequestGetObject(@ExcludeUnifiedRequest JwtTokenRequestDto jwtTokenRequestDto);

    /**
     * 非统一参数解析, 请求方式get, 参数签名为map
     * @param requestMap map
     * @return Map m
     */
    @GetMapping("excludeUnifiedRequestGetMap")
    Map<Object, Object> excludeUnifiedRequestGetMap(@ExcludeUnifiedRequest Map<Object, Object> requestMap);

    /**
     * 非统一参数解析, 请求方式get, 参数签名为对象, 参数认证
     * @param jwtTokenRequestDto dto
     * @return JwtTokenRequestDto 请求对象
     */
    @GetMapping("excludeUnifiedRequestValidatedGetObject")
    JwtTokenRequestDto excludeUnifiedRequestValidatedGetObject(@Validated @ExcludeUnifiedRequest JwtTokenRequestDto jwtTokenRequestDto);

    /**
     * 非统一参数解析, 请求方式post, 参数签名为对象
     * @param jwtTokenRequestDto dto
     * @return JwtTokenRequestDto 请求对象
     */
    @PostMapping("excludeUnifiedRequestPostObject")
    JwtTokenRequestDto excludeUnifiedRequestPostObject(@ExcludeUnifiedRequest JwtTokenRequestDto jwtTokenRequestDto);

    /**
     * 非统一参数解析, 请求方式post, 参数签名为map
     * @param requestMap map
     * @return Map 集合
     */
    @PostMapping("excludeUnifiedRequestPostMap")
    Map<Object, Object> excludeUnifiedRequestPostMap(@ExcludeUnifiedRequest Map<Object, Object> requestMap);

    /**
     * 非统一参数解析, 请求方式post, 参数签名为对象, 参数认证
     * @param jwtTokenRequestDto dto
     * @return JwtTokenRequestDto j
     */
    @PostMapping("excludeUnifiedRequestValidatedPostObject")
    JwtTokenRequestDto excludeUnifiedRequestValidatedPostObject(@Validated @ExcludeUnifiedRequest JwtTokenRequestDto jwtTokenRequestDto);

    /**
     * 非统一参数解析, 请求方式任意
     * @param jwtTokenRequestDto j
     * @return JwtTokenRequestDto jwt
     */
    @RequestMapping("excludeUnifiedRequestObject")
    JwtTokenRequestDto excludeUnifiedRequestObject(@ExcludeUnifiedRequest JwtTokenRequestDto jwtTokenRequestDto);

    /**
     * 非统一参数解析, 请求方式任意
     * @param requestMap r
     * @return Map m
     */
    @RequestMapping("excludeUnifiedRequestMap")
    Map<Object, Object> excludeUnifiedRequestMap(@ExcludeUnifiedRequest Map<Object, Object> requestMap);

    /**
     * 非统一参数解析, 请求方式任意, 参数认证
     * @param jwtTokenRequestDto j
     * @return JwtTokenRequestDto jwt
     */
    @RequestMapping("excludeUnifiedRequestValidatedObject")
    JwtTokenRequestDto excludeUnifiedRequestValidatedObject(@Validated @ExcludeUnifiedRequest JwtTokenRequestDto jwtTokenRequestDto);
}

package gaarason.convention.test.common.web.contract.trait;

import gaarason.convention.common.model.annotation.web.ExcludeUnifiedResponse;
import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author xt
 */
public interface TestControllerExcludeUnifiedResponseTrait {

    /**
     * 非统一响应, 接口响应定义为String
     * @return String s
     */
    @GetMapping("excludeUnifiedResponseString")
    @ExcludeUnifiedResponse
    String excludeUnifiedResponseString();

    /**
     * 非统一响应, 接口响应定义为自定义java对象
     * @return JwtTokenRequestDto 请求体
     */
    @GetMapping("excludeUnifiedResponseObject")
    @ExcludeUnifiedResponse
    JwtTokenRequestDto excludeUnifiedResponseObject();

    /**
     * 非统一响应, 接口响应定义为Boolean
     * @return Boolean 结果
     */
    @GetMapping("excludeUnifiedResponseBoolean")
    @ExcludeUnifiedResponse
    Boolean excludeUnifiedResponseBoolean();

    /**
     * 非统一响应, 接口响应定义为Void
     */
    @GetMapping("excludeUnifiedResponseVoid")
    @ExcludeUnifiedResponse
    void excludeUnifiedResponseVoid();

    /**
     * 非统一响应, 接口响应定义为Number
     * @return Number 数字
     */
    @GetMapping("excludeUnifiedResponseNumber")
    @ExcludeUnifiedResponse
    Number excludeUnifiedResponseNumber();

    /**
     * 非统一响应, 接口响应定义为Map<?, ?>
     * @return Map  集合
     */
    @GetMapping("excludeUnifiedResponseMap")
    @ExcludeUnifiedResponse
    Map<?, ?> excludeUnifiedResponseMap();
}

package gaarason.convention.test.common.web.contract.trait;

import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 统一响应相关
 * @author xt
 */
public interface TestControllerUnifiedResponseTrait {

    /**
     * 统一响应, 接口响应定义为String
     * @return String 结果
     */
    @GetMapping("unifiedResponseString")
    String unifiedResponseString();

    /**
     * 统一响应, 接口响应定义为自定义java对象
     * @return JwtTokenRequestDto 请求对象
     */
    @GetMapping("unifiedResponseObject")
    JwtTokenRequestDto unifiedResponseObject();

    /**
     * 统一响应, 接口响应定义为Boolean
     * @return Boolean 是否正常
     */
    @GetMapping("unifiedResponseBoolean")
    Boolean unifiedResponseBoolean();

    /**
     * 统一响应, 接口响应定义为Void
     */
    @GetMapping("unifiedResponseVoid")
    void unifiedResponseVoid();

    /**
     * 统一响应, 接口响应定义为Number
     * @return Number 数字
     */
    @GetMapping("unifiedResponseNumber")
    Number unifiedResponseNumber();

    /**
     * 统一响应, 接口响应定义为Map
     * @return MAP m
     */
    @GetMapping("unifiedResponseMap")
    Map<Object, Object> unifiedResponseMap();
}

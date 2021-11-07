package gaarason.convention.test.common.web.contract.trait;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author xt
 */
public interface HttpServiceTestsExcludeUnifiedResponseResponseEntityTrait {

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<String>
     */
    void excludeUnifiedResponseResponseEntityString();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<自定义java对象>
     *
     * @throws JsonProcessingException 异常
     */
    void excludeUnifiedResponseResponseEntityObject() throws JsonProcessingException;

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<Boolean>
     */
    void excludeUnifiedResponseResponseEntityBoolean();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<Void>
     */
    void excludeUnifiedResponseResponseEntityVoid();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<Number>
     */
    void excludeUnifiedResponseResponseEntityNumber();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<Map<?, ?>>
     *
     * @throws JsonProcessingException 异常
     */
    void excludeUnifiedResponseResponseEntityMap() throws JsonProcessingException;
}

package gaarason.convention.test.common.web.contract.trait;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author xt
 */
public interface HttpServiceTestsExcludeUnifiedResponseTrait {

    /**
     * 非统一响应, 接口响应定义为String
     */
    void excludeUnifiedResponseString();

    /**
     * 非统一响应, 接口响应定义为自定义java对象
     *
     * @throws JsonProcessingException e异常
     */
    void excludeUnifiedResponseObject() throws JsonProcessingException;

    /**
     * 非统一响应, 接口响应定义为Boolean
     */
    void excludeUnifiedResponseBoolean();

    /**
     * 非统一响应, 接口响应定义为Void
     */
    void excludeUnifiedResponseVoid();

    /**
     * 非统一响应, 接口响应定义为Number
     */
    void excludeUnifiedResponseNumber();

    /**
     * 非统一响应, 接口响应定义为Map<?, ?>
     *
     * @throws JsonProcessingException j
     */
    void excludeUnifiedResponseMap() throws JsonProcessingException;
}

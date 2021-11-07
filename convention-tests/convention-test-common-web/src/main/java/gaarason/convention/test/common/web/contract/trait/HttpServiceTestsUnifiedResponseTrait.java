package gaarason.convention.test.common.web.contract.trait;

/**
 * 统一响应相关
 *
 * @author xt
 */
public interface HttpServiceTestsUnifiedResponseTrait {

    /**
     * 统一响应, 接口响应定义为String
     */
    void unifiedResponseString();

    /**
     * 统一响应, 接口响应定义为自定义java对象
     */
    void unifiedResponseObject();

    /**
     * 统一响应, 接口响应定义为Boolean
     */
    void unifiedResponseBoolean();

    /**
     * 统一响应, 接口响应定义为Void
     */
    void unifiedResponseVoid();

    /**
     * 统一响应, 接口响应定义为Number
     */
    void unifiedResponseNumber();

    /**
     * 统一响应, 接口响应定义为Map
     */
    void unifiedResponseMap();
}

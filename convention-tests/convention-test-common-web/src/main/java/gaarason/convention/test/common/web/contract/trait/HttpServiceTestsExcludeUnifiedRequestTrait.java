package gaarason.convention.test.common.web.contract.trait;

/**
 * 统一参数解析相关(非)
 *
 * @author xt
 */
public interface HttpServiceTestsExcludeUnifiedRequestTrait {

    /**
     * 非统一参数解析, get 参数签名为对象
     */
    void excludeUnifiedRequestGetObject();

    /**
     * 非统一参数解析, get 参数签名为map
     */
    void excludeUnifiedRequestGetMap();

    /**
     * 非统一参数解析, get 参数签名为对象, 参数认证
     */
    void excludeUnifiedRequestValidatedGetObject();

    /**
     * 非统一参数解析, post json 参数签名为对象
     */
    void excludeUnifiedRequestPostObjectJson();

    /**
     * 非统一参数解析, post form-data 参数签名为对象
     */
    void excludeUnifiedRequestPostObjectJsonFormData();

    /**
     * 非统一参数解析, post x-www-form-urlencoded 参数签名为对象
     */
    void excludeUnifiedRequestPostObjectJsonUrlencoded();

    /**
     * 非统一参数解析, post form-data 参数签名为map
     */
    void excludeUnifiedRequestPostMapJson();

    /**
     * 非统一参数解析, post form-data 参数签名为map
     */
    void excludeUnifiedRequestPostMapFormData();

    /**
     * 非统一参数解析, post x-www-form-urlencoded 参数签名为map
     */
    void excludeUnifiedRequestPostMapFormUrlencoded();

    // ----------------------- 参数实例化之后的参数认证都是统一逻辑, 所以不需要根据不同的解析方式测试 ------------------------//

    /**
     * 非统一参数解析, post json 参数认证 @Now
     */
    void excludeUnifiedRequestValidatedPostObjectNow();

    /**
     * 非统一参数解析, post json 参数认证 @DateTimeFormat
     */
    void excludeUnifiedRequestValidatedPostObjectDateTimeFormat();

    /**
     * 非统一参数解析, post json 参数认证 @Format
     */
    void excludeUnifiedRequestValidatedPostObjectFormat();

    /**
     * 非统一参数解析, post json 参数认证 @NumberRange
     */
    void excludeUnifiedRequestValidatedPostObjectRange();

    /**
     * 非统一参数解析, post json 参数认证 spring 的自带注解
     */
    void excludeUnifiedRequestValidatedPostObjectSpring();

}

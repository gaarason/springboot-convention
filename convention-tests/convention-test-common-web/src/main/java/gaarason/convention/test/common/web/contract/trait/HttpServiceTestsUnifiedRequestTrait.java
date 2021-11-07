package gaarason.convention.test.common.web.contract.trait;

/**
 * 统一参数解析相关
 *
 * @author xt
 */
public interface HttpServiceTestsUnifiedRequestTrait {

    /**
     * 统一参数解析, get 参数接收, map参数定义
     */
    void unifiedRequestMapGetQuery();

    /**
     * 统一参数解析, get 参数接收
     */
    void unifiedRequestObjectGetQuery();

    /**
     * 统一参数解析, get 参数下划线形式接收
     */
    void unifiedRequestObjectGetQueryUnderline();

    /**
     * 统一参数解析, get 参数混合形式接收
     */
    void unifiedRequestObjectGetQueryMixed();

    /**
     * 统一参数解析, post json 参数接收
     */
    void unifiedRequestObjectPostJson();

    /**
     * 统一参数解析, post json 参数接收, 参数是泛型
     */
    void unifiedRequestGenericObjectPostJson();

    /**
     * 统一参数解析, get 参数接收, 参数是泛型
     */
    void unifiedRequestGenericObjectGet();

    /**
     * 统一参数解析, post json 参数接收
     */
    void unifiedRequestMapPostJson();

    /**
     * 统一参数解析, post form-data 参数接收
     */
    void unifiedRequestObjectPostFormData();

    /**
     * 统一参数解析, post form-data 下划线形式接收
     */
    void unifiedRequestObjectPostFormDataUnderline();

    /**
     * 统一参数解析, post form-data 混合形式接收
     */
    void unifiedRequestObjectPostFormDataMixed();

    /**
     * 统一参数解析, post x-www-form-urlencoded 参数接收
     */
    void unifiedRequestObjectPostUrlencoded();

    /**
     * 统一参数解析, post x-www-form-urlencoded 下划线形式接收
     */
    void unifiedRequestObjectPostUrlencodedUnderline();

    /**
     * 统一参数解析, post x-www-form-urlencoded 混合形式接收
     */
    void unifiedRequestObjectPostUrlencodedMixed();

    /**
     * 统一参数解析, 参数是含 LocalDate, LocalTime, LocalDateTime
     */
    void unifiedRequestLocalDateTime();

    // ----------------------- 参数实例化之后的参数认证都是统一逻辑, 所以不需要根据不同的解析方式测试 ------------------------//

    /**
     * 统一参数解析, post json 参数认证 @Now
     */
    void unifiedRequestValidatedNow();

    /**
     * 统一参数解析, post json 参数认证 @DateTimeFormat
     */
    void unifiedRequestValidatedDateTimeFormat();

    /**
     * 统一参数解析, post json 参数认证 @Format
     */
    void unifiedRequestValidatedFormat();

    /**
     * 统一参数解析, post json 参数认证 @NumberRange
     */
    void unifiedRequestValidatedNumberRange();

    /**
     * 统一参数解析, post json 参数认证 spring 的自带注解
     */
    void unifiedRequestValidatedSpring();

}

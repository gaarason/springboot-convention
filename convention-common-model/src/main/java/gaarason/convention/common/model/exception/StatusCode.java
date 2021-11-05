package gaarason.convention.common.model.exception;

/**
 * @author xt
 */
public enum StatusCode {
    /**************************** 成功 ******************************/
    SUCCESS(0, "ok"),
    /**************************** 系统错误 ******************************/
    DEFAULT_ERROR(400000, "未分类的异常"),
    NOT_ALLOW_INSTANCE(400001, "不允许被实例化"),
    TENANT_CODE_NOT_FOUND(440001, "获取不到租户code"),
    TENANT_CONNECTION_INFO_NOT_FOUND(440002, "获取不到租户连接信息(配置库中没有)"),
    CHAIN_CONTEXT_PARAMETER_IS_NULL(440003, "清空ChainContext请求调用clear()方法"),
    LOGGING_DEPENDENCY_NOT_EXCLUDE(400010, "spring-boot-starter-logging包未被排除，请检查引入的依赖是否带有此包"),
    REFLECTION_ERROR(400011, "反射异常，请检查是否因为版本升级导致方法不一致"),
    ARGUMENT_IS_NULL(400012, "指定参数为null"),
    CONFIG_FILE_NOT_FOUND(400013, "配置文件找不到"),
    NOT_ALLOW_CALL(400004, "不允许调用此方法"),
    SPECIAL_ERROR(400002, "特殊处理的异常"),
    URL_ENCODE_ERROR(400003, "HTTP参数构造异常"),
    HTTP_REQUEST_API_ERROR(400004, "HTTP请求其他接口异常"),
    HTTP_REQUEST_API_RETRY_TIME_ERROR(400005, "HTTP请求其他接口重试次数异常"),
    HTTP_REQUEST_BODY_TYPE_VALIDATION_FAIL(400006, "HTTP请求其他接口时同时使用了两种请求体格式"),
    CALL_NOT_SUPPORTED(400007, "不支持的调用"),
    RESPONSE_TYPE_SUPPORTED(400008, "没找到可用的响应解析器"),
    HTTP_CLIENT_BAD_REQUEST(400400, "接收到来自HTTP客户端的不合法的请求"),
    HTTP_NOT_FOUND(400404, "HTTP路由不正确"),
    HTTP_METHOD_ERROR(400405, "HTTP方法不支持"),
    REQUEST_ENTITY_TOO_LARGE(413, "HTTP请求过大"),
    PARAMETER_VALIDATION_ERROR(423000, "参数效验不通过"),
    PARAMETER_NOT_READABLE(423001, "请求参数无法正确解析"),
    PARAMETER_MEDIA_TYPE_ERROR(423003, "请求参数类型不支持"),
    REQUIRED_PARAMETER_DOES_NOT_EXIST(423404, "必要请求参数不存在"),
    INTERNAL_ERROR(500000, "系统内部错误");

    /**************************** 业务上的自定义错误, 建议 600000 以后******************************/

    /**
     * 业务状态码
     */
    private final int code;

    /**
     * 描述信息
     */
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

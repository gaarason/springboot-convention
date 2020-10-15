package gaarason.springboot.convention.common.pojo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@ApiModel("响应状态码")
public enum StatusCode {

    /**************************** 成功 ******************************/
    SUCCESS(0, "ok"),
    /**************************** 系统错误 ******************************/
    INTERNAL_ERROR(500000, "系统内部错误"),
    /**************************** 请求参数错误 422000 - 422999 请不要使用 ******************************/
    HTTP_REQUEST_VALIDATION_ERROR(423000, "参数效验不通过"),
    HTTP_MESSAGE_NOT_READABLE(423001, "请求参数无法正确解析"),
    HTTP_METHOD_ERROR(423002, "HTTP方法不支持"),
    /****************************  ******************************/
    E400001(400002, "测试用的异常");
    /****************************  ******************************/

    @Getter
    private final int code;

    @Getter
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
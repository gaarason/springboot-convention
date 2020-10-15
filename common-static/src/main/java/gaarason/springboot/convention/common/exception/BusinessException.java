package gaarason.springboot.convention.common.exception;

import gaarason.springboot.convention.common.contract.ErrorLogFunctionalInterface;
import gaarason.springboot.convention.common.pojo.StatusCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 本项目异常基类
 */
@Slf4j
public class BusinessException extends RuntimeException {

    @Getter
    protected int code;

    @Getter
    protected String message;

    @Getter
    protected Map<Object, Object> debug = new HashMap<>();

    public BusinessException(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }

    public BusinessException(StatusCode statusCode, String message) {
        this.code = statusCode.getCode();
        this.message = message;
    }

    public BusinessException(StatusCode statusCode, String message, ErrorLogFunctionalInterface makeDebug) {
        this.code = statusCode.getCode();
        this.message = message;
        makeDebug.run(debug);
    }

    public BusinessException(StatusCode statusCode, ErrorLogFunctionalInterface makeDebug) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        makeDebug.run(debug);
    }

    public BusinessException(StatusCode statusCode, ErrorLogFunctionalInterface makeDebug, Throwable e) {
        super(e);
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        makeDebug.run(debug);
    }

    public BusinessException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, String message, ErrorLogFunctionalInterface makeDebug) {
        this.code = code;
        this.message = message;
        makeDebug.run(debug);
    }

    public BusinessException(int code, String message, ErrorLogFunctionalInterface makeDebug, Throwable e) {
        super(e);
        this.code = code;
        this.message = message;
        makeDebug.run(debug);
    }
}

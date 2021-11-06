package gaarason.convention.common.model.exception;

import java.io.Serializable;
import java.util.*;

/**
 * 异常
 * @author xt
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码, 建议6位
     */
    protected final int code;

    /**
     * 错误补充信息, 出现在响应对象的 data 中
     */
    protected final Serializable error;

    /**
     * 错误补充信息, 仅出现在日志记录中, 不会出现的响应中
     */
    protected final HashMap<Object, Object> debug = new HashMap<>(16);

    public BusinessException(int code, String message, Serializable error, DebugFunctionalInterface makeDebug) {
        super(message);
        this.code = code;
        this.error = error;
        makeDebug.run(debug);
    }

    public BusinessException(int code, String message, Serializable error, DebugFunctionalInterface makeDebug, Throwable e) {
        super(message, e);
        this.code = code;
        this.error = error;
        makeDebug.run(debug);
    }

    public BusinessException(int code, String message) {
        this(code, message, null, map -> {});
    }

    public BusinessException(int code, String message, Throwable e) {
        this(code, message, null, map -> {}, e);
    }

    public BusinessException(StatusCode statusCode, Serializable error, DebugFunctionalInterface makeDebug) {
        this(statusCode.getCode(), statusCode.getMessage(), error, makeDebug);
    }

    public BusinessException(StatusCode statusCode, Serializable error, DebugFunctionalInterface makeDebug, Throwable e) {
        this(statusCode.getCode(), statusCode.getMessage(), error, makeDebug, e);
    }

    public BusinessException(StatusCode statusCode, DebugFunctionalInterface makeDebug) {
        this(statusCode.getCode(), statusCode.getMessage(), null, makeDebug);
    }

    public BusinessException(StatusCode statusCode, DebugFunctionalInterface makeDebug, Throwable e) {
        this(statusCode.getCode(), statusCode.getMessage(), null, makeDebug, e);
    }

    public BusinessException(StatusCode statusCode) {
        this(statusCode.getCode(), statusCode.getMessage(), null, map -> {
        });
    }

    public BusinessException(String message, DebugFunctionalInterface makeDebug) {
        this(StatusCode.DEFAULT_ERROR.getCode(), message, null, makeDebug);
    }

    public BusinessException(String message, DebugFunctionalInterface makeDebug, Throwable e) {
        this(StatusCode.DEFAULT_ERROR.getCode(), message, null, makeDebug, e);
    }

    public BusinessException(String message) {
        this(StatusCode.DEFAULT_ERROR.getCode(), message, null, map -> {
        });
    }

    public BusinessException(String message, Throwable e) {
        this(StatusCode.DEFAULT_ERROR.getCode(), message, null, map -> {
        }, e);
    }

    public BusinessException(StatusCode statusCode, Throwable e) {
        this(statusCode.getCode(), statusCode.getMessage(), null, map -> {
        }, e);
    }

    public BusinessException(StatusCode statusCode, Serializable error) {
        this(statusCode.getCode(), statusCode.getMessage(), error, map -> {
        });
    }

    public BusinessException(StatusCode statusCode, Serializable error, Throwable e) {
        this(statusCode.getCode(), statusCode.getMessage(), error, map -> {
        }, e);
    }

    public BusinessException(StatusCode statusCode, String message) {
        this(statusCode.getCode(), message, null, map -> {
        });
    }

    public BusinessException(StatusCode statusCode, String message, Throwable e) {
        this(statusCode.getCode(), message, null, map -> {
        }, e);
    }

    public BusinessException() {
        this(StatusCode.INTERNAL_ERROR.getCode(), null, null, map -> {
        });
    }

    public BusinessException(Throwable e) {
        this(StatusCode.INTERNAL_ERROR.getCode(), null, null, map -> {
        }, e);
    }

    public int getCode() {
        return code;
    }

    public Serializable getError() {
        return error;
    }

    public Map<Object, Object> getDebug() {
        return debug;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        StackTraceElement[] stackTrace = super.getStackTrace();
        List<StackTraceElement> stackTraceElements = new ArrayList<>(Arrays.asList(stackTrace));
        StackTraceElement errorElement = new StackTraceElement(getClass().toString().replace("class ", ""), "ERROR", error.toString(), 1);
        StackTraceElement debugElement = new StackTraceElement(getClass().toString().replace("class ", ""), "DEBUG", debug.toString(), debug.size());
        stackTraceElements.add(errorElement);
        stackTraceElements.add(debugElement);
        return stackTraceElements.toArray(new StackTraceElement[0]);
    }

    @FunctionalInterface
    public interface DebugFunctionalInterface {

        /**
         * 调试信息补充
         * @param map 调试信息记录MAP
         */
        void run(Map<Object, Object> map);
    }

}

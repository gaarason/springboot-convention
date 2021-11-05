package gaarason.convention.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gaarason.convention.common.model.exception.StatusCode;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * 统一响应 兼容 jackson
 * @param <T>
 * @author xt
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultVO<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码, 0 表示正常, 非0 表示异常
     */
    protected Integer code;

    /**
     * 响应提示
     */
    protected String message;

    /**
     * 响应内容
     */
    @Nullable
    protected T data;

    /**
     * 堆栈信息, 仅在非生产环境时存在
     */
    protected String stackTrace;

    /**
     * 请求标记
     */
    protected String traceId;

    /**
     * 请求路径
     */
    protected String requestUrl;

    /**
     * 请求时间
     */
    protected String requestDatetime;

    /**
     * 响应时间
     */
    protected String responseDatetime;

    /**
     * 应用名称
     */
    protected String applicationName;

    @JsonIgnore
    @Nullable
    protected Throwable exception;

    /**
     * 正确的响应
     */
    public ResultVO() {
        code = StatusCode.SUCCESS.getCode();
        message = StatusCode.SUCCESS.getMessage();
        data = null;
        stackTrace = "";
        traceId = "";
        requestUrl = "";
        requestDatetime = "";
        responseDatetime = "";
        applicationName = "";
        exception = null;
    }

    /**
     * 正确的响应
     * @param data 响应内容
     */
    public ResultVO(T data) {
        this();
        this.data = data;
    }

    /**
     * 错误的响应
     * @param code    状态码
     * @param message 提示消息
     */
    public ResultVO(int code, String message) {
        this();
        this.code = code;
        this.message = message;
        data = null;
    }

    /**
     * 错误的响应
     * @param code    状态码
     * @param message 提示消息
     * @param data    补充信息
     */
    public ResultVO(int code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Nullable
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestDatetime() {
        return requestDatetime;
    }

    public void setRequestDatetime(String requestDatetime) {
        this.requestDatetime = requestDatetime;
    }

    public String getResponseDatetime() {
        return responseDatetime;
    }

    public void setResponseDatetime(String responseDatetime) {
        this.responseDatetime = responseDatetime;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Nullable
    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "ResultVO{" + "code=" + code + ", message='" + message + '\'' + ", data=" + data + ", stackTrace='" + stackTrace + '\'' + ", traceId='" + traceId
            + '\'' + ", requestUrl='" + requestUrl + '\'' + ", requestDatetime='" + requestDatetime + '\'' + ", responseDatetime='" + responseDatetime + '\''
            + ", applicationName='" + applicationName + '\'' + '}';
    }
}

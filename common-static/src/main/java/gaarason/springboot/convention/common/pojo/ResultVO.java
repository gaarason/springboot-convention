package gaarason.springboot.convention.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应
 * @param <T>
 */
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultVO<T> implements Serializable {

    @ApiModelProperty(value = "响应状态码", example = "200", required = true)
    private int code = StatusCode.SUCCESS.getCode();

    @ApiModelProperty(value = "响应消息", example = "OK", required = true)
    private String message = StatusCode.SUCCESS.getMessage();

    @ApiModelProperty(value = "响应内容")
    private T data;

    /**
     * 正确的响应
     */
    public ResultVO() {
        this.data = null;
    }

    /**
     * 正确的响应
     * @param data 响应内容
     */
    public ResultVO(T data) {
        this.data = data;
    }

    /**
     * 错误的响应
     * @param code    状态码
     * @param message 提示消息
     */
    public ResultVO(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    /**
     * 错误的响应
     * @param code    状态码
     * @param message 提示消息
     * @param data    补充信息
     */
    public ResultVO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
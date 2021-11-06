package gaarason.convention.starter.webmvc.pojo;

import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.util.ResultUtils;
import gaarason.convention.common.web.pojo.ResultExpand;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/**
 * @param <T> t
 * @author xt
 */
public class WebMvcResultExpand<T> extends ResultExpand<T> {

    private static final long serialVersionUID = 1L;

    /**
     * 通用属性填充
     * @return 响应对象
     */
    public static WebMvcResultExpand<Object> wrap() {
        WebMvcResultExpand<Object> originalResult = new WebMvcResultExpand<>();
        ResultVO<Object> wrap = ResultUtils.wrap(originalResult);
        return (WebMvcResultExpand<Object>) wrap;
    }

    /**
     * 已识别的错误
     * @param httpStatus 错误http状态码
     * @param message    消息
     * @param e          异常
     * @return 响应
     */
    public ResultVO<T> warn(HttpStatus httpStatus, String message, @Nullable Throwable e) {
        return ResultUtils.warn(this, StatusCode.DEFAULT_ERROR.getCode() + httpStatus.value(), message, e);
    }

    /**
     * 已识别的错误
     * @param httpCode 错误http状态码
     * @param message  消息
     * @param e        异常
     * @return 响应
     */
    public ResultVO<T> warn(int httpCode, String message, @Nullable Throwable e) {
        return ResultUtils.warn(this, StatusCode.DEFAULT_ERROR.getCode() + httpCode, message, e);
    }

    /**
     * 已识别的错误
     * @param httpStatus 错误http状态码
     * @return 响应
     */
    public ResultVO<T> warn(HttpStatus httpStatus) {
        return ResultUtils.warn(this, StatusCode.DEFAULT_ERROR.getCode() + httpStatus.value(), httpStatus.getReasonPhrase(), null);
    }

}

package gaarason.convention.starter.webflux.pojo;

import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.util.ResultUtils;
import gaarason.convention.common.web.pojo.ResultExpand;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

/**
 * @param <T>
 * @author xt
 */
public class WebFluxResultExpand<T> extends ResultExpand<T> {

    private static final long serialVersionUID = 1L;

    /**
     * 通用属性填充
     * @return 响应对象
     */
    public static WebFluxResultExpand<Object> wrap() {
        WebFluxResultExpand<Object> originalResult = new WebFluxResultExpand<>();
        ResultVO<Object> wrap = ResultUtils.wrap(originalResult);
        return (WebFluxResultExpand<Object>) wrap;
    }

    /**
     * 先使用serverWebExchange中的属性补偿, 通用属性填充
     * @param serverWebExchange 请求
     * @return 响应对象
     */
    public static WebFluxResultExpand<Object> wrap(ServerWebExchange serverWebExchange) {
        ChainProvider.initMDC(serverWebExchange.getAttributes());
        return WebFluxResultExpand.wrap();
    }

    /**
     * 已识别的错误
     * @param httpStatus 错误http状态码
     * @param message    消息
     * @param e          异常
     * @return 响应
     */
    public ResultVO<T> warn(HttpStatus httpStatus, String message, Throwable e) {
        return ResultUtils.warn(this, StatusCode.DEFAULT_ERROR.getCode() + httpStatus.value(), message, e);
    }

    /**
     * 已识别的错误
     * @param httpStatus 错误http状态码
     * @param message    消息
     * @param e          异常
     * @return 响应
     */
    public ResultVO<T> warn(HttpResponseStatus httpStatus, String message, Throwable e) {
        return ResultUtils.warn(this, StatusCode.DEFAULT_ERROR.getCode() + httpStatus.code(), httpStatus.reasonPhrase() + ", " + message, e);
    }
}

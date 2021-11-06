package gaarason.convention.starter.webmvc.response;

import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.util.SpringUtils;
import gaarason.convention.common.web.util.ResponseUtils;
import gaarason.convention.starter.webmvc.pojo.WebMvcResultExpand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Optional;

/**
 * 统一响应体处理器
 * 方法(正常响应就是controller, 异常就是全局异常处理器)响应签名类型 -> 可用的HttpMessageConverter -> 根据 http 中的 Accept 确定可用的 HttpMessageConverter -> 开始执行
 * @author xt
 */
@RestControllerAdvice
public class WebMvcGlobalResponseEncapsulationAndLogging implements ResponseBodyAdvice<Object> {

    private static final Logger LOGGER = LogManager.getLogger(WebMvcGlobalResponseEncapsulationAndLogging.class);

    protected ConventionProperties conventionProperties;

    public WebMvcGlobalResponseEncapsulationAndLogging(final ConventionProperties properties) {
        conventionProperties = properties;
    }

    /**
     * 记录日志并返回
     * @param body 响应体
     * @return 响应体
     */
    @Nullable
    private static Object returnAndLog(@Nullable final Object body) {
        return ResponseUtils.responseAndLog(body);
    }

    @Override
    public boolean supports(final MethodParameter returnType, final Class converterType) {
        // 下面的方法中记录的响应日志, 这里需要无脑进入.
        return true;
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(@Nullable final Object body, final MethodParameter returnType, final MediaType selectedContentType,
        final Class selectedConverterType, final ServerHttpRequest request, final ServerHttpResponse response) {

        // 不使用全局响应, 仅记录日志
        if (!SpringUtils.isReturnTypeSupport(returnType, conventionProperties)) {
            WebMvcGlobalResponseEncapsulationAndLogging.LOGGER.debug("Http service sending response, not use global response.");
            return WebMvcGlobalResponseEncapsulationAndLogging.returnAndLog(body);
        }

        final ServletServerHttpResponse responseTemp = (ServletServerHttpResponse) response;
        final int status = responseTemp.getServletResponse().getStatus();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().set("Character-Encoding", "utf-8");

        // body 可能已经被全局异常处理过, 已经为 ResultVO
        final ResultVO<Object> resultVO;
        final int anInt = 200;
        if (status != anInt) {
            final HttpStatus httpStatus = Optional.ofNullable(HttpStatus.resolve(status)).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
            resultVO = WebMvcResultExpand.wrap().warn(httpStatus);
        } else {
            resultVO = WebMvcResultExpand.wrap().success(body);
        }

        return WebMvcGlobalResponseEncapsulationAndLogging.returnAndLog(resultVO);
    }

}

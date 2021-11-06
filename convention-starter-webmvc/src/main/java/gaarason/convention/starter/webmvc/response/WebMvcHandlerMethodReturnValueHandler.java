package gaarason.convention.starter.webmvc.response;

import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.util.SpringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

/**
 * 1. 全局默认仅返回 json
 * 2. http响应头中的content-type 依然遵循请求头中的 accept
 * 注意: 此处不会更改响应的内容
 * @author xt
 */
public class WebMvcHandlerMethodReturnValueHandler extends RequestResponseBodyMethodProcessor implements HandlerMethodReturnValueHandler {

    protected ConventionProperties conventionProperties;

    public WebMvcHandlerMethodReturnValueHandler(final List<HttpMessageConverter<?>> converters,
        @Nullable final List<Object> requestResponseBodyAdvice,
        final ConventionProperties properties) {
        super(converters, requestResponseBodyAdvice);
        conventionProperties = properties;
    }

    @Override
    public boolean supportsReturnType(@NotNull final MethodParameter returnType) {
        return SpringUtils.isReturnTypeSupport(returnType, conventionProperties);
    }
}

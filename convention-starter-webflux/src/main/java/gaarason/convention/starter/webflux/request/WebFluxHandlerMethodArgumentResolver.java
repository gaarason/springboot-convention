package gaarason.convention.starter.webflux.request;

import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.util.SpringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author xt
 */
public class WebFluxHandlerMethodArgumentResolver extends AbstractWebfluxCustomizeResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOGGER = LogManager.getLogger(WebFluxHandlerMethodArgumentResolver.class);

    protected final ConventionProperties conventionProperties;

    public WebFluxHandlerMethodArgumentResolver(CodecConfigurer codecConfigurer, ReactiveAdapterRegistry registry,
        ConventionProperties conventionProperties) {
        super(codecConfigurer, registry);
        this.conventionProperties = conventionProperties;
    }

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        boolean resolveArgumentSupport = SpringUtils.isResolveArgumentSupport(parameter, conventionProperties);
        LOGGER.debug("Using unified request ? {}", resolveArgumentSupport);
        return resolveArgumentSupport;
    }

    /**
     * Resolve the value for the method parameter.
     * @param parameter the method parameter
     * @param context   the binding context to use
     * @param exchange  the current exchange
     * @return {@code Mono} for the argument value, possibly empty
     */
    @Override
    @NotNull
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter, @NotNull BindingContext context, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        MediaType contentType = request.getHeaders().getContentType();
        MediaType mediaType = (contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM);

        // get 请求无视 Content-Type
        if (HttpMethod.GET.equals(exchange.getRequest().getMethod())) {
            return webFluxModelAttributeMethodArgumentResolver.resolveArgument(parameter, context, exchange);
        }

        // json 赋值与验证
        // 支持下划线转驼峰, 依靠 jackson 相关注解
        if (mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            return webFluxRequestBodyMethodArgumentResolver.resolveArgument(parameter, context, exchange);
        }

        // query && form-data && x-www-form-urlencoded 赋值与验证
        // 支持下划线转驼峰, 不依靠注解
        return webFluxModelAttributeMethodArgumentResolver.resolveArgument(parameter, context, exchange);

    }
}

package gaarason.convention.starter.webflux.request;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.annotation.RequestBodyMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 不要进入自动加载
 * @author xt
 */
public class WebFluxRequestBodyMethodArgumentResolver extends RequestBodyMethodArgumentResolver {

    public WebFluxRequestBodyMethodArgumentResolver(List<HttpMessageReader<?>> readers, ReactiveAdapterRegistry registry) {
        super(readers, registry);
    }

    @NotNull
    @Override
    public Mono<Object> resolveArgument(@NotNull MethodParameter param, @NotNull BindingContext bindingContext, @NotNull ServerWebExchange exchange) {
        return readBody(param, true, bindingContext, exchange);
    }
}

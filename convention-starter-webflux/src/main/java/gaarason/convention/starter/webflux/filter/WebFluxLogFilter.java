package gaarason.convention.starter.webflux.filter;

import gaarason.convention.starter.webflux.util.WebFluxRequestUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 这个filter仅用来记录请求体
 * @author xt
 */
public class WebFluxLogFilter implements WebFilter, Ordered {

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, WebFilterChain chain) {
        // http 入口日志, 设置上下文信信息
        WebFluxRequestUtils.printHttpProviderReceivedRequestLog(exchange);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}

package gaarason.convention.starter.webflux.filter;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.provider.LogProvider;
import gaarason.convention.common.util.HttpUtils;
import gaarason.convention.starter.webflux.util.WebFluxRequestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 这个filter用来记录请求体, 响应体
 * @author xt
 */
public class GatewayLogFilter implements WebFilter, Ordered {

    private static final Logger LOGGER = LogManager.getLogger(GatewayLogFilter.class);

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, WebFilterChain chain) {
        // http 入口日志, 设置上下文信信息
        WebFluxRequestUtils.printHttpProviderReceivedRequestLog(exchange);

        return chain.filter(new PayloadServerWebExchangeDecorator(exchange));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static class PayloadServerWebExchangeDecorator extends ServerWebExchangeDecorator {

        private final ServerHttpResponseDecorator responseDecorator;

        public PayloadServerWebExchangeDecorator(ServerWebExchange delegate) {
            super(delegate);
            responseDecorator = new RecorderServerHttpResponseDecorator(delegate);
        }

        @NotNull
        @Override
        public ServerHttpResponse getResponse() {
            return responseDecorator;
        }
    }

    public static class RecorderServerHttpResponseDecorator extends ServerHttpResponseDecorator {

        @Nullable
        private byte[] bodyBytes;

        /**
         * server web exchange
         */
        private final ServerWebExchange exchange;

        public RecorderServerHttpResponseDecorator(ServerWebExchange exchange) {
            super(exchange.getResponse());
            this.exchange = exchange;
        }

        @NotNull
        @Override
        public Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {

            LogProvider logProvider = LogProvider.getInstance();

            // 记录响应体
            if (logProvider.isLogHttpProviderSendingResponse()) {
                return Flux.from(body).collectList().filter(list -> !list.isEmpty()).map(list -> list.get(0).factory().join(list))
                    .doOnDiscard(PooledDataBuffer.class, DataBufferUtils::release).flatMap(db -> {
                        try {
                            bodyBytes = new byte[db.readableByteCount()];
                            db.read(bodyBytes);
                            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(bodyBytes);
                            return getDelegate().writeWith(Mono.just(dataBuffer));
                        } finally {
                            DataBufferUtils.release(db);
                        }
                    }).doFinally(s -> recordTheBody());
            } else {
                return getDelegate().writeWith(body);
            }
        }

        /**
         * 将body转化为可以记录的字符串
         * @return body字符串
         */
        public String getBodyAsString() {
            if (bodyBytes == null || bodyBytes.length < 1) {
                return FinalVariable.EMPTY_STRING;
            }
            try {
                return HttpUtils.gzipDecode(bodyBytes, exchange.getResponse().getHeaders());
            } catch (RuntimeException e) {
                GatewayLogFilter.LOGGER.error("get body error", e);
            }
            return FinalVariable.EMPTY_STRING;
        }

        /**
         * 记录响应体
         */
        protected void recordTheBody() {
            LogProvider.getInstance().printHttpProviderSendingResponseLog(this::getBodyAsString);
        }

    }

}

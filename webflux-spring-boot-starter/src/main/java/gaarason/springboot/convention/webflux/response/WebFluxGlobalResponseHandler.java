package gaarason.springboot.convention.webflux.response;

import gaarason.springboot.convention.common.pojo.ResultVO;
import gaarason.springboot.convention.common.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

/**
 * WebFlux 统一响应
 */
@Slf4j
public class WebFluxGlobalResponseHandler extends ResponseBodyResultHandler {

    private static final ResultVO<?> SUCCESS_RESULT = new ResultVO<>();

    private static final MethodParameter METHOD_PARAMETER_MONO_COMMON_RESULT;

    static {
        try {
            // 获得 METHOD_PARAMETER_MONO_COMMON_RESULT 。其中 -1 表示 `#methodForParams()` 方法的返回值
            METHOD_PARAMETER_MONO_COMMON_RESULT = new MethodParameter(
                WebFluxGlobalResponseHandler.class.getDeclaredMethod("methodForParams"), -1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    public WebFluxGlobalResponseHandler(List<HttpMessageWriter<?>> writers,
                                        RequestedContentTypeResolver resolver) {
        super(writers, resolver);
    }

    public WebFluxGlobalResponseHandler(List<HttpMessageWriter<?>> writers,
                                        RequestedContentTypeResolver resolver,
                                        ReactiveAdapterRegistry registry) {
        super(writers, resolver, registry);
    }


    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public Mono<Void> handleResult(@NonNull ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();
        Object body;
        // <1.1>  处理返回结果为 Mono 的情况
        if (returnValue instanceof Mono) {
            body = ((Mono<Object>) returnValue)
                .map((Function<Object, Object>) ResultUtils::success)
                .defaultIfEmpty(SUCCESS_RESULT);
            //  <1.2> 处理返回结果为 Flux 的情况
        } else if (returnValue instanceof Flux) {
            body = ((Flux<Object>) returnValue)
                .collectList()
                .map((Function<Object, Object>) ResultUtils::success)
                .defaultIfEmpty(SUCCESS_RESULT);
            //  <1.3> 处理结果为其它类型
        } else {
            body = ResultUtils.success(returnValue);
        }
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_STREAM_JSON);
        return writeBody(body, METHOD_PARAMETER_MONO_COMMON_RESULT, exchange);
    }

    private static Mono<ResultVO<?>> methodForParams() {
        return null;
    }

}
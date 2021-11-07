package gaarason.convention.starter.webflux.response;

import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.util.SpringUtils;
import gaarason.convention.common.web.util.ResponseUtils;
import gaarason.convention.starter.webflux.pojo.WebFluxResultExpand;
import gaarason.convention.starter.webflux.support.JsonHttpMessageWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * WebFlux 统一响应
 * @author xt
 */
public class WebFluxGlobalResponseHandler extends ResponseBodyResultHandler implements HandlerResultHandler {

    private static final Logger LOGGER = LogManager.getLogger(WebFluxGlobalResponseHandler.class);

    private static final ResultVO<?> SUCCESS_RESULT = new ResultVO<>();

    private static final MethodParameter METHOD_PARAMETER_MONO_COMMON_RESULT;

    @Resource
    private ConventionProperties conventionProperties;

    static {
        try {
            // 获得 METHOD_PARAMETER_MONO_COMMON_RESULT 。其中 -1 表示 `#methodForParams()` 方法的返回值
            METHOD_PARAMETER_MONO_COMMON_RESULT = new MethodParameter(WebFluxGlobalResponseHandler.class.getDeclaredMethod("methodForParams"), -1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public WebFluxGlobalResponseHandler(ServerCodecConfigurer serverCodecConfigurer, RequestedContentTypeResolver requestedContentTypeResolver) {
        this(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
    }

    public WebFluxGlobalResponseHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
        super(writers, resolver);
        // todo check
        writers.add(0, new JsonHttpMessageWriter());
    }

    @Override
    public boolean supports(HandlerResult result) {
        MethodParameter returnType = result.getReturnTypeSource();
        return SpringUtils.isReturnTypeSupport(returnType, conventionProperties);
    }

    /**
     * 比默认的优先即可
     * @return 排序
     */
    @Override
    public int getOrder() {
        return -999;
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public Mono<Void> handleResult(@NonNull ServerWebExchange exchange, HandlerResult result) {
        // 补偿
        ChainProvider.initMDC(exchange.getAttributes());

        Object returnValue = result.getReturnValue();

        // 使用 spring 包装过的,不再处理
        // todo
        if (returnValue instanceof ResponseEntity) {
            WebFluxGlobalResponseHandler.LOGGER.debug("Http service sending response, <org.springframework.http.ResponseEntity>");
            return returnAndLog(((ResponseEntity<?>) returnValue).getBody(), exchange, result, returnValue);
        }

        Object body;
        // <1.1> 处理返回结果为 Mono 的情况
        if (returnValue instanceof Mono) {
            body = ((Mono<Object>) returnValue).map(v -> WebFluxResultExpand.wrap().success(v))
                .defaultIfEmpty(WebFluxResultExpand.wrap().success(WebFluxGlobalResponseHandler.SUCCESS_RESULT));
            // <1.2> 处理返回结果为 Flux 的情况
        } else if (returnValue instanceof Flux) {
            body = ((Flux<Object>) returnValue).collectList().map(v -> WebFluxResultExpand.wrap().success(v))
                .defaultIfEmpty(WebFluxResultExpand.wrap().success(WebFluxGlobalResponseHandler.SUCCESS_RESULT));
            // <1.3> 处理结果为其它类型
        } else {
            body = WebFluxResultExpand.wrap().success(returnValue);
        }
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 响应日志
        return returnAndLog(body, exchange, result, returnValue);
    }

    @Nullable
    private static Mono<ResultVO<?>> methodForParams() {
        return null;
    }

    /**
     * 记录日志并返回
     * @param body        响应体
     * @param exchange    请求
     * @param result      声明的响应
     * @param returnValue 实际的响应
     * @return void
     */
    private Mono<Void> returnAndLog(@Nullable Object body, ServerWebExchange exchange, HandlerResult result,
        @Nullable Object returnValue) {
        // 响应日志
        Object res = ResponseUtils.responseAndLog(body);
        exchange.getResponse().getHeaders().set("Character-Encoding", "utf-8");

        MethodParameter bodyTypeParameter =
            returnValue instanceof ResponseEntity ? result.getReturnTypeSource() : WebFluxGlobalResponseHandler.METHOD_PARAMETER_MONO_COMMON_RESULT;

        return writeBody(res, bodyTypeParameter, exchange);
    }

}

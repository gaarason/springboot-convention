package gaarason.convention.starter.webflux.response;

import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.provider.LogProvider;
import gaarason.convention.common.web.util.ResponseUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

/**
 * WebFlux 统一响应日志记录
 * 当不使用统一响应时, 此处生效, 仅记录日志, 再由spring原本的HandlerResultHandler处理
 * @author xt
 */
public class WebFluxGlobalResponseLogHandler implements HandlerResultHandler, Ordered {

    @Resource
    private LogProvider logProvider;

    private final List<HandlerResultHandler> handlerResultHandlers;

    public WebFluxGlobalResponseLogHandler(List<HandlerResultHandler> handlers) {
        handlerResultHandlers = handlers;
    }

    @Override
    public boolean supports(@NotNull HandlerResult result) {
        // 下面的方法中记录的响应日志, 这里需要无脑进入.
        return true;
    }

    /**
     * 比默认的优先即可
     * @return 排序
     */
    @Override
    public int getOrder() {
        return -888;
    }

    @Override
    @NonNull
    public Mono<Void> handleResult(@NonNull ServerWebExchange exchange, HandlerResult result) {
        // 补偿
        ChainProvider.initMDC(exchange.getAttributes());

        Object returnValue = result.getReturnValue();

        // 响应日志
        Object res = ResponseUtils.responseAndLog(returnValue);

        HandlerResult handlerResult = new HandlerResult(result.getHandler(), res, result.getReturnTypeSource(), result.getBindingContext());

        for (HandlerResultHandler handler : handlerResultHandlers) {
            if (handler.supports(result)) {
                return handler.handleResult(exchange, handlerResult);
            }

        }

        throw new BusinessException(StatusCode.RESPONSE_TYPE_SUPPORTED);
    }
}

package gaarason.convention.starter.webflux.error;

import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.util.StringUtils;
import gaarason.convention.common.web.util.ResponseUtils;
import gaarason.convention.starter.webflux.util.WebFluxExceptionUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

/**
 * WebFlux 全局异常
 * @author xt
 */
@Order(-10000)
public class WebFluxErrorAttributes extends DefaultErrorAttributes {

    /**
     * 获取错误信息
     * @param request 请求
     * @param options 参数
     * @return 错误信息
     */
    @Override
    public Map<String, Object> getErrorAttributes(final ServerRequest request, final ErrorAttributeOptions options) {
        // 补偿
        ChainProvider.initMDC(request.attributes());

        final Throwable e = getError(request);

        // 异常转响应对象
        final ResultVO<?> resultVO = WebFluxExceptionUtils.exceptionHandler(e);

        // 用户响应转化处理, 并记录日志
        final Object res = ResponseUtils.responseAndLog(resultVO);

        // 只有错误会走到这里,且错误响应里面的元素都是平铺的, 没有多级嵌套, 所有下面的逻辑可以简单处理
        // todo
        return ObjectUtils.obj2Map(res, StringUtils::humpToLine);
    }
}

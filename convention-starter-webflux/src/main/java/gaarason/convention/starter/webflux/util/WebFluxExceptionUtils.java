package gaarason.convention.starter.webflux.util;

import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.util.SpringUtils;
import gaarason.convention.common.web.contract.ExceptionHandlerContract;
import gaarason.convention.starter.webflux.pojo.WebFluxResultExpand;
import io.netty.handler.codec.TooLongFrameException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author xt
 */
public class WebFluxExceptionUtils {

    /**
     * 异常处理器
     * 含 ExceptionHandlerContract 异常转化
     * @param throwable 异常
     * @return 响应
     */
    public static ResultVO<?> exceptionHandler(Throwable throwable) {

        // 获取自定义对象
        ExceptionHandlerContract exceptionHandlerContract = SpringUtils.getBean("exceptionHandlerContract");
        // 执行转化
        Throwable e = exceptionHandlerContract.conversion(throwable);
        ResultVO<Object> resultVO;
        // 对于自定义异常的处理
        if (e instanceof BusinessException) {
            resultVO = WebFluxResultExpand.wrap().warn((BusinessException) e);
        } else if (e instanceof TooLongFrameException) {
            resultVO = WebFluxResultExpand.wrap().warn(StatusCode.REQUEST_ENTITY_TOO_LARGE, e.getMessage(), e);
        } else if (e instanceof WebExchangeBindException) {
            // 参数认证异常
            WebExchangeBindException ex = (WebExchangeBindException) e;
            String defaultMessage = ex.getAllErrors().get(0).getDefaultMessage();
            resultVO = WebFluxResultExpand.wrap().warn(StatusCode.PARAMETER_VALIDATION_ERROR, defaultMessage, ex);
        } else if (e instanceof ResponseStatusException) {
            // HTTP异常
            ResponseStatusException ex = (ResponseStatusException) e;
            String message = ex.getMessage();
            resultVO = WebFluxResultExpand.wrap().warn(ex.getStatus(), ObjectUtils.nullSafeToString(message), ex);
        } else {
            // 其他
            resultVO = WebFluxResultExpand.wrap().error(e);
        }
        return resultVO;
    }
}

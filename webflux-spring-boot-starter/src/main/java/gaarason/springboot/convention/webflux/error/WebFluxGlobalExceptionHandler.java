package gaarason.springboot.convention.webflux.error;

import gaarason.springboot.convention.common.exception.BusinessException;
import gaarason.springboot.convention.common.pojo.ResultVO;
import gaarason.springboot.convention.common.pojo.StatusCode;
import gaarason.springboot.convention.common.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * WebFlux 全局异常
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class WebFluxGlobalExceptionHandler {

    /**
     * 异常处理器
     * @param e 异常
     * @return 响应
     */
    @ExceptionHandler(value = Throwable.class) //该注解声明异常处理方法
    public static ResultVO<?> exceptionHandler(Throwable e) {
        // 对于自定义异常的处理
        if (e instanceof BusinessException) {
            BusinessException ex = (BusinessException) e;
            return ResultUtils.warn(ex);
        }
        // 参数认证异常
        else if (e instanceof WebExchangeBindException) {
            WebExchangeBindException ex = (WebExchangeBindException) e;
            String defaultMessage = ex.getAllErrors().get(0).getDefaultMessage();
            return ResultUtils.warn(StatusCode.HTTP_REQUEST_VALIDATION_ERROR, defaultMessage, ex);
        }
        // 其他
        else {
            return ResultUtils.error(e);
        }
    }

}
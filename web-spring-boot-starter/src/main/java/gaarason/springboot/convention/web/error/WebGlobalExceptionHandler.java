package gaarason.springboot.convention.web.error;

import gaarason.springboot.convention.common.exception.BusinessException;
import gaarason.springboot.convention.common.pojo.ResultVO;
import gaarason.springboot.convention.common.pojo.StatusCode;
import gaarason.springboot.convention.common.util.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常
 */
@RestControllerAdvice
public class WebGlobalExceptionHandler {
    /**
     * 异常处理器
     * @param request 请求上下文
     * @param e       异常
     * @return 响应
     */
    @ExceptionHandler(value = Throwable.class)
    public ResultVO<?> exceptionHandler(HttpServletRequest request, Exception e) {
        // 对于自定义异常的处理
        if (e instanceof BusinessException) {
            BusinessException ex = (BusinessException) e;
            return ResultUtils.warn(ex);
        }
        // 对于绑定异常的处理，使用jsr303中的自定义注解抛出的异常属于绑定异常
        else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return ResultUtils.warn(StatusCode.HTTP_REQUEST_VALIDATION_ERROR, msg, e);
        }
        // 对于绑定异常的处理，使用jsr303中的自定义注解抛出的异常属于绑定异常
        else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            List<ObjectError> errors = ex.getBindingResult().getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return ResultUtils.warn(StatusCode.HTTP_REQUEST_VALIDATION_ERROR, msg, e);
        }
        // 必要参数不能正确反序列化
        else if (e instanceof HttpMessageNotReadableException) {
            return ResultUtils.warn(StatusCode.HTTP_MESSAGE_NOT_READABLE, e.getMessage(), e);
        }
        // http方法错误
        else if (e instanceof HttpRequestMethodNotSupportedException) {
            return ResultUtils.warn(StatusCode.HTTP_METHOD_ERROR, e.getMessage(), e);
        }
        // 其他
        else {
            return ResultUtils.error(e);
        }
    }
}
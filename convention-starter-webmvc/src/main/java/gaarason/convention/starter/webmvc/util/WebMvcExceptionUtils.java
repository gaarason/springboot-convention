package gaarason.convention.starter.webmvc.util;

import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.util.SpringUtils;
import gaarason.convention.common.web.contract.ExceptionHandlerContract;
import gaarason.convention.starter.webmvc.pojo.WebMvcResultExpand;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.io.Serializable;
import java.util.List;

/**
 * @author xt
 */
public class WebMvcExceptionUtils {

    /**
     * 异常处理
     * 含 ExceptionHandlerContract 异常转化
     * @param throwable 错误
     * @return 响应对象
     */
    public static ResultVO<?> exceptionHandler(Throwable throwable) {
        // 获取自定义对象
        ExceptionHandlerContract exceptionHandlerContract = SpringUtils.getBean("exceptionHandlerContract");
        // 执行转化
        Throwable e = exceptionHandlerContract.conversion(throwable);
        ResultVO<?> resultVO;
        // 对于自定义异常的处理
        if (e instanceof BusinessException) {
            resultVO = WebMvcResultExpand.wrap().warn((BusinessException) e);
        } else if (e instanceof BindException) {
            // todo
            // 对于绑定异常的处理，使用jsr303中的自定义注解抛出的异常属于绑定异常
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            resultVO = WebMvcResultExpand.wrap().warn(StatusCode.PARAMETER_VALIDATION_ERROR, msg, e);
        } else if (e instanceof MissingServletRequestParameterException) {
            // 必要参数不存在
            resultVO = WebMvcResultExpand.wrap().warn(StatusCode.REQUIRED_PARAMETER_DOES_NOT_EXIST, e.getMessage(), e);
        } else if (e instanceof HttpMessageNotReadableException) {
            // 必要参数不能正确反序列化
            resultVO = WebMvcResultExpand.wrap().warn(StatusCode.PARAMETER_NOT_READABLE, e.getMessage(), e);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            // http方法错误
            resultVO = WebMvcResultExpand.wrap().warn(StatusCode.HTTP_METHOD_ERROR, e.getMessage(), e);
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            // http参数类型错误
            resultVO = WebMvcResultExpand.wrap().warn(StatusCode.PARAMETER_MEDIA_TYPE_ERROR, e.getMessage(), e);
        } else {
            // 其他
            resultVO = WebMvcResultExpand.wrap().error(e);
        }
        return resultVO;
    }
}

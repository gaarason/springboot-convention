package gaarason.convention.starter.webmvc.error;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.annotation.web.ExcludeUnifiedResponse;
import gaarason.convention.starter.webmvc.util.WebMvcExceptionUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常
 * @author xt
 */
@RestControllerAdvice
public class WebMvcGlobalExceptionHandler {

    /**
     * 异常处理器
     * @param request   请求上下文
     * @param exception 异常
     * @return 响应
     */
    @ExceptionHandler(value = Throwable.class)
    @ExcludeUnifiedResponse
    public ResponseEntity<?> exceptionHandler(final HttpServletRequest request, final Exception exception) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).header(FinalVariable.Http.CONVENTION_MARK_UNIFIED_RESPONSE, "1")
            .header(FinalVariable.Http.CHARACTER_ENCODING, "utf-8").body(WebMvcExceptionUtils.exceptionHandler(exception));
    }
}

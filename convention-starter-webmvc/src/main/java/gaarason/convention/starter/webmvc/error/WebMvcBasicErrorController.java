package gaarason.convention.starter.webmvc.error;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.annotation.web.ExcludeUnifiedResponse;
import gaarason.convention.starter.webmvc.pojo.WebMvcResultExpand;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 错误控制器
 * @author xt
 */
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class WebMvcBasicErrorController extends AbstractErrorController {

    public WebMvcBasicErrorController(final ErrorAttributes errorAttributes, final ErrorProperties errorProperties,
        final List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        Assert.notNull(errorProperties, "ErrorProperties must not be null");
    }

    @Override
    @Nullable
    public String getErrorPath() {
        return null;
    }

    @RequestMapping
    @ExcludeUnifiedResponse
    public ResponseEntity<Object> error(final HttpServletRequest request) {
        // 组装响应内容
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).header(FinalVariable.Http.CONVENTION_MARK_UNIFIED_RESPONSE, "1")
            .header(FinalVariable.Http.CHARACTER_ENCODING, "utf-8").body(WebMvcResultExpand.wrap().warn(getStatus(request)));
    }
}

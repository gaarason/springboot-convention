package gaarason.convention.starter.webmvc.request;

import gaarason.convention.starter.webmvc.request.binder.WebMvcServletRequestDataBinder;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.ServletRequest;

/**
 * @author xt
 */
public class WebMvcServletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

    /**
     * Class constructor.
     * @param annotationNotRequired if "true", non-simple method arguments and
     *                              return values are considered model attributes with or without a
     *                              {@code @ModelAttribute} annotation
     */
    public WebMvcServletModelAttributeMethodProcessor(final boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    /**
     * Extension point to bind the request to the target object.
     * @param binder  the data binder instance to use for the binding
     * @param request the current request
     */
    @Override
    protected void bindRequestParameters(final WebDataBinder binder, @NotNull final NativeWebRequest request) {
        final ServletRequest servletRequest = request.getNativeRequest(ServletRequest.class);
        final Object target = binder.getTarget();
        Assert.state(servletRequest != null, "No ServletRequest");
        Assert.state(target != null, "No binder.getTarget()");
        new WebMvcServletRequestDataBinder(target, binder.getObjectName()).bind(servletRequest);
    }
}

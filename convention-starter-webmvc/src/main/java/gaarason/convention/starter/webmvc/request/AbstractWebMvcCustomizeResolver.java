package gaarason.convention.starter.webmvc.request;

import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * @author xt
 */
public abstract class AbstractWebMvcCustomizeResolver implements HandlerMethodArgumentResolver {

    protected final RequestResponseBodyMethodProcessor requestResponseBodyMethodProcessor;

    protected final WebMvcServletModelAttributeMethodProcessor webMvcServletModelAttributeMethodProcessor;

    protected AbstractWebMvcCustomizeResolver(List<HttpMessageConverter<?>> converters) {
        requestResponseBodyMethodProcessor = new RequestResponseBodyMethodProcessor(converters);
        webMvcServletModelAttributeMethodProcessor = new WebMvcServletModelAttributeMethodProcessor(true);
    }

    protected static void valid(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory, Object arg) throws Exception {
        String name = Conventions.getVariableNameForParameter(parameter);
        WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
        if (arg != null) {
            AbstractWebMvcCustomizeResolver.validateIfApplicable(binder, parameter);
            if (binder.getBindingResult().hasErrors() && AbstractWebMvcCustomizeResolver.isBindExceptionRequired(parameter)) {
                throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
            }
        }
        mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
    }

    /**
     * Validate the binding target if applicable.
     * <p>The default implementation checks for {@code @javax.validation.Valid},
     * Spring's {@link Validated},
     * and custom annotations whose name starts with "Valid".
     *
     * @param binder    the DataBinder to be used
     * @param parameter the method parameter descriptor
     * @see #isBindExceptionRequired
     * @since 4.1.5
     */
    protected static void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
            if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
                Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
                binder.validate(validationHints);
                break;
            }
        }
    }

    protected static boolean isBindExceptionRequired(MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = Objects.requireNonNull(parameter.getMethod()).getParameterTypes();
        return !(paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]));
    }
}

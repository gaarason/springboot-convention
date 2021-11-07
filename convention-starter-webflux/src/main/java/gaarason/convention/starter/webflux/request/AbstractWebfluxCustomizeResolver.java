package gaarason.convention.starter.webflux.request;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;

import java.lang.annotation.Annotation;

/**
 * @author xt
 */
public abstract class AbstractWebfluxCustomizeResolver implements HandlerMethodArgumentResolver {

    protected final ReactiveAdapterRegistry registry;

    protected final CodecConfigurer codecConfigurer;

    /**
     * json 等
     */
    protected final WebFluxRequestBodyMethodArgumentResolver webFluxRequestBodyMethodArgumentResolver;

    /**
     * form-data
     */
    protected final WebFluxModelAttributeMethodArgumentResolver webFluxModelAttributeMethodArgumentResolver;

    /**
     * 构造方法
     *
     * @param codecConfigurer 配置
     * @param registry        注册
     */
    public AbstractWebfluxCustomizeResolver(CodecConfigurer codecConfigurer, ReactiveAdapterRegistry registry) {
        this.codecConfigurer = codecConfigurer;
        this.registry = registry;
        webFluxRequestBodyMethodArgumentResolver = new WebFluxRequestBodyMethodArgumentResolver(codecConfigurer.getReaders(), registry);
        webFluxModelAttributeMethodArgumentResolver = new WebFluxModelAttributeMethodArgumentResolver(registry, false);
    }

    /**
     * 参数验证
     *
     * @param binder    参数binder
     * @param parameter 形参
     */
    public static void validateIfApplicable(WebExchangeDataBinder binder, MethodParameter parameter) {
        for (Annotation ann : parameter.getParameterAnnotations()) {
            Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
            if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
                if (hints != null) {
                    Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
                    binder.validate(validationHints);
                } else {
                    binder.validate();
                }
            }
        }
    }
}

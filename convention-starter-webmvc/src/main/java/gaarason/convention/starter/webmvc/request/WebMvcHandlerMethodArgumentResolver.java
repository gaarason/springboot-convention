package gaarason.convention.starter.webmvc.request;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.util.SpringUtils;
import org.apache.catalina.connector.RequestFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xt
 */
public class WebMvcHandlerMethodArgumentResolver extends AbstractWebMvcCustomizeResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOGGER = LogManager.getLogger(WebMvcHandlerMethodArgumentResolver.class);

    protected static final RequestBody ANNOTATION_REQUEST_BODY;

    protected final ConventionProperties conventionProperties;

    static {
        ANNOTATION_REQUEST_BODY = WebMvcHandlerMethodArgumentResolver.getAnnotationRequestBody(new Object());
    }

    public WebMvcHandlerMethodArgumentResolver(final List<HttpMessageConverter<?>> converters, final ConventionProperties conventionProperties) {
        super(converters);
        this.conventionProperties = conventionProperties;
    }

    /**
     * 为 methodParameter 增加 @RequestBody 注解
     * @param methodParameter 方法参数
     */
    private static void setAnnotationRequestBody(final MethodParameter methodParameter) {
        final List<Annotation> oldAnnotations = Arrays.asList(methodParameter.getParameterAnnotations());
        final List<Annotation> newAnnotations = new ArrayList<>(oldAnnotations);
        newAnnotations.add(WebMvcHandlerMethodArgumentResolver.ANNOTATION_REQUEST_BODY);

        final Annotation[] newAnnotationArray = newAnnotations.toArray(new Annotation[0]);

        final Class<? extends MethodParameter> clazz = methodParameter.getClass();
        try {
            final Field parameterAnnotations1 = clazz.getDeclaredField("combinedAnnotations");
            parameterAnnotations1.setAccessible(true);
            parameterAnnotations1.set(methodParameter, newAnnotationArray);
        } catch (final Throwable e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 仅用作获取 @RequestBody 注解
     * @param ignore 灭用的参数
     * @return @RequestBody
     */
    private static RequestBody getAnnotationRequestBody(@RequestBody final Object ignore) {
        try {
            final Parameter[] reqs = WebMvcHandlerMethodArgumentResolver.class.getDeclaredMethod("getAnnotationRequestBody",
                Object.class).getParameters();
            final Annotation[] annotations = reqs[0].getAnnotations();
            return (RequestBody) annotations[0];
        } catch (final Throwable e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 是否使用
     * @param parameter 方法形参
     * @return boolean
     */
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        final boolean resolveArgumentSupport = SpringUtils.isResolveArgumentSupport(parameter, conventionProperties);
        WebMvcHandlerMethodArgumentResolver.LOGGER.debug("Using unified request ? {}", resolveArgumentSupport);
        return resolveArgumentSupport;
    }

    /**
     * 装载参数
     * @param methodParameter       方法参数
     * @param modelAndViewContainer 返回视图容器
     * @param nativeWebRequest      本次请求对象
     * @param webDataBinderFactory  数据绑定工厂
     * @return the resolved argument value, or {@code null}
     * @throws Exception in case of errors with the preparation of argument values
     */
    @Override
    public Object resolveArgument(final MethodParameter methodParameter, @Nullable final ModelAndViewContainer modelAndViewContainer,
        final NativeWebRequest nativeWebRequest, @Nullable final WebDataBinderFactory webDataBinderFactory) throws Exception {

        final String contentTypeString = nativeWebRequest.getHeader("Content-Type");
        final MediaType contentType = contentTypeString != null ? MediaType.parseMediaType(contentTypeString) : MediaType.APPLICATION_OCTET_STREAM;

        final RequestFacade requestFacade = nativeWebRequest.getNativeRequest(RequestFacade.class);

        // get 请求无视 Content-Type
        if (requestFacade != null && FinalVariable.Http.Method.GET.name().equals(requestFacade.getMethod())) {

            return webMvcServletModelAttributeMethodProcessor.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest,
                webDataBinderFactory);
        }

        // json 赋值与验证
        // 支持下划线转驼峰, 依靠 jackson 相关注解
        if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            // 设置必传
            WebMvcHandlerMethodArgumentResolver.setAnnotationRequestBody(methodParameter);

            return requestResponseBodyMethodProcessor.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest, webDataBinderFactory);
        }

        return webMvcServletModelAttributeMethodProcessor.resolveArgument(methodParameter, modelAndViewContainer, nativeWebRequest,
            webDataBinderFactory);
    }
}

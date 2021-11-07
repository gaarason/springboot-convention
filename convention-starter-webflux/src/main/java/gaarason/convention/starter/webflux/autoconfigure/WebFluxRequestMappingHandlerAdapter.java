package gaarason.convention.starter.webflux.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.util.JsonUtils;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.starter.webflux.request.WebFluxHandlerMethodArgumentResolver;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author xt
 */
public class WebFluxRequestMappingHandlerAdapter implements InitializingBean {

    protected final RequestMappingHandlerAdapter adapter;

    protected CodecConfigurer codecConfigurer;

    protected ReactiveAdapterRegistry registry;

    protected ConventionProperties conventionProperties;

    public WebFluxRequestMappingHandlerAdapter(RequestMappingHandlerAdapter adapter, CodecConfigurer codecConfigurer,
        ReactiveAdapterRegistry registry,
        ConventionProperties conventionProperties) {
        this.adapter = adapter;
        this.codecConfigurer = codecConfigurer;
        this.registry = registry;
        this.conventionProperties = conventionProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 生成handler
        HandlerMethodArgumentResolver handler = WebFluxRequestMappingHandlerAdapter.generateHandler(codecConfigurer, registry, conventionProperties);
        // 获取对象
        Object methodResolver = WebFluxRequestMappingHandlerAdapter.getControllerMethodResolverInAdapter(adapter);
        // 增加到 RequestMappingResolvers 列表首位
        WebFluxRequestMappingHandlerAdapter.addFirstHandlerMethodArgumentResolverInRequestMappingResolvers(methodResolver, handler);
        // 增加到 ModelAttributeResolvers 列表首位
        WebFluxRequestMappingHandlerAdapter.addFirstHandlerMethodArgumentResolverInModelAttributeResolvers(methodResolver, handler);

    }

    /**
     * 返回 methodResolver
     * @return ControllerMethodResolver
     */
    protected static Object getControllerMethodResolverInAdapter(
        RequestMappingHandlerAdapter adapter) throws NoSuchFieldException, IllegalAccessException {
        Field fieldMethodResolver = RequestMappingHandlerAdapter.class.getDeclaredField("methodResolver");
        fieldMethodResolver.setAccessible(true);
        // 不是public的类, 使用Object返回
        return fieldMethodResolver.get(adapter);
    }

    /**
     * 增加 handler 到最高优先级
     * @param methodResolver ControllerMethodResolver
     * @param handler        HandlerMethodArgumentResolver
     */
    protected static void addFirstHandlerMethodArgumentResolverInModelAttributeResolvers(Object methodResolver, HandlerMethodArgumentResolver handler)
        throws NoSuchFieldException, IllegalAccessException {
        Field fieldRequestMappingResolvers = methodResolver.getClass().getDeclaredField("modelAttributeResolvers");
        fieldRequestMappingResolvers.setAccessible(true);
        List<HandlerMethodArgumentResolver> requestMappingResolvers = ObjectUtils.typeCast(fieldRequestMappingResolvers.get(methodResolver));
        requestMappingResolvers.add(0, handler);
    }

    /**
     * 增加 handler 到最高优先级
     * @param methodResolver ControllerMethodResolver
     * @param handler        HandlerMethodArgumentResolver
     */
    protected static void addFirstHandlerMethodArgumentResolverInRequestMappingResolvers(Object methodResolver, HandlerMethodArgumentResolver handler)
        throws NoSuchFieldException, IllegalAccessException {
        Field fieldRequestMappingResolvers = methodResolver.getClass().getDeclaredField("requestMappingResolvers");
        fieldRequestMappingResolvers.setAccessible(true);
        List<HandlerMethodArgumentResolver> requestMappingResolvers = ObjectUtils.typeCast(fieldRequestMappingResolvers.get(methodResolver));
        requestMappingResolvers.add(0, handler);
    }

    /**
     * 生生成自定义参数解析handler
     * @param codecConfigurer      CodecConfigurer
     * @param registry             ReactiveAdapterRegistry
     * @param conventionProperties ConventionProperties
     * @return 参数解析handler
     */
    protected static HandlerMethodArgumentResolver generateHandler(CodecConfigurer codecConfigurer, ReactiveAdapterRegistry registry,
        ConventionProperties conventionProperties) {
        CodecConfigurer configurer = codecConfigurer.clone();

        // 使用统一的 jackson 配置
        try {
            CodecConfigurer.DefaultCodecs defaultCodecs = configurer.defaultCodecs();
            ObjectMapper mapper = JsonUtils.getMapper();
            Class<?> clazz = defaultCodecs.getClass().getSuperclass();

            Field decoderField = clazz.getDeclaredField("jackson2JsonDecoder");
            decoderField.setAccessible(true);
            Jackson2JsonDecoder jackson2JsonDecoder = ObjectUtils.typeCast(decoderField.get(defaultCodecs));
            jackson2JsonDecoder.setObjectMapper(mapper);

            Field encoderField = clazz.getDeclaredField("jackson2JsonEncoder");
            encoderField.setAccessible(true);
            Jackson2JsonEncoder jackson2JsonEncoder = ObjectUtils.typeCast(encoderField.get(defaultCodecs));
            jackson2JsonEncoder.setObjectMapper(mapper);
        } catch (Throwable e) {
            throw new BusinessException(e);
        }

        return new WebFluxHandlerMethodArgumentResolver(configurer, registry, conventionProperties);
    }
}

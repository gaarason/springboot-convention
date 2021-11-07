package gaarason.convention.starter.webflux.autoconfigure;

import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.starter.webflux.error.WebFluxErrorAttributes;
import gaarason.convention.starter.webflux.error.WebFluxGlobalErrorWebExceptionHandler;
import gaarason.convention.starter.webflux.filter.GatewayLogFilter;
import gaarason.convention.starter.webflux.filter.WebFluxLogFilter;
import gaarason.convention.starter.webflux.response.WebFluxGlobalResponseHandler;
import gaarason.convention.starter.webflux.response.WebFluxGlobalResponseLogHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * @author xt
 * @since 2021/7/9 15:19
 */
@Configuration
public class WebFluxAutoConfiguration {

    /**
     * 非网关应用的入口日志
     */
    @Bean
    @ConditionalOnMissingBean(type = "org.springframework.cloud.gateway.config.GatewayAutoConfiguration")
    public WebFluxLogFilter webFluxLogFilter() {
        return new WebFluxLogFilter();
    }

    /**
     * 网关应用的入口日志
     */
    @Bean
    @ConditionalOnBean(type = "org.springframework.cloud.gateway.config.GatewayAutoConfiguration")
    public GatewayLogFilter gatewayLogFilter() {
        return new GatewayLogFilter();
    }

    /**
     * 全局异常
     */
    @Bean
    @ConditionalOnMissingBean
    public WebFluxErrorAttributes webFluxErrorAttributes() {
        return new WebFluxErrorAttributes();
    }

    @Bean
    @ConditionalOnMissingBean
    public WebFluxGlobalErrorWebExceptionHandler webFluxGlobalErrorWebExceptionHandler(WebFluxErrorAttributes errorAttributes,
        WebProperties.Resources resources, ApplicationContext applicationContext) {
        return new WebFluxGlobalErrorWebExceptionHandler(errorAttributes, resources, applicationContext);
    }

    /**
     * 统一参数解析
     */
    @Bean
    @ConditionalOnMissingBean
    public WebFluxRequestMappingHandlerAdapter webFluxRequestMappingHandlerAdapter(RequestMappingHandlerAdapter adapter,
        CodecConfigurer codecConfigurer,
        ReactiveAdapterRegistry registry, ConventionProperties conventionProperties) {
        return new WebFluxRequestMappingHandlerAdapter(adapter, codecConfigurer, registry, conventionProperties);
    }

    /**
     * 统一响应与响应日志记录
     */
    @Bean
    @ConditionalOnMissingBean
    public WebFluxGlobalResponseHandler webFluxGlobalResponseHandler(ServerCodecConfigurer serverCodecConfigurer,
        RequestedContentTypeResolver resolver) {
        return new WebFluxGlobalResponseHandler(serverCodecConfigurer, resolver);
    }

    /**
     * 不使用统一响应时的响应日志记录
     */
    @Bean
    @ConditionalOnMissingBean
    public WebFluxGlobalResponseLogHandler webFluxGlobalResponseLogHandler(List<HandlerResultHandler> handlerResultHandlers) {
        return new WebFluxGlobalResponseLogHandler(handlerResultHandlers);
    }
}

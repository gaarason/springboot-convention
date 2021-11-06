package gaarason.convention.starter.webmvc.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.util.JsonUtils;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.web.util.ResponseUtils;
import gaarason.convention.starter.webmvc.error.WebMvcBasicErrorController;
import gaarason.convention.starter.webmvc.error.WebMvcGlobalExceptionHandler;
import gaarason.convention.starter.webmvc.filter.WebMvcLogFilter;
import gaarason.convention.starter.webmvc.response.WebMvcGlobalResponseEncapsulationAndLogging;
import gaarason.convention.starter.webmvc.util.WebMvcExceptionUtils;
import gaarason.convention.starter.webmvc.util.WebMvcRequestUtils;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.Collectors;

/**
 * @author xt
 * @since 2021/7/9 15:19
 */
@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
public class WebMvcAutoConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(WebMvcAutoConfiguration.class);

    /**
     * 1. 将异常(Throwable)转化为统一响应对象(ResultVO)
     */
    @Bean
    @ConditionalOnMissingBean
    public WebMvcGlobalExceptionHandler webGlobalExceptionHandler() {
        return new WebMvcGlobalExceptionHandler();
    }

    /**
     * 1. 将控制器响应(Object)转化为统一响应对象(ResultVO)
     * 2. 记录响应日志
     */
    @Bean
    @ConditionalOnMissingBean
    public WebMvcGlobalResponseEncapsulationAndLogging webGlobalResponseEncapsulationAndLogging(final ConventionProperties conventionProperties) {
        return new WebMvcGlobalResponseEncapsulationAndLogging(conventionProperties);
    }

    /**
     * 设置全局参数解析, 设置全局统一响应
     * 1. 增加 WebReturnValueHandler 控制最终响应的content-type (直接在WebMvcConfigurerAdapter 中配置HandlerMethodReturnValueHandler，优先级过低)
     * 2. 自定义的参数解析
     */
    @Bean
    @ConditionalOnMissingBean
    public WebMvcRequestMappingHandlerAdapter webInitializingAdvice() {
        return new WebMvcRequestMappingHandlerAdapter();
    }

    /**
     * 1. 将异常(Html)转化为统一响应对象(ResultVO)
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass({Servlet.class, DispatcherServlet.class})
    @AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
    @EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class, WebMvcProperties.class})
    public static class WebErrorMvcAutoConfiguration {

        private final ServerProperties serverProperties;

        public WebErrorMvcAutoConfiguration(final ServerProperties serverProperties) {
            this.serverProperties = serverProperties;
        }

        @Bean
        @ConditionalOnMissingBean(value = {ErrorAttributes.class}, search = SearchStrategy.CURRENT)
        public DefaultErrorAttributes errorAttributes() {
            return new DefaultErrorAttributes();
        }

        @Bean
        @ConditionalOnMissingBean(value = {ErrorController.class}, search = SearchStrategy.CURRENT)
        public WebMvcBasicErrorController basicErrorController(final ErrorAttributes errorAttributes,
            final ObjectProvider<ErrorViewResolver> errorViewResolvers) {
            return new WebMvcBasicErrorController(errorAttributes, serverProperties.getError(),
                errorViewResolvers.orderedStream().collect(Collectors.toList()));
        }
    }

    /**
     * 网关应用的入口日志
     */
    @Bean
    @ConditionalOnMissingBean
    public WebMvcLogFilter gatewayLogFilter() {
        return new WebMvcLogFilter();
    }

    /**
     * 1. http入口日志
     * 2. Tomcat 异常捕获
     * 3. http请求结束后的, 线程清理
     */
    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter(ServletWebServerFactoryAutoConfiguration.class)
    public static class WebTomcatConfiguration {

        private final ObjectMapper mapper = JsonUtils.getMapper();

        @Bean
        public WebServerFactoryCustomizer<TomcatServletWebServerFactory> serverFactoryCustomizer() {
            return factory -> {
                factory.addConnectorCustomizers((Connector connector) -> {
                    // path上允许的特殊字符
//                    connector.setProperty("relaxedPathChars", "\"<>[\\]^`{}%");
                    // query上允许的特殊字符
                    connector.setProperty("relaxedQueryChars", "\"<>[\\]^`{}%");
                });
                factory.addContextCustomizers(context -> context.getParent().getPipeline().addValve(new ValveBase() {
                    @Override
                    public void invoke(final Request request, final Response response) throws IOException, ServletException {

                        /**
                         * 正常请求的入口日志移动到了, WebMvcLogFilter
                         */

                        // 错误响应
                        if (response.isError()) {

                            HttpServletRequest httpServletRequest = ObjectUtils.typeCast(request);

                            // 网路级别的错误请求时, 不会进入skywalking的监控范围, 所以skywalking不会有链路和日志, 需要手动搞定这个.
                            // http 入口日志, 设置上下文信信息
                            WebMvcRequestUtils.printHttpProviderReceivedRequestLog(httpServletRequest);

                            // 可能为null, 但方法签名没有体现
                            // 需要手动判断
                            final Object errorException = httpServletRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

                            // Bad Request 处理
                            final BusinessException businessException =
                                null == errorException ? new BusinessException(StatusCode.HTTP_CLIENT_BAD_REQUEST, response.getMessage())
                                    : new BusinessException(StatusCode.HTTP_CLIENT_BAD_REQUEST.getCode(), ((Throwable) errorException).getMessage(),
                                    (Throwable) errorException);

                            // 异常转化为响应对象
                            final ResultVO<?> resultVO = WebMvcExceptionUtils.exceptionHandler(businessException);

                            // 响应转化, 并记录响应日志
                            final Object obj = ResponseUtils.responseAndLog(resultVO);

                            final String resultString = mapper.writeValueAsString(obj);
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("utf-8");
                            response.setSuspended(false);
                            final Writer writer = response.getReporter();
                            if (writer != null) {
                                writer.write(resultString);
                                response.finishResponse();
                            } else {
                                WebMvcAutoConfiguration.LOGGER.error("writer is null, so can not send response.");
                            }
                        } else {
                            getNext().invoke(request, response);
                        }
                        // 此处手动清除下, 更加保险
                        ChainProvider.clear();
                    }
                }));
            };
        }
    }
}

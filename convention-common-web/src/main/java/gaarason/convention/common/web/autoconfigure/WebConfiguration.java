package gaarason.convention.common.web.autoconfigure;

import gaarason.convention.common.web.contract.ChainContextHandlerContract;
import gaarason.convention.common.web.contract.ExceptionHandlerContract;
import gaarason.convention.common.web.contract.ResponseHandlerContract;
import gaarason.convention.common.web.controller.BlankFaviconIcoController;
import gaarason.convention.common.web.controller.impl.BlankFaviconIcoControllerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义配置
 * @author xt
 */
@Configuration
public class WebConfiguration {

    /**
     * 自定义链路上下文配置
     */
    @Bean
    @ConditionalOnMissingBean
    public ChainContextHandlerContract chainContextHandlerContract() {
        return (request) -> {
        };
    }

    /**
     * 转化异常
     */
    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandlerContract exceptionHandlerContract() {
        return throwable -> throwable;
    }

    /**
     * 响应转化
     */
    @Bean
    @ConditionalOnMissingBean
    public ResponseHandlerContract responseHandlerContract() {
        return resultVO -> resultVO;
    }

    /**
     * 路由 /favicon.ico
     * 可以使用 ykj.http.generate-blank-favicon-ico = false 关闭
     */
    @Bean
    @ConditionalOnProperty(name = "ykj.http.generate-blank-favicon-ico", havingValue = "true", matchIfMissing = true)
    public BlankFaviconIcoController blankFaviconIcoController() {
        return new BlankFaviconIcoControllerImpl();
    }
}

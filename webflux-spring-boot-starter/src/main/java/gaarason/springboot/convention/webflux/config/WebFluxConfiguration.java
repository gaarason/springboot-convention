package gaarason.springboot.convention.webflux.config;

import gaarason.springboot.convention.webflux.response.WebFluxGlobalResponseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;

@Configuration
public class WebFluxConfiguration {

    /**
     * 统一响应
     */
    @Bean
    public WebFluxGlobalResponseHandler responseWrapper(ServerCodecConfigurer serverCodecConfigurer,
                                                        RequestedContentTypeResolver requestedContentTypeResolver) {
        return new WebFluxGlobalResponseHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
    }
}
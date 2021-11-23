package gaarason.convention.common.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.convention.common.provider.LogProvider;
import gaarason.convention.common.util.JsonUtils;
import gaarason.convention.common.util.SpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置
 * @author xt
 */
@Configuration
@EnableConfigurationProperties({ConventionProperties.class})
public class CommonAutoConfiguration implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 设置 spring 上下文
        SpringUtils.setApplicationContext(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public LogProvider logProvider() {
        return new LogProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return JsonUtils.getMapper();
    }
}

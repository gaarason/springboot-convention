package gaarason.springboot.convention.common.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.Locale;

public class SpringBootConfiguration implements ApplicationContextAware {

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) context.getBean(beanName);
    }

    public static String getMessage(String key) {
        return context.getMessage(key, null, Locale.getDefault());
    }

    /**
     * 获取当前环境
     * @return dev or test or ...
     */
    public static String getActiveProfile() {
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        return activeProfiles.length == 0 ? "default" : activeProfiles[0];
    }
}

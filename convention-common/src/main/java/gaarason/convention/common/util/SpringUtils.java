package gaarason.convention.common.util;

import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.model.annotation.web.ExcludeUnifiedRequest;
import gaarason.convention.common.model.annotation.web.ExcludeUnifiedResponse;
import gaarason.convention.common.model.annotation.web.UnifiedRequest;
import gaarason.convention.common.model.annotation.web.UnifiedResponse;
import gaarason.convention.common.model.exception.BusinessException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * spring 工具
 * @author xt
 */
public final class SpringUtils {

    private static ApplicationContext applicationContext = null;

    private SpringUtils() {

    }

    /**
     * 设置spring上下文, 需提前执行
     * @param context ApplicationContext
     */
    public static void setApplicationContext(ApplicationContext context) {
        SpringUtils.applicationContext = context;
    }

    /**
     * 获取spring上下文
     * @return ApplicationContext
     */
    public static ApplicationContext getContext() {
        return SpringUtils.applicationContext;
    }

    /**
     * 获取容器中的对象
     * @param beanName 对象id
     * @param <T>      对象类型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) SpringUtils.applicationContext.getBean(beanName);
    }

    /**
     * 获取容器中的对象
     * @param requiredType 对象类型
     * @param <T>          对象类型
     * @return 对象
     */
    public static <T> T getBean(Class<T> requiredType) {
        return SpringUtils.applicationContext.getBean(requiredType);
    }

    /**
     * 获取当前环境
     * @return dev or test or ...
     */
    public static String getActiveProfile() {
        String[] activeProfiles = SpringUtils.applicationContext.getEnvironment().getActiveProfiles();
        return activeProfiles.length == 0 ? "default" : activeProfiles[0];
    }

    /**
     * 是否使用统一参数解析
     * 判断逻辑: 参数上标记开启 || ((((开启参数解析配置 && 当前类在spring扫描的范围内) || 类上标记开启) && 类上没有标记排除) || 参数上没有标记排除)
     * @param parameter 方法的形参
     * @return 是否使用统一参数解析
     */
    public static boolean isResolveArgumentSupport(MethodParameter parameter, ConventionProperties conventionProperties) {
        // 所在类
        Class<?> containingClass = parameter.getContainingClass();
        // 所在类的完全限定名称
        String className = containingClass.getName();

        // (开启参数解析配置 && 当前类在spring扫描的范围内) || 类上标记开启
        boolean shouldDo = (conventionProperties.getHttp().isEnableArgumentResolver() && SpringUtils.getScanBasePackages().stream().anyMatch(
            className::startsWith))
            || AnnotatedElementUtils.hasAnnotation(containingClass, UnifiedRequest.class);

        // 类上的 @ExcludeUnifiedRequest 分析
        shouldDo = shouldDo && !AnnotatedElementUtils.hasAnnotation(containingClass, ExcludeUnifiedRequest.class);

        // 参数上标记开启 || (在开启参数解析的范围内 && 参数上没有标记排除)
        shouldDo = parameter.hasParameterAnnotation(UnifiedRequest.class) || (shouldDo && !parameter.hasParameterAnnotation(
            ExcludeUnifiedRequest.class));

        return shouldDo;
    }

    /**
     * 是否使用统一响应
     * 判断逻辑: 方法上标记开启 || ((((开启响应封装配置 && 当前类在spring扫描的范围内) || 类上标记开启) && 类上没有标记排除) || 方法上没有标记排除)
     * @param returnType 方法的签名
     * @return 是否使用统一响应
     */
    public static boolean isReturnTypeSupport(MethodParameter returnType, ConventionProperties conventionProperties) {
        // 所在类
        Class<?> containingClass = returnType.getContainingClass();
        // 所在类的完全限定名称
        String className = containingClass.getName();

        // (开启响应封装配置 && 当前类在spring扫描的范围内) || 类上标记开启
        boolean shouldDo = (conventionProperties.getHttp().isEnableReturnValueHandler() && SpringUtils.getScanBasePackages().stream().anyMatch(
            className::startsWith))
            || AnnotatedElementUtils.hasAnnotation(containingClass, UnifiedResponse.class);

        // 类上的 @ExcludeUnifiedResponse 分析
        shouldDo = shouldDo && !AnnotatedElementUtils.hasAnnotation(containingClass, ExcludeUnifiedResponse.class);

        // 方法上标记开启 || (在开启响应封装的范围内 && 方法上没有标记排除)
        shouldDo = returnType.hasMethodAnnotation(UnifiedResponse.class) || (shouldDo && !returnType.hasMethodAnnotation(
            ExcludeUnifiedResponse.class));

        return shouldDo;
    }

    /**
     * 简单的获取启动类上的包扫描路径, 完全不考虑spring的其他兼容与别名注解
     * @return 包扫描路径列表
     */
    public static List<String> getScanBasePackages() {
        try {
            Map<String, Object> annotatedBeans = SpringUtils.getContext().getBeansWithAnnotation(SpringBootApplication.class);
            Class<?> mainApplicationClass = annotatedBeans.values().toArray()[0].getClass();
            SpringBootApplication annotation = AnnotationUtils.findAnnotation(mainApplicationClass, SpringBootApplication.class);
            assert annotation != null;
            String[] strings = annotation.scanBasePackages();
            if (strings.length == 0) {
                return Collections.singletonList(StringUtils.rtrim(mainApplicationClass.getName(), "." + mainApplicationClass.getSimpleName()));
            }
            return Arrays.asList(strings.clone());
        } catch (Throwable e) {
            throw new BusinessException("获取启动类上的包扫描路径失败", e);
        }
    }

    /**
     * 将spring的配置,转入到system
     * @param environment   环境
     * @param propertyNames 配置名
     */
    public static void turnSpringPropertyToSystemIfPresent(ConfigurableEnvironment environment, String... propertyNames) {
        for (String propertyName : propertyNames) {
            String springPropertyValue = environment.getProperty(propertyName);
            if (org.springframework.util.StringUtils.hasText(springPropertyValue)) {
                System.setProperty(propertyName, springPropertyValue);
            }
        }
    }

    /**
     * 将spring的配置,转入到system, 不存在时, 使用默认值
     * @param environment  环境
     * @param propertyName 配置名
     * @param defaultValue 默认值
     */
    public static void turnSpringPropertyToSystem(ConfigurableEnvironment environment, String propertyName, String defaultValue) {
        String springPropertyValue = environment.getProperty(propertyName);
        if (org.springframework.util.StringUtils.hasText(springPropertyValue)) {
            System.setProperty(propertyName, springPropertyValue);
        } else {
            System.setProperty(propertyName, defaultValue);
        }

    }
}

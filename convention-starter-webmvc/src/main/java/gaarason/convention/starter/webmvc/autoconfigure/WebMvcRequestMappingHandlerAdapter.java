package gaarason.convention.starter.webmvc.autoconfigure;

import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.web.support.WebMappingJackson2HttpMessageConverter;
import gaarason.convention.starter.webmvc.request.WebMvcHandlerMethodArgumentResolver;
import gaarason.convention.starter.webmvc.response.WebMvcHandlerMethodReturnValueHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 设置全局参数解析(最高优先级), 设置全局统一响应(最高优先级)
 * @author xt
 */
public class WebMvcRequestMappingHandlerAdapter implements InitializingBean {

    @Resource
    protected RequestMappingHandlerAdapter adapter;

    @Resource
    private ConventionProperties conventionProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 获取原 HttpMessageConverters, 并增加 SpecialMappingJackson2HttpMessageConverter 到首位
        final List<HttpMessageConverter<?>> httpMessageConverters = redefiningHttpMessageConverters();

        // 获取原 RequestResponseBodyAdvices
        final List<Object> requestResponseBodyAdvices = redefiningRequestResponseBodyAdvices();

        // 获取 HandlerMethodReturnValueHandlers, 并增加 WebHandlerMethodReturnValueHandler 到首位
        final List<HandlerMethodReturnValueHandler> handlerMethodReturnValueHandlers =
            redefiningHandlerMethodReturnValueHandlers(httpMessageConverters, requestResponseBodyAdvices);

        // 设置修改过的 handlerMethodReturnValueHandlers 到 adapter
        adapter.setReturnValueHandlers(handlerMethodReturnValueHandlers);

        // 获取 HandlerMethodArgumentResolver, 并增加 WebHandlerMethodArgumentResolver 到首位
        final List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers =
            redefiningHandlerMethodArgumentResolvers(httpMessageConverters, conventionProperties);

        // 设置修改过的 handlerMethodArgumentResolvers 到 adapter
        adapter.setArgumentResolvers(handlerMethodArgumentResolvers);
    }

    /**
     * 获取原HttpMessageConverters, 并增加SpecialMappingJackson2HttpMessageConverter到首位
     * @return 修改过的全新对象HttpMessageConverters
     */
    protected List<HttpMessageConverter<?>> redefiningHttpMessageConverters() {
        // 返回全新对象, 不影响spring原本的 MessageConverters
        final List<HttpMessageConverter<?>> messageConverters = new ArrayList<>(adapter.getMessageConverters());
        messageConverters.add(0, new WebMappingJackson2HttpMessageConverter());
        return messageConverters;
    }

    /**
     * 获取原RequestResponseBodyAdvices
     * @return RequestResponseBodyAdvices
     * @throws NoSuchFieldException   异常
     * @throws IllegalAccessException 异常
     */
    protected List<Object> redefiningRequestResponseBodyAdvices() throws NoSuchFieldException, IllegalAccessException {
        final Field field = RequestMappingHandlerAdapter.class.getDeclaredField("requestResponseBodyAdvice");
        field.setAccessible(true);
        return ObjectUtils.typeCast(field.get(adapter));
    }

    /**
     * 获取 HandlerMethodReturnValueHandlers, 并增加 SpecialRequestResponseBodyMethodProcessors 到首位
     * @return HandlerMethodReturnValueHandlers
     */
    protected List<HandlerMethodReturnValueHandler> redefiningHandlerMethodReturnValueHandlers(
        final List<HttpMessageConverter<?>> httpMessageConverters,
        final List<Object> requestResponseBodyAdvices) {
        final List<HandlerMethodReturnValueHandler> returnValueHandlers = adapter.getReturnValueHandlers();
        // 正常情况下, 不会为null
        assert returnValueHandlers != null;
        final List<HandlerMethodReturnValueHandler> theHandlers = new ArrayList<>(returnValueHandlers);

        // 增加 SpecialRequestResponseBodyMethodProcessors 到首位
        theHandlers.add(0, new WebMvcHandlerMethodReturnValueHandler(httpMessageConverters, requestResponseBodyAdvices, conventionProperties));
        return theHandlers;
    }

    /**
     * 获取 x, 并增加 x 到首位
     * @return x
     */
    protected List<HandlerMethodArgumentResolver> redefiningHandlerMethodArgumentResolvers(final List<HttpMessageConverter<?>> httpMessageConverters,
        final ConventionProperties properties) {
        final List<HandlerMethodArgumentResolver> argumentResolvers = adapter.getArgumentResolvers();
        // 正常情况下, 不会为null
        assert argumentResolvers != null;
        final List<HandlerMethodArgumentResolver> theHandlers = new ArrayList<>(argumentResolvers);

        // 增加 到首位
        theHandlers.add(0, new WebMvcHandlerMethodArgumentResolver(httpMessageConverters, properties));
        return theHandlers;
    }

}

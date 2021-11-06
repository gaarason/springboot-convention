package gaarason.convention.common.web.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.util.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Collections;

/**
 * @author xt
 */
public class WebMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

    /**
     * Construct a new {@link MappingJackson2HttpMessageConverter} using default configuration
     * provided by {@link Jackson2ObjectMapperBuilder}.
     */
    public WebMappingJackson2HttpMessageConverter() {
        this(JsonUtils.getMapper());
    }

    /**
     * Construct a new {@link MappingJackson2HttpMessageConverter} with a custom {@link ObjectMapper}.
     * You can use {@link Jackson2ObjectMapperBuilder} to build it easily.
     * @see Jackson2ObjectMapperBuilder#json()
     */
    public WebMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
        setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    /**
     * 是否可写
     * @param clazz     Class<?>
     * @param mediaType MediaType
     * @return boolean
     */
    @Override
    public boolean canWrite(@NotNull Class<?> clazz, @Nullable MediaType mediaType) {
        return true;
    }

    /**
     * 指定json
     * @param object Object
     * @return MediaType
     */
    @Override
    protected MediaType getDefaultContentType(@NotNull Object object) {
        return MediaType.APPLICATION_JSON;
    }

    /**
     * 设置头
     * @param headers     HttpHeaders
     * @param t           Object
     * @param contentType MediaType
     * @throws IOException 网络异常
     */
    @Override
    protected void addDefaultHeaders(@NotNull HttpHeaders headers, @NotNull Object t, @Nullable MediaType contentType) throws IOException {
        super.addDefaultHeaders(headers, t, contentType);
        // 走统一响应的, 在响应头中标记一下, 方便客户端解码时判断
        headers.set(FinalVariable.Http.CONVENTION_MARK_UNIFIED_RESPONSE, "1");
        headers.set(FinalVariable.Http.CHARACTER_ENCODING, "utf-8");
    }
}

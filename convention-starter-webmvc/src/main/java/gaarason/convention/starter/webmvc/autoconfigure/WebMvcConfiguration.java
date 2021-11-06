package gaarason.convention.starter.webmvc.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.convention.common.util.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

/**
 * @author xt
 */
public class WebMvcConfiguration implements WebMvcConfigurer {

    public static class SpecialMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {

        /**
         * Construct a new {@link MappingJackson2HttpMessageConverter} using default configuration
         * provided by {@link Jackson2ObjectMapperBuilder}.
         */
        public SpecialMappingJackson2HttpMessageConverter() {
            this(JsonUtils.getMapper());
        }

        /**
         * Construct a new {@link MappingJackson2HttpMessageConverter} with a custom {@link ObjectMapper}.
         * You can use {@link Jackson2ObjectMapperBuilder} to build it easily.
         *
         * @see Jackson2ObjectMapperBuilder#json()
         */
        public SpecialMappingJackson2HttpMessageConverter(final ObjectMapper objectMapper) {
            super(objectMapper);
            setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        }

        @Override
        public boolean canWrite(@NotNull final Class<?> clazz, @Nullable final MediaType mediaType) {
            return super.canWrite(clazz, mediaType);
        }

        @Override
        protected MediaType getDefaultContentType(@NotNull final Object object) {
            return MediaType.APPLICATION_JSON;
        }
    }
}

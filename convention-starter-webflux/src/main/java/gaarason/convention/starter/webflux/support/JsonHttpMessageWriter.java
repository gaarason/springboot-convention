package gaarason.convention.starter.webflux.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.provider.LogProvider;
import gaarason.convention.common.util.JsonUtils;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author xt
 */
public class JsonHttpMessageWriter implements HttpMessageWriter<Object> {

    private static final ObjectMapper OBJECT_MAPPER = JsonUtils.getMapper();

    @NonNull
    @Override
    public List<MediaType> getWritableMediaTypes() {
        // todo check
        return Collections.singletonList(MediaType.ALL);
        // return Collections.singletonList(MediaType.APPLICATION_JSON);
    }

    @Override
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        // todo check
        return true;
        // return MediaType.APPLICATION_JSON.includes(mediaType);
    }

    @NonNull
    @Override
    public Mono<Void> write(Publisher<?> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message,
        Map<String, Object> hints) {
        message.getHeaders().set(FinalVariable.Http.CONVENTION_MARK_UNIFIED_RESPONSE, "1");
        message.getHeaders().set(FinalVariable.Http.CHARACTER_ENCODING, "utf-8");

        return Mono.from(inputStream).flatMap(
            m -> message.writeWith(Mono.just(message.bufferFactory().wrap(JsonHttpMessageWriter.transform2Json(m).getBytes()))));
    }

    private static String transform2Json(Object sourceMap) {
        try {
            return JsonHttpMessageWriter.OBJECT_MAPPER.writeValueAsString(sourceMap);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}

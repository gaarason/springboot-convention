package gaarason.convention.starter.webflux.error;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.starter.webflux.support.JsonHttpMessageWriter;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * @author xt
 */
@Order(-2)
public class WebFluxGlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public WebFluxGlobalErrorWebExceptionHandler(WebFluxErrorAttributes errorAttributes, WebProperties.Resources resources,
        ApplicationContext applicationContext) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(Collections.singletonList(new JsonHttpMessageWriter()));
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        return ServerResponse.status(200).header(FinalVariable.Http.CONVENTION_MARK_UNIFIED_RESPONSE, "1")
            .header(FinalVariable.Http.CHARACTER_ENCODING, "utf-8").contentType(MediaType.APPLICATION_JSON).body(
                BodyInserters.fromValue(errorPropertiesMap));
    }

}

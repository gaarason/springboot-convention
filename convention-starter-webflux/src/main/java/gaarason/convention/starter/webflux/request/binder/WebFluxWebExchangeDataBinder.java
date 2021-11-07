package gaarason.convention.starter.webflux.request.binder;

import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.util.StringUtils;
import gaarason.convention.common.web.util.LocalDateTimeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author xt
 */
public class WebFluxWebExchangeDataBinder extends WebExchangeDataBinder {

    public WebFluxWebExchangeDataBinder(WebExchangeDataBinder originalSpringBinder) {
        this(Objects.requireNonNull(originalSpringBinder.getTarget()), originalSpringBinder.getObjectName());
        addValidators(originalSpringBinder.getValidator());
        setAutoGrowNestedPaths(originalSpringBinder.isAutoGrowNestedPaths());
        // No get method
        setMessageCodesResolver(null);
        setBindingErrorProcessor(originalSpringBinder.getBindingErrorProcessor());
        setValidator(originalSpringBinder.getValidator());
        setConversionService(originalSpringBinder.getConversionService());

    }

    public WebFluxWebExchangeDataBinder(Object target, String objectName) {
        super(target, objectName);
    }

    /**
     * Combine query params and form data for multipart form data from the body
     * of the request into a {@code Map<String, Object>} of values to use for
     * data binding purposes.
     * @param exchange the current exchange
     * @return a {@code Mono} with the values to bind
     * @see org.springframework.http.server.reactive.ServerHttpRequest#getQueryParams()
     * @see ServerWebExchange#getFormData()
     * @see ServerWebExchange#getMultipartData()
     */
    public static Mono<Map<String, Object>> extractValuesToBind(ServerWebExchange exchange) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        Mono<MultiValueMap<String, String>> formData = exchange.getFormData();
        Mono<MultiValueMap<String, Part>> multipartData = exchange.getMultipartData();

        return Mono.zip(Mono.just(queryParams), formData, multipartData).map(tuple -> {
            Map<String, Object> result = new TreeMap<>();
            tuple.getT1().forEach((key, values) -> WebFluxWebExchangeDataBinder.addBindValue(result, key, values));
            tuple.getT2().forEach((key, values) -> WebFluxWebExchangeDataBinder.addBindValue(result, key, values));
            tuple.getT3().forEach((key, values) -> WebFluxWebExchangeDataBinder.addBindValue(result, key, values));
            return result;
        });
    }

    protected static void addBindValue(Map<String, Object> params, String key, List<?> values) {
        if (!CollectionUtils.isEmpty(values)) {
            values = values.stream().map(value -> value instanceof FormFieldPart ? ((FormFieldPart) value).value() : value).collect(
                Collectors.toList());
            /////////////////
            // 下划线转驼峰
            /////////////////
            params.put(StringUtils.lineToHump(key), values.size() == 1 ? values.get(0) : values);
        }
    }

    /**
     * Bind query params, form data, and or multipart form data to the binder target.
     * @param exchange the current exchange
     * @return a {@code Mono<Void>} when binding is complete
     */
    @Override
    public Mono<Void> bind(ServerWebExchange exchange) {
        try {
            return getValuesToBind(exchange).doOnNext(values -> doBind(new MutablePropertyValues(values))).then();
        } catch (Throwable e) {
            throw new BusinessException(StatusCode.PARAMETER_NOT_READABLE, e);
        }

    }

    /**
     * Protected method to obtain the values for data binding. By default this
     * method delegates to {@link #extractValuesToBind(ServerWebExchange)}.
     */
    @Override
    @NotNull
    public Mono<Map<String, Object>> getValuesToBind(@NotNull ServerWebExchange exchange) {
        return WebFluxWebExchangeDataBinder.extractValuesToBind(exchange);
    }

    /**
     * Return the underlying PropertyAccessor of this binder's BindingResult.
     */
    @Override
    protected ConfigurablePropertyAccessor getPropertyAccessor() {
        ConfigurablePropertyAccessor registry = getInternalBindingResult().getPropertyAccessor();
        // 时间解析器
        LocalDateTimeUtils.registerCustomEditorForLocalDateTime(registry);

        return registry;
    }
}

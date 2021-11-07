package gaarason.convention.starter.webflux.request;

import gaarason.convention.starter.webflux.request.binder.WebFluxWebExchangeDataBinder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.core.*;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.annotation.ModelAttributeMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.beans.ConstructorProperties;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author xt
 */
public class WebFluxModelAttributeMethodArgumentResolver extends ModelAttributeMethodArgumentResolver {

    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * Class constructor with a default resolution mode flag.
     * @param adapterRegistry      for adapting to other reactive types from and to Mono
     * @param useDefaultResolution if "true", non-simple method arguments and
     *                             return values are considered model attributes with or without a
     *                             {@code @ModelAttribute} annotation present.
     */
    public WebFluxModelAttributeMethodArgumentResolver(ReactiveAdapterRegistry adapterRegistry, boolean useDefaultResolution) {
        super(adapterRegistry, useDefaultResolution);
    }

    public static String getNameForParameter(MethodParameter parameter) {
        ModelAttribute ann = parameter.getParameterAnnotation(ModelAttribute.class);
        String name = (ann != null ? ann.value() : null);
        return (StringUtils.hasText(name) ? name : Conventions.getVariableNameForParameter(parameter));
    }

    private static boolean hasErrorsArgumentCopyParent(MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        return (paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]));
    }

    private static Mono<?> createAttributeCopyParent(String attributeName, Class<?> clazz, BindingContext context,
        ServerWebExchange exchange) {

        Constructor<?> ctor = BeanUtils.findPrimaryConstructor(clazz);
        if (ctor == null) {
            Constructor<?>[] ctors = clazz.getConstructors();
            if (ctors.length == 1) {
                ctor = ctors[0];
            } else {
                try {
                    ctor = clazz.getDeclaredConstructor();
                } catch (NoSuchMethodException ex) {
                    throw new IllegalStateException("No primary or default constructor found for " + clazz, ex);
                }
            }
        }
        return WebFluxModelAttributeMethodArgumentResolver.constructAttributeCopyParent(ctor, attributeName, context, exchange);
    }

    private static Mono<?> constructAttributeCopyParent(Constructor<?> ctor, String attributeName, BindingContext context,
        ServerWebExchange exchange) {

        if (ctor.getParameterCount() == 0) {
            // A single default constructor -> clearly a standard JavaBeans arrangement.
            return Mono.just(BeanUtils.instantiateClass(ctor));
        }

        // A single data class constructor -> resolve constructor arguments from request parameters.
        return WebExchangeDataBinder.extractValuesToBind(exchange).map(bindValues -> {
            ConstructorProperties cp = ctor.getAnnotation(ConstructorProperties.class);
            String[] paramNames =
                (cp != null ? cp.value() : WebFluxModelAttributeMethodArgumentResolver.PARAMETER_NAME_DISCOVERER.getParameterNames(ctor));
            Assert.state(paramNames != null, () -> "Cannot resolve parameter names for constructor " + ctor);
            Class<?>[] paramTypes = ctor.getParameterTypes();
            Assert.state(paramNames.length == paramTypes.length,
                () -> "Invalid number of parameter names: " + paramNames.length + " for constructor " + ctor);
            Object[] args = new Object[paramTypes.length];
            WebDataBinder binder = context.createDataBinder(exchange, null, attributeName);
            String fieldDefaultPrefix = binder.getFieldDefaultPrefix();
            String fieldMarkerPrefix = binder.getFieldMarkerPrefix();
            for (int i = 0; i < paramNames.length; i++) {
                String paramName = paramNames[i];
                Class<?> paramType = paramTypes[i];
                Object value = bindValues.get(paramName);
                if (value == null) {
                    if (fieldDefaultPrefix != null) {
                        value = bindValues.get(fieldDefaultPrefix + paramName);
                    }
                    if (value == null && fieldMarkerPrefix != null) {
                        if (bindValues.get(fieldMarkerPrefix + paramName) != null) {
                            value = binder.getEmptyValue(paramType);
                        }
                    }
                }
                value = (value instanceof List ? ((List<?>) value).toArray() : value);
                MethodParameter methodParam = new MethodParameter(ctor, i);
                if (value == null && methodParam.isOptional()) {
                    args[i] = (methodParam.getParameterType() == Optional.class ? Optional.empty() : null);
                } else {
                    args[i] = binder.convertIfNecessary(value, paramTypes[i], methodParam);
                }
            }
            return BeanUtils.instantiateClass(ctor, args);
        });
    }

    @NotNull
    @Override
    public Mono<Object> resolveArgument(@NotNull MethodParameter parameter, @NotNull BindingContext context,
        @NotNull ServerWebExchange exchange) {
        ResolvableType type = ResolvableType.forMethodParameter(parameter);
        Class<?> resolvedType = type.resolve();
        ReactiveAdapter adapter = (resolvedType != null ? getAdapterRegistry().getAdapter(resolvedType) : null);
        ResolvableType valueType = (adapter != null ? type.getGeneric() : type);

        Assert.state(adapter == null || !adapter.isMultiValue(),
            () -> getClass().getSimpleName() + " does not support multi-value reactive type wrapper: " + parameter.getGenericParameterType());

        String name = WebFluxModelAttributeMethodArgumentResolver.getNameForParameter(parameter);
        Mono<?> valueMono = prepareAttributeMonoCopyParent(name, valueType, context, exchange);

        // unsafe(): we're intercepting, already serialized Publisher signals
        Sinks.One<BindingResult> bindingResultSink = Sinks.unsafe().one();
        Map<String, Object> model = context.getModel().asMap();
        model.put(BindingResult.MODEL_KEY_PREFIX + name, bindingResultSink.asMono());

        return valueMono.flatMap(value -> {
            ///////////////////////
            // have change
            ///////////////////////
            WebFluxWebExchangeDataBinder binder = new WebFluxWebExchangeDataBinder(context.createDataBinder(exchange, value, name));

            return bindRequestParameters(binder, exchange).doOnError(bindingResultSink::tryEmitError).doOnSuccess(aVoid -> {
                AbstractWebfluxCustomizeResolver.validateIfApplicable(binder, parameter);
                BindingResult bindingResult = binder.getBindingResult();
                model.put(BindingResult.MODEL_KEY_PREFIX + name, bindingResult);
                model.put(name, value);
                // Ignore result: serialized and buffered (should never fail)
                bindingResultSink.tryEmitValue(bindingResult);
            }).then(Mono.fromCallable(() -> {
                BindingResult errors = binder.getBindingResult();
                if (adapter != null) {
                    return adapter.fromPublisher(errors.hasErrors() ? Mono.error(new WebExchangeBindException(parameter, errors)) : valueMono);
                } else {
                    if (errors.hasErrors() && !WebFluxModelAttributeMethodArgumentResolver.hasErrorsArgumentCopyParent(parameter)) {
                        throw new WebExchangeBindException(parameter, errors);
                    }
                    return value;
                }
            }));
        });
    }

    private Mono<?> prepareAttributeMonoCopyParent(String attributeName, ResolvableType attributeType, BindingContext context,
        ServerWebExchange exchange) {

        Object attribute = context.getModel().asMap().get(attributeName);

        if (attribute == null) {
            attribute = findAndRemoveReactiveAttributeCopyParent(context.getModel(), attributeName);
        }

        if (attribute == null) {
            return WebFluxModelAttributeMethodArgumentResolver.createAttributeCopyParent(attributeName, attributeType.toClass(), context, exchange);
        }

        ReactiveAdapter adapter = getAdapterRegistry().getAdapter(null, attribute);
        if (adapter != null) {
            Assert.isTrue(!adapter.isMultiValue(), "Data binding only supports single-value async types");
            return Mono.from(adapter.toPublisher(attribute));
        } else {
            return Mono.justOrEmpty(attribute);
        }
    }

    @Nullable
    private Object findAndRemoveReactiveAttributeCopyParent(Model model, String attributeName) {
        return model.asMap().entrySet().stream().filter(entry -> {
            if (!entry.getKey().startsWith(attributeName)) {
                return false;
            }
            ReactiveAdapter adapter = getAdapterRegistry().getAdapter(null, entry.getValue());
            if (adapter == null) {
                return false;
            }
            String name = attributeName + ClassUtils.getShortName(adapter.getReactiveType());
            return entry.getKey().equals(name);
        }).findFirst().map(entry -> {
            // Remove since we will be re-inserting the resolved attribute value
            model.asMap().remove(entry.getKey());
            return entry.getValue();
        }).orElse(null);
    }
}

package gaarason.convention.starter.webflux.util;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.provider.LogProvider;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.util.SpringUtils;
import gaarason.convention.common.util.StringUtils;
import gaarason.convention.common.web.contract.ChainContextHandlerContract;
import gaarason.convention.common.web.pojo.GeneralRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.*;

/**
 * 请求分析工具
 * @author xt
 */
public final class WebFluxRequestUtils {

    private static final Logger LOGGER = LogManager.getLogger(WebFluxRequestUtils.class);

    private WebFluxRequestUtils() {
    }

    /**
     * 从请求头中依次获请求头的值, 只要有一个存在就即刻返回, 都不存在时返回默认值
     * @param defaultValue 默认值
     * @param exchange     请求
     * @param keys         请求头
     * @return 值
     */
    public static String getOneInHeader(String defaultValue, ServerWebExchange exchange, String... keys) {
        String theHeader = null;
        try {
            for (String key : keys) {
                List<String> theHeaders = exchange.getRequest().getHeaders().get(key);
                if (!ObjectUtils.isEmpty(theHeaders)) {
                    theHeader = theHeaders.get(0);
                    if (theHeader != null) {
                        break;
                    }
                }
            }
        } catch (RuntimeException e) {
            WebFluxRequestUtils.LOGGER.error("getOneInHeader处理{}出错", keys, e);
        }
        return theHeader != null ? theHeader : defaultValue;
    }

    /**
     * 从请求头中依次获请求头的值, 只要有一个存在就即刻返回, 都不存在时返回默认值
     * @param defaultValue 默认值
     * @param request      请求
     * @param keys         请求头
     * @return 值
     */
    public static String getOneInHeader(String defaultValue, HttpRequest request, String... keys) {
        String value = null;
        try {
            HttpHeaders headers = request.headers();
            flag:
            for (String key : keys) {
                for (Map.Entry<String, String> header : headers) {
                    if (header.getKey().equals(key)) {
                        value = header.getValue();
                        if (value != null) {
                            break flag;
                        }
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.error("getOneInHeader处理{}出错", keys, e);
        }
        return value != null ? value : defaultValue;
    }

    public static String dealRequestRealUrl(ServerWebExchange exchange) {
        String requestRealUrl = "";
        try {
            requestRealUrl = exchange.getRequest().getURI().toString();
        } catch (RuntimeException e) {
            LOGGER.error("处理REQUEST_REAL_URL出错", e);
        }
        return requestRealUrl;
    }

    public static String dealRequestUrl(ServerWebExchange exchange) {
        return WebFluxRequestUtils.getOneInHeader("", exchange, FinalVariable.X_FORWARD_REQUEST_URL);
    }

    public static String dealRequestRealUrl(HttpRequest request) {
        String requestRealUrl = "";
        try {
            String protocol = request.protocolVersion().protocolName().toLowerCase();
            String uri = request.uri();
            String host = WebFluxRequestUtils.getOneInHeader("127.0.0.1:8080", request, "Host");
            requestRealUrl = protocol + "://" + host + uri;
        } catch (Exception e) {
            LOGGER.error("处理REQUEST_REAL_URL出错", e);
        }
        return requestRealUrl;
    }

    /**
     * 获取 traceId
     * @param exchange 请求
     * @return traceId
     */
    public static String dealTraceId(ServerWebExchange exchange) {
        return ChainProvider.computeIfAbsentTraceId(
            () -> getOneInHeader("", exchange, ChainProvider.CanCrossProcessKey.TRACE_ID.getHttpHeaderKey()));
    }

    /**
     * 获取 traceId
     * @param request 请求
     * @return traceId
     */
    public static String dealTraceId(HttpRequest request) {
        return ChainProvider.computeIfAbsentTraceId(
            () -> getOneInHeader("", request, ChainProvider.CanCrossProcessKey.TRACE_ID.getHttpHeaderKey()));
    }

    public static String dealRequestUrl(HttpRequest request) {
        return WebFluxRequestUtils.getOneInHeader("", request, FinalVariable.X_FORWARD_REQUEST_URL);
    }

    /**
     * 在请求入口, 打印入口日志
     * 只有通信异常, 才会在这儿记录http入口日志与通信异常响应格式化
     * 此处不用考虑多线程的问题
     * @param request HttpRequest
     */
    public static void printHttpProviderReceivedRequestLog(HttpRequest request) {

        LogProvider.clear();

        // 设置其他上下文
        setChainContext(request);

        // 设置traceId
        ChainProvider.put(ChainProvider.CanCrossProcessKey.TRACE_ID, dealTraceId(request));

        // 设置请求方法
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_METHOD, request.method().name());

        // 设置请求头
        Map<String, List<String>> headersMap = WebFluxRequestUtils.formattingHeader(request);
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_HEADER_STRING, headersMap.toString());

        // 设置请求时间
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_DATETIME, FinalVariable.NOW_DATETIME.get());

        // 设置请求后端URL
        String requestRealUrl = WebFluxRequestUtils.dealRequestRealUrl(request);
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_REAL_URL, requestRealUrl);

        // 设置请求URL
        String requestUrl = WebFluxRequestUtils.dealRequestUrl(request);
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_URL, requestUrl.isEmpty() ? requestRealUrl : requestUrl);

        // 请求入口日志
        LogProvider.getInstance().printHttpProviderReceivedRequestLog();
    }

    /**
     * 在请求入口, 打印入口日志
     * 此处必须考虑多线程的问题
     * @param exchange exchange
     */
    public static void printHttpProviderReceivedRequestLog(ServerWebExchange exchange) {
        Map<String, Object> attributes = exchange.getAttributes();

        LogProvider.clear(attributes);

        // 设置其他上下文
        setChainContext(attributes, exchange);

        // 设置traceId
        ChainProvider.put(attributes, ChainProvider.CanCrossProcessKey.TRACE_ID, dealTraceId(exchange));

        // 设置请求方法
        ChainProvider.put(attributes, ChainProvider.CanNotCrossProcessKey.REQUEST_METHOD, exchange.getRequest().getMethodValue());

        // 设置请求头
        Map<String, List<String>> headersMap = WebFluxRequestUtils.formattingHeader(exchange);
        ChainProvider.put(attributes, ChainProvider.CanNotCrossProcessKey.REQUEST_HEADER_STRING, headersMap.toString());

        // 设置请求时间
        ChainProvider.put(attributes, ChainProvider.CanNotCrossProcessKey.REQUEST_DATETIME, FinalVariable.NOW_DATETIME.get());

        // 设置请求后端URL
        String requestRealUrl = WebFluxRequestUtils.dealRequestRealUrl(exchange);
        ChainProvider.put(attributes, ChainProvider.CanNotCrossProcessKey.REQUEST_REAL_URL, requestRealUrl);

        // 设置请求URL
        String requestUrl = WebFluxRequestUtils.dealRequestUrl(exchange);
        ChainProvider.put(attributes, ChainProvider.CanNotCrossProcessKey.REQUEST_URL, requestUrl.isEmpty() ? requestRealUrl : requestUrl);

        // 设置自定义上下文
        SpringUtils.getBean(ChainContextHandlerContract.class).conversion(generateRequest(exchange));

        // 请求入口日志
        LogProvider.getInstance().printHttpProviderReceivedRequestLog();
    }

    /**
     * 获取请求头, 且格式化
     * @param request 请求
     * @return 请求头
     */
    private static Map<String, List<String>> formattingHeader(HttpRequest request) {
        Map<String, List<String>> headersMap = new HashMap<>(16);
        HttpHeaders httpHeaders = request.headers();

        Set<String> headerKeys = httpHeaders.names();
        for (String headerKey : headerKeys) {
            List<String> values = httpHeaders.getAll(headerKey);
            headersMap.put(headerKey, values);
        }
        return headersMap;
    }

    /**
     * 获取请求头, 且格式化
     * @param exchange 请求
     * @return 请求头
     */
    private static Map<String, List<String>> formattingHeader(ServerWebExchange exchange) {
        Map<String, List<String>> headersMap = new HashMap<>(16);
        org.springframework.http.HttpHeaders httpHeaders = exchange.getRequest().getHeaders();

        headersMap.putAll(httpHeaders);
        return headersMap;
    }

    /**
     * 生成简易request 对象
     * @param request 请求对象
     * @return 简易request
     */
    public static GeneralRequest<HttpRequest> generateRequest(HttpRequest request) {
        String querySearch = request.uri();
        final Map<String, List<String>> headerMap = new HashMap<>(16);
        for (Map.Entry<String, String> entry : request.headers()) {
            List<String> list = headerMap.get(entry.getKey());
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(entry.getValue());
            headerMap.put(entry.getKey(), list);
        }
        return new GeneralRequest<>(querySearch, headerMap, request);
    }

    /**
     * 生成简易request 对象
     * @param exchange 请求对象
     * @return 简易request
     */
    public static GeneralRequest<ServerHttpRequest> generateRequest(ServerWebExchange exchange) {
        ServerHttpRequest theRequest = exchange.getRequest();
        String querySearch = StringUtils.mapToQuerySearch(ObjectUtils.typeCast(theRequest.getQueryParams()));
        Map<String, List<String>> headerMap = ObjectUtils.typeCast(theRequest.getHeaders());
        return new GeneralRequest<>(querySearch, headerMap, theRequest);
    }


    /**
     * 设置请求上下文信息
     * @param request 请求
     */
    private static void setChainContext(HttpRequest request) {
        HttpHeaders headers = request.headers();
        for (ChainProvider.CanCrossProcessKey processKey : ChainProvider.CanCrossProcessKey.values()) {
            String value = headers.get(processKey.getHttpHeaderKey());
            ChainProvider.put(processKey, value);
        }

        // 自定义上下文
        SpringUtils.getBean(ChainContextHandlerContract.class).conversion(generateRequest(request));
    }

    /**
     * 设置请求上下文信息
     * @param attributes 用于补偿的存储对象
     * @param exchange   请求
     */
    private static void setChainContext(Map<String, Object> attributes, ServerWebExchange exchange) {
        Map<String, String> headers = exchange.getRequest().getHeaders().toSingleValueMap();
        for (ChainProvider.CanCrossProcessKey processKey : ChainProvider.CanCrossProcessKey.values()) {
            String value = headers.get(processKey.getHttpHeaderKey());
            ChainProvider.put(attributes, processKey, value);
        }

        // 自定义上下文
        SpringUtils.getBean(ChainContextHandlerContract.class).conversion(generateRequest(exchange));
    }
}

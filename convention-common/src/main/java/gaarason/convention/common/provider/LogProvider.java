package gaarason.convention.common.provider;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.autoconfigure.ConventionProperties;
import gaarason.convention.common.model.context.ChainContext;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.util.SpringUtils;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 日志相关
 * @author xt
 */
public class LogProvider {

    private static final Logger LOGGER = LogManager.getLogger(LogProvider.class);

    /**
     * 自身
     */
    @Nullable
    private static LogProvider instance;

    /**
     * 直接使用 swagger 的配置
     */
    @Value("${springfox.documentation.swagger-ui.enabled:true}")
    private boolean swaggerEnable;

    /**
     * 直接使用 server.port 的配置
     */
    @Value("${server.port:8080}")
    private int serverPort;

    /**
     * 直接使用 swagger 的配置
     */
    @Value("${springfox.documentation.swagger-ui.base-url:}")
    private String swaggerBaseUrl;

    @Resource
    private ConventionProperties conventionProperties;

    /**
     * 返回spring中的本对象
     * @return LogProvider
     */
    public static LogProvider getInstance() {
        LogProvider localIns = LogProvider.instance;
        if (localIns == null) {
            synchronized (LogProvider.class) {
                localIns = LogProvider.instance;
                if (localIns == null) {
                    localIns = LogProvider.instance = SpringUtils.getBean("logProvider");
                }
            }
        }
        return localIns;
    }

    /**
     * 设置 ChainContext 里面存在的所有属性
     */
    public static void pushChainContext() {
        for (Map.Entry<ChainProvider.ChainEnum, String> entry : ChainProvider.getAll().entrySet()) {
            LogProvider.pushSomething(entry.getKey().name(), entry.getValue());
        }
    }

    /**
     * 设置 ChainContext 里面存在的所有属性
     * @param attributes 用于补偿的存储对象
     */
    public static void pushChainContext(final Map<String, Object> attributes) {
        for (Map.Entry<ChainProvider.ChainEnum, String> entry : ChainProvider.getAll().entrySet()) {
            LogProvider.pushSomething(entry.getKey().name(), entry.getValue(), attributes);
        }
    }

    /**
     * 补充设置 traceId
     */
    public void pushTraceId() {
        String traceId = pullTraceId();
        if (traceId == null) {
            pushSomething(FinalVariable.LogEnum.TRACE_ID.name(), FinalVariable.GENERATE_TRACE_ID.get());
        }
    }

    /**
     * 设置 traceId
     * @param traceId traceId
     */
    public void pushTraceId(String traceId) {
        pushSomething(FinalVariable.LogEnum.TRACE_ID.name(), traceId);
    }


    /**
     * 设置 traceId
     * @param traceId    traceId
     * @param attributes 用于补偿的存储对象
     */
    public void pushTraceId(String traceId, Map<String, Object> attributes) {
        pushSomething(FinalVariable.LogEnum.TRACE_ID.name(), traceId, attributes);
    }

    /**
     * 获取当前线程的 traceId
     * @return traceId
     */
    public String pullTraceId() {
        return MDC.get(FinalVariable.LogEnum.TRACE_ID.name());
    }

    /**
     * 设置 TraceId 里面存在的所有属性
     * @param attributes 用于补偿的存储对象
     */
    public static void pushTraceId(final Map<String, Object> attributes) {

    }

    /**
     * 设置 requestMethod
     * @param requestMethod http方法
     * @param attributes    用于补偿的存储对象
     */
    public static void pushRequestMethod(final String requestMethod, final Map<String, Object> attributes) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_METHOD.name(), requestMethod.toLowerCase(), attributes);
    }

    /**
     * 设置 requestHeader
     * @param headersMap http请求头
     * @param attributes 用于补偿的存储对象
     */
    public static void pushRequestHeader(final Map<String, List<String>> headersMap, final Map<String, Object> attributes) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_HEADER_STRING.name(), headersMap.toString(), attributes);
    }

    /**
     * 设置请求时间
     * @param requestDatetime 请求时间
     * @param attributes      用于补偿的存储对象
     */
    public static void pushRequestDatetime(final String requestDatetime, final Map<String, Object> attributes) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_DATETIME.name(), requestDatetime, attributes);
    }

    /**
     * 设置请求后端URL
     * @param requestRealUrl 后端地址
     * @param attributes     用于补偿的存储对象
     */
    public static void pushRequestRealUrl(final String requestRealUrl, final Map<String, Object> attributes) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_REAL_URL.name(), requestRealUrl, attributes);
    }

    /**
     * 设置请求URL
     * @param requestUrl 请求地址
     * @param attributes 用于补偿的存储对象
     */
    public static void pushRequestUrl(final String requestUrl, final Map<String, Object> attributes) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_URL.name(), requestUrl, attributes);
    }

    /**
     * http 警告日志
     * @param resultVO 响应
     * @param e        异常
     */
    public static void printHttpProviderSendingResponseWarningLog(final ResultVO<?> resultVO, @Nullable final Throwable e) {
        LogProvider.LOGGER.warn("Http service sending response,【{}】{}", resultVO.getCode(), resultVO.getMessage(), e);
        LogProvider.markAlreadySentHttpResponse();
    }

    /**
     * 设置 requestMethod
     * @param requestMethod http方法
     */
    public static void pushRequestMethod(final String requestMethod) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_METHOD.name(), requestMethod.toLowerCase());
    }

    /**
     * 设置 requestHeader
     * @param headersMap http请求头
     */
    public static void pushRequestHeader(final Map<String, List<String>> headersMap) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_HEADER_STRING.name(), headersMap.toString());
    }

    /**
     * 设置请求时间
     * @param requestDatetime 请求时间
     */
    public static void pushRequestDatetime(final String requestDatetime) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_DATETIME.name(), requestDatetime);
    }

    /**
     * 设置请求后端URL
     * @param requestRealUrl 后端地址
     */
    public static void pushRequestRealUrl(final String requestRealUrl) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_REAL_URL.name(), requestRealUrl);
    }

    /**
     * 设置请求URL
     * @param requestUrl 请求地址
     */
    public static void pushRequestUrl(final String requestUrl) {
        LogProvider.pushSomething(FinalVariable.LogEnum.REQUEST_URL.name(), requestUrl);
    }

    /**
     * http 错误日志
     * @param resultVO 响应
     * @param e        异常
     */
    public static void printHttpProviderSendingResponseErrorLog(final ResultVO<?> resultVO, @Nullable final Throwable e) {
        LogProvider.LOGGER.error("Http service sending response,【{}】{}", resultVO.getCode(), resultVO.getMessage(), e);
        LogProvider.markAlreadySentHttpResponse();
    }

    /**
     * 补偿, 从MAP中获取, 并记录到 MDC
     * 用于手动解决线程切换的问题
     * @param attributes map
     */
    public static void compensationSomething(final Map<String, Object> attributes) {
        for (final FinalVariable.LogEnum logEnumKey : FinalVariable.LogEnum.values()) {
            final String key = logEnumKey.name();
            final Object value = attributes.get(key);
            if (null == value) {
                continue;
            }
            LogProvider.pushSomething(key, String.valueOf(value));
        }
    }

    /**
     * 设置 已经发送结果
     */
    public static void markAlreadySentHttpResponse() {
        MDC.put(FinalVariable.LogEnum.SENT_HTTP_RESPONSE.name(), FinalVariable.SENT_HTTP_RESPONSE);
    }

    /**
     * 是否已经发送
     * @return 是否
     */
    public static boolean sentHttpResponse() {
        return FinalVariable.SENT_HTTP_RESPONSE.equals(MDC.get(FinalVariable.LogEnum.SENT_HTTP_RESPONSE.name()));
    }

    /**
     * 记录到 MDC 与 map
     * @param key        键
     * @param value      值
     * @param attributes map
     */
    protected static void pushSomething(final String key, final String value, final Map<String, Object> attributes) {
        MDC.put(key, value);
        attributes.put(key, value);
    }

    /**
     * 记录到 MDC
     * @param key   键
     * @param value 值
     */
    protected static void pushSomething(final String key, final String value) {
        MDC.put(key, value);
    }

    /**
     * 获取当前线程的 MDC 中存在的所有信息
     * 可在 compensationSomething(Map<String, Object> attributes) 中使用
     * @return MDC中的信息
     */
    public static Map<String, Object> pull() {
        final HashMap<String, Object> map = new HashMap<>(16);
        for (final FinalVariable.LogEnum logEnumKey : FinalVariable.LogEnum.values()) {
            final String key = logEnumKey.name();
            final String value = MDC.get(key);
            if (null == value) {
                continue;
            }
            map.put(key, value);
        }
        return map;
    }

    /**
     * 将MDC中的值赋值到 map
     * 并在traceId不存在时赋值 (不会影响MDC)
     * @param attributes map
     */
    public static void push(final Map<String, Object> attributes) {
        final Map<String, Object> everything = LogProvider.pull();
        for (final FinalVariable.LogEnum logEnumKey : FinalVariable.LogEnum.values()) {
            final String key = logEnumKey.name();
            final Object value = everything.get(key);
            if (null == value) {
                continue;
            }
            attributes.put(key, String.valueOf(value));
        }
        // if (attributes.get(FinalVariable.LogEnum.TRACE_ID.name()) == null) {
        // attributes.put(FinalVariable.LogEnum.TRACE_ID.name(), FinalVariable.GENERATE_TRACE_ID.get());
        // }
    }

    /**
     * 清空
     */
    public static void clear(final Map<String, Object> attributes) {
        LogProvider.LOGGER.debug("Cleaning up in MDC && Map");
        for (final FinalVariable.LogEnum value : FinalVariable.LogEnum.values()) {
            attributes.remove(value.name());
            MDC.remove(value.name());
        }
    }

    /**
     * 清空
     */
    public static void clear() {
        LogProvider.LOGGER.debug("Cleaning up in MDC");
        for (final FinalVariable.LogEnum value : FinalVariable.LogEnum.values()) {
            MDC.remove(value.name());
        }
    }

    /**
     * 是否记录请求体, 因为记录请求体会带来额外的性能损失
     * 增加此类判断, 尽量在不记录时避免损失
     * @return 是否记录
     */
    public boolean isLogHttpProviderReceivedRequestBody() {
        return conventionProperties.getHttp().getLog().isProviderReceivedRequestBody();
    }

    /**
     * 是否记录响应体, 因为记录响应体会带来额外的性能损失
     * 增加此类判断, 尽量在不记录时避免损失
     * @return 是否记录
     */
    public boolean isLogHttpProviderSendingResponse() {
        return conventionProperties.getHttp().getLog().isProviderSendingResponse();
    }

    /**
     * 记录日志 服务端接收 http 请求
     */
    public void printHttpProviderReceivedRequestLog() {
        if (conventionProperties.getHttp().getLog().isProviderReceivedRequest()) {
            LogProvider.LOGGER.info("Http service received request, url[{}], real url[{}], header[{}], method[{}].",
                MDC.get(FinalVariable.LogEnum.REQUEST_URL.name()), MDC.get(FinalVariable.LogEnum.REQUEST_REAL_URL.name()),
                MDC.get(FinalVariable.LogEnum.REQUEST_HEADER_STRING.name()), MDC.get(FinalVariable.LogEnum.REQUEST_METHOD.name()));
        }
    }

    /**
     * 同步记录日志 服务端接收 http 请求体
     */
    public void printHttpProviderReceivedRequestBodyLog(final Supplier<String> bodyClosure) {
        if (conventionProperties.getHttp().getLog().isProviderReceivedRequestBody()) {
            LogProvider.LOGGER.info("Http service received request, body[{}].", bodyClosure.get());
        }
    }

    /**
     * 记录日志 服务端发送 http 响应
     * @param bodyClosure 响应内容
     */
    public void printHttpProviderSendingResponseLog(final Supplier<String> bodyClosure) {
        final boolean b = LogProvider.sentHttpResponse();
        // 配置需要记录
        if (conventionProperties.getHttp().getLog().isProviderSendingResponse() && !LogProvider.sentHttpResponse()) {
            LogProvider.LOGGER.info("Http service sending response, url[{}], real url[{}], method[{}], response[{}].",
                MDC.get(FinalVariable.LogEnum.REQUEST_URL.name()), MDC.get(FinalVariable.LogEnum.REQUEST_REAL_URL.name()),
                MDC.get(FinalVariable.LogEnum.REQUEST_METHOD.name()), bodyClosure.get());
            // 标记已发送
            LogProvider.markAlreadySentHttpResponse();
        }
    }

    /**
     * 记录日志 服务端发送 http 响应
     * @param obj 响应内容
     */
    public void printHttpProviderSendingResponseLog(final Object obj) {
        // 配置需要记录
        if (conventionProperties.getHttp().getLog().isProviderSendingResponse() && !LogProvider.sentHttpResponse()) {
            LogProvider.LOGGER.info("Http service sending response, url[{}], real url[{}], method[{}], response[{}].",
                MDC.get(FinalVariable.LogEnum.REQUEST_URL.name()), MDC.get(FinalVariable.LogEnum.REQUEST_REAL_URL.name()),
                MDC.get(FinalVariable.LogEnum.REQUEST_METHOD.name()), obj.toString());
            // 标记已发送
            LogProvider.markAlreadySentHttpResponse();
        }
    }

    /**
     * 记录日志 客户端发送 http 请求, 重试失败
     * @param retryTime 已经进行重试次数
     */
    public void printHttpConsumerSendingRequestRetryGiveUpLog(final int retryTime) {
        if (conventionProperties.getHttp().getLog().isConsumerSendingRequest()) {
            LogProvider.LOGGER.info("Http client sending request, it has been retried {} times, now give up.", retryTime);
        }
    }

    /**
     * 记录日志 客户端发送 http 请求, 重试日志
     * @param retryTime    当前第几次重试
     * @param retryMaxTime 最大重试次数请求
     */
    public void printHttpConsumerSendingRequestRetryLog(final int retryTime, final int retryMaxTime) {
        if (conventionProperties.getHttp().getLog().isConsumerSendingRequest()) {
            LogProvider.LOGGER.info("Http client sending request, try again for the {}th, up to {} times.", retryTime, retryMaxTime);
        }
    }

    /**
     * 记录日志 客户端发送 http 请求
     * @param request    请求
     * @param bodyString 请求体日志字符串
     */
    public void printHttpConsumerSendingRequestLog(final Request request, final String bodyString) {
        if (conventionProperties.getHttp().getLog().isConsumerSendingRequest()) {
            LogProvider.LOGGER.info("Http client sending request, url[{}], method[{}], headers[{}], request body[{}].",
                request.url().url().toString(),
                request.method(), request.headers().toMultimap(), bodyString);
        }
    }

    /**
     * 记录日志 客户端接收 http 响应
     * @param request            请求
     * @param response           响应
     * @param responseBodyString 响应体字符串
     */
    public void printHttpConsumerReceivedResponseLog(final Request request, final Response response, final String responseBodyString) {
        if (conventionProperties.getHttp().getLog().isConsumerReceivedResponse()) {
            LogProvider.LOGGER.info("Http client received response, request[{}], response[{}], response header[{}], response body[{}].",
                request.toString(),
                response.toString(), response.headers().toMultimap(), responseBodyString);
        }
    }

    /**
     * 记录日志 服务端接收 dubbo 请求
     */
    public void printDubboProviderReceivedRequestLog(final String url, final Object[] arguments) {
        if (conventionProperties.getDubbo().getLog().isProviderReceivedRequest()) {
            LogProvider.LOGGER.info("Dubbo service received request, url[{}], request [{}].", url, arguments);
        }
    }

    /**
     * 记录日志 服务端发送 dubbo 响应
     */
    public void printDubboProviderSendingResponseLog(final String url, final Object result, final Throwable e) {
        if (conventionProperties.getDubbo().getLog().isProviderSendingResponse()) {
            LogProvider.LOGGER.info("Dubbo service sending response, url[{}], response [{}]", url, result, e);
        }
    }

    /**
     * 记录日志 客户端发送 dubbo 请求
     */
    public void printDubboConsumerSendingRequestLog(final String url, final Object[] arguments) {
        if (conventionProperties.getDubbo().getLog().isConsumerSendingRequest()) {
            LogProvider.LOGGER.info("Dubbo client sending request, url[{}], request [{}].", url, arguments);
        }
    }

    /**
     * 记录日志 客户端接收 dubbo 响应
     */
    public void printDubboConsumerReceivedResponseLog(final String url, final Object result, final Throwable e) {
        if (conventionProperties.getDubbo().getLog().isConsumerReceivedResponse()) {
            LogProvider.LOGGER.info("Dubbo client received response, url[{}], response [{}]", url, result, e);
        }
    }

    /**
     * 记录日志 dubbo 异常
     */
    public static void printDubboErrorLog(final String url, final Throwable e) {
        LogProvider.LOGGER.error("Dubbo error, url[{}]", url, e);
    }

    /**
     * 记录日志 Swagger 可用
     */
    public void printSwaggerIsAvailable() {
        if (swaggerEnable) {
            LogProvider.LOGGER.info("Swagger is available, can see http://127.0.0.1:{}{}/swagger-ui/index.html", serverPort, swaggerBaseUrl);
        }
    }
}

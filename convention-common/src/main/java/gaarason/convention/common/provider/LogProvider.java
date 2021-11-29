package gaarason.convention.common.provider;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.autoconfigure.ConventionProperties;
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
                    try {
                        localIns = LogProvider.instance = SpringUtils.getBean("logProvider");
                    } catch (Throwable e) {
                        LOGGER.warn("Not found LogProvider instance in Spring.", e);
                        localIns = LogProvider.instance = new LogProvider();
                        localIns.conventionProperties = new ConventionProperties();
                        localIns.serverPort = 8080;
                        localIns.swaggerBaseUrl = "";
                        localIns.swaggerEnable = false;
                    }
                }
            }
        }
        return localIns;
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
     * http 错误日志
     * @param resultVO 响应
     * @param e        异常
     */
    public static void printHttpProviderSendingResponseErrorLog(final ResultVO<?> resultVO, @Nullable final Throwable e) {
        LogProvider.LOGGER.error("Http service sending response,【{}】{}", resultVO.getCode(), resultVO.getMessage(), e);
        LogProvider.markAlreadySentHttpResponse();
    }

    /**
     * 设置 已经发送结果
     */
    public static void markAlreadySentHttpResponse() {
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.SENT_HTTP_RESPONSE, FinalVariable.SENT_HTTP_RESPONSE);
    }

    /**
     * 是否已经发送
     * @return 是否
     */
    public static boolean sentHttpResponse() {
        return FinalVariable.SENT_HTTP_RESPONSE.equals(ChainProvider.get(ChainProvider.CanNotCrossProcessKey.SENT_HTTP_RESPONSE));
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
     * 清空
     */
    public static void clear(final Map<String, Object> attributes) {
        LogProvider.LOGGER.debug("Cleaning up in MDC && Map");
        ChainProvider.clear();
        ChainProvider.clear(attributes);
    }

    /**
     * 清空
     */
    public static void clear() {
        LogProvider.LOGGER.debug("Cleaning up in MDC");
        ChainProvider.clear();
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
                ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_URL),
                ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_REAL_URL),
                ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_HEADER_STRING),
                ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_METHOD));
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
        // 配置需要记录
        if (conventionProperties.getHttp().getLog().isProviderSendingResponse() && !LogProvider.sentHttpResponse()) {
            LogProvider.LOGGER.info("Http service sending response, url[{}], real url[{}], method[{}], response[{}].",
                ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_URL),
                ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_REAL_URL),
                ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_METHOD),
                bodyClosure.get());
            // 标记已发送
            LogProvider.markAlreadySentHttpResponse();
        }
    }

    /**
     * 记录日志 服务端发送 http 响应
     * @param obj 响应内容
     */
    public void printHttpProviderSendingResponseLog(final Object obj) {
        printHttpProviderSendingResponseLog(obj::toString);
    }


    /**
     * 记录日志 客户端发送 http 请求, 重试失败
     * @param enable    是否启用, DEFAULT 表示使用全局配置
     * @param retryTime 已经进行的重试次数
     */
    public void printHttpConsumerSendingRequestRetryGiveUpLog(FinalVariable.Bool enable, final int retryTime) {
        if (enable == FinalVariable.Bool.TRUE || (enable == FinalVariable.Bool.DEFAULT && conventionProperties.getHttp().getLog().isConsumerSendingRequestRetry())) {
            LogProvider.LOGGER.info("Http client sending request, it has been retried {} times, now give up.", retryTime);
        }
    }

    /**
     * 记录日志 客户端发送 http 请求, 重试日志
     * @param enable       是否启用, DEFAULT 表示使用全局配置
     * @param retryTime    当前第几次重试
     * @param retryMaxTime 最大重试次数请求
     */
    public void printHttpConsumerSendingRequestRetryLog(FinalVariable.Bool enable, final int retryTime, final int retryMaxTime) {
        if (enable == FinalVariable.Bool.TRUE || (enable == FinalVariable.Bool.DEFAULT && conventionProperties.getHttp().getLog().isConsumerSendingRequest())) {
            LogProvider.LOGGER.info("Http client sending request, try again for the {}th, up to {} times.", retryTime, retryMaxTime);
        }
    }

    /**
     * 记录日志 客户端发送 http 请求
     * @param enable     是否启用, DEFAULT 表示使用全局配置
     * @param request    请求
     * @param bodyString 请求体日志字符串
     */
    public void printHttpConsumerSendingRequestLog(FinalVariable.Bool enable, Request request, Supplier<String> bodyString) {
        if (enable == FinalVariable.Bool.TRUE || (enable == FinalVariable.Bool.DEFAULT && conventionProperties.getHttp().getLog().isConsumerSendingRequest())) {
            LogProvider.LOGGER.info("Http client sending request, url[{}], method[{}], headers[{}], request body[{}].",
                request.url().url().toString(),
                request.method(), request.headers().toMultimap(), bodyString.get());
        }
    }

    /**
     * 是否 记录日志 客户端接收 http 响应
     * @param enable 是否启用, DEFAULT 表示使用全局配置
     * @return 是否
     */
    public boolean isPrintHttpConsumerReceivedResponseLog(FinalVariable.Bool enable) {
        return enable == FinalVariable.Bool.TRUE || (enable == FinalVariable.Bool.DEFAULT && conventionProperties.getHttp().getLog().isConsumerReceivedResponse());
    }

    /**
     * 记录日志 客户端接收 http 响应
     * @param enable             是否启用, DEFAULT 表示使用全局配置
     * @param request            请求
     * @param response           响应
     * @param responseBodyString 响应体字符串
     */
    public void printHttpConsumerReceivedResponseLog(FinalVariable.Bool enable, Request request, Response response,
        Supplier<String> responseBodyString) {
        if (isPrintHttpConsumerReceivedResponseLog(enable)) {
            LogProvider.LOGGER.info("Http client received response, request[{}], response[{}], response header[{}], response body[{}].",
                request.toString(),
                response.toString(), response.headers().toMultimap(), responseBodyString.get());
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

package gaarason.convention.common.util;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.provider.LogProvider;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;

/**
 * Http请求工具
 * @author xt
 */
public class HttpUtils {

    private static final int TIMEOUT = 6;

    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    private static final String HTTP_HEADER_CONNECTION = "Connection";

    private static final String HTTP_HEADER_ACCEPT_ENCODING = "Accept-Encoding";

    private static final String HTTP_HEADER_ACCEPT_CHARSET = "Accept-Charset";

    private static final OkHttpClient DEFAULT_HTTP_CLIENT;

    static {
        /**
         * retryOnConnectionFailure(false) 失败重试使用手动实现,避免对下游的重复请求不好定位
         */
        DEFAULT_HTTP_CLIENT = new OkHttpClient.Builder().retryOnConnectionFailure(false).connectTimeout(HttpUtils.TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(HttpUtils.TIMEOUT, TimeUnit.SECONDS).readTimeout(HttpUtils.TIMEOUT, TimeUnit.SECONDS).build();
    }

    /**
     * 获取请求构造对象
     * @return 请求构造对象
     */
    public static RequestBuilder request() {
        return new RequestBuilder(HttpUtils.DEFAULT_HTTP_CLIENT);
    }

    /**
     * 获取请求构造对象
     * @param okHttpClient 自定义的请求客户端
     * @return 请求构造对象
     */
    public static RequestBuilder request(OkHttpClient okHttpClient) {
        return new RequestBuilder(okHttpClient);
    }

    /**
     * 由 http method 判断是否存在请求体
     * @param method http method
     * @return 是否可以存在请求体
     */
    public static boolean isMethodCanWithBody(FinalVariable.Http.Method method) {
        return FinalVariable.Http.METHOD_WITH_BODY.contains(method);
    }

    /**
     * 请求构造器
     */
    public static class RequestBuilder {

        private final Request.Builder builder = new Request.Builder();

        private final OkHttpClient httpClient;

        private FinalVariable.Http.Method method = FinalVariable.Http.Method.GET;

        private final Headers.Builder headers = new Headers.Builder();

        private final FormBody.Builder formBody = new FormBody.Builder();

        private final MultipartBody.Builder multipartBody = new MultipartBody.Builder();

        private String url = "";

        private final Map<String, Object> mapQuery = new HashMap<>(16);

        private final Map<String, Object> mapBody = new HashMap<>(16);

        private byte[] bytesBody = new byte[0];

        private Supplier<String> logBodyCache = () -> "";

        /**
         * 请求日志记录(DEFAULT 表示使用全局配置)
         */
        private FinalVariable.Bool clientSendingRequestLogEnable = FinalVariable.Bool.DEFAULT;

        /**
         * 请求重试日志记录(DEFAULT 表示使用全局配置)
         */
        private FinalVariable.Bool clientSendingRequestRetryLogEnable = FinalVariable.Bool.DEFAULT;

        /**
         * 响应日志记录(DEFAULT 表示使用全局配置)
         */
        private FinalVariable.Bool clientReceivedResponseLogEnable = FinalVariable.Bool.DEFAULT;

        @Nullable
        private MediaType mediaType;

        private final LogProvider logProvider;

        /**
         * 只能选用一种 body 格式
         */
        private BodyType bodyType = BodyType.UNDEFINED;

        /**
         * 是否接收gzip, 并在响应格式为gzip时进行解码
         */
        private boolean acceptEncodingGzip = false;

        /**
         * 是否传递通过请求头传递 上下文信息
         * 手动设置的优先级更高
         */
        private boolean chainTransferWithHeader = true;

        /**
         * 是否传递通过请求url传递 上下文信息
         * 手动设置的优先级更高
         */
        private boolean chainTransferWithUrl = false;

        /**
         * 重试相关
         */
        private HttpRetryOption httpRetryOption = new HttpRetryOption();

        public RequestBuilder(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            logProvider = LogProvider.getInstance();

            setHeader(HTTP_HEADER_ACCEPT_CHARSET, "UTF-8");
            setHeader(HTTP_HEADER_CONNECTION, "keep-alive");
//            setHeader(HTTP_HEADER_CONNECTION, "close");
            setHeader(HTTP_HEADER_ACCEPT_ENCODING, "");
        }

        public RequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RequestBuilder url(URL url) {
            return url(url.toString());
        }

        /**
         * url 发下 query 参数
         * @param url       url
         * @param paramsMap 参数map
         * @return 请求构造器
         */
        public RequestBuilder url(String url, Map<String, Object> paramsMap) {
            url(url);
            return setQuery(paramsMap);
        }

        public RequestBuilder url(HttpUrl url) {
            return url(url.toString());
        }

        public RequestBuilder setQuery(String name, Object value) {
            mapQuery.put(name, value);
            return this;
        }

        public RequestBuilder setQuery(Map<String, Object> paramsMap) {
            mapQuery.putAll(paramsMap);
            return this;
        }

        /**
         * 设置http请求方法, 不调用则默认get
         * @param method http 方法
         * @return 请求构造器
         */
        public RequestBuilder setMethod(FinalVariable.Http.Method method) {
            this.method = method;
            return this;
        }

        public RequestBuilder addHeader(String line) {
            headers.add(line);
            return this;
        }

        public RequestBuilder addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public RequestBuilder addHeader(String name, Date date) {
            headers.add(name, date);
            return this;
        }

        public RequestBuilder addHeader(String name, Instant value) {
            headers.add(name, value);
            return this;
        }

        public RequestBuilder addHeader(Headers paramHeaders) {
            headers.addAll(paramHeaders);
            return this;
        }

        public RequestBuilder setHeader(String name, String value) {
            headers.set(name, value);
            return this;
        }

        public RequestBuilder setHeaderIgnoreNullOrEmpty(String name, @Nullable String value) {
            return ObjectUtils.isEmpty(value) ? this : setHeader(name, value);
        }

        public RequestBuilder setHeader(String name, Date date) {
            headers.set(name, date);
            return this;
        }

        public RequestBuilder setHeader(String name, Instant value) {
            headers.set(name, value);
            return this;
        }

        /**
         * 设置链路上下文
         * @param name  键
         * @param value 值
         * @return 请求构造器
         */
        public RequestBuilder setHeader(ChainProvider.CanCrossProcessKey name, String value) {
            headers.set(name.getHttpHeaderKey(), value);
            return this;
        }

        /**
         * 设置链路上下文(忽略值为空or null的情况)
         * @param name  键
         * @param value 值
         * @return 请求构造器
         */
        public RequestBuilder setHeaderIgnoreNullOrEmpty(ChainProvider.CanCrossProcessKey name, @Nullable String value) {
            return ObjectUtils.isEmpty(value) ? this : setHeader(name, value);
        }

        /**
         * 移除请求头
         * @param name 键
         * @return 请求构造器
         */
        public RequestBuilder removeHeader(String name) {
            headers.removeAll(name);
            return this;
        }

        /**
         * 是否开启请求日志(目前仅支持传递false时关闭)
         * @param bool 是否
         * @return 请求构造器
         */
        public RequestBuilder setClientSendingRequestLogEnable(FinalVariable.Bool bool) {
            clientSendingRequestLogEnable = bool;
            return this;
        }

        /**
         * 是否开启请求重试日志(目前仅支持传递false时关闭)
         * @param bool 是否
         * @return 请求构造器
         */
        public RequestBuilder setClientSendingRequestRetryLogEnable(FinalVariable.Bool bool) {
            clientSendingRequestRetryLogEnable = bool;
            return this;
        }

        /**
         * 是否开启响应日志(目前仅支持传递false时关闭)
         * @param bool 是否
         * @return 请求构造器
         */
        public RequestBuilder setClientReceivedResponseLogEnable(FinalVariable.Bool bool) {
            clientReceivedResponseLogEnable = bool;
            return this;
        }

        /**
         * 重试相关配置
         * @param option 配置
         * @return 请求构造器
         */
        public RequestBuilder setHttpRetryOption(HttpRetryOption option) {
            httpRetryOption = ObjectUtils.deepCopy(option);
            return this;
        }

        /**
         * 设置是否打开 保持链接
         * @param open 开/关
         * @return 请求构造器
         */
        public RequestBuilder setKeepAlive(boolean open) {
            return setHeader(HTTP_HEADER_CONNECTION, open ? "keep-alive" : "close");
        }

        /**
         * 设置是否自动解码gzip
         * @param deal 开/关
         * @return 请求构造器
         */
        public RequestBuilder setAcceptEncodingGzip(boolean deal) {
            acceptEncodingGzip = deal;
            setHeader(HTTP_HEADER_ACCEPT_ENCODING, deal ? "gzip" : "");
            return this;
        }

        /**
         * 是否传递上下文信息
         * @param deal 开/关
         * @return 请求构造器
         */
        public RequestBuilder setChainTransferWithHeader(boolean deal) {
            chainTransferWithHeader = deal;
            return this;
        }

        /**
         * 是否将 上下文信息 附加到 url参数上
         * @param deal 开/关
         * @return 请求构造器
         */
        public RequestBuilder setChainCodeWithUrl(boolean deal) {
            chainTransferWithUrl = deal;
            return this;
        }

        /**
         * 等价于 postman 的 x-www-form-urlencoded
         * @param name  键
         * @param value 值
         * @return 请求构造器
         */
        public RequestBuilder addFormBody(String name, String value) {
            lockBodyType(BodyType.FORM);
            formBody.add(name, value);
            mapBody.put(name, value);
            return this;
        }

        /**
         * 等价于 postman 的 x-www-form-urlencoded
         * @param name  键
         * @param value 值
         * @return 请求构造器
         */
        public RequestBuilder addEncodedFormBody(String name, String value) {
            lockBodyType(BodyType.FORM);
            formBody.addEncoded(name, value);
            mapBody.put(name, value);
            return this;
        }

        /**
         * 等价于 postman 的 form-data
         * @param name  键
         * @param value 值
         * @return 请求构造器
         */
        public RequestBuilder addMultipartBodyFormDataPart(String name, String value) {
            lockBodyType(BodyType.MULTIPART);
            multipartBody.addFormDataPart(name, value);
            mapBody.put(name, value);
            return this;
        }

        /**
         * 等价于 postman 的 form-data
         * @param name 键
         * @param file 文件对象
         * @return 请求构造器
         */
        public RequestBuilder addMultipartBodyFormDataPart(String name, File file) {
            lockBodyType(BodyType.MULTIPART);
            RequestBody requestBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
            multipartBody.addFormDataPart(name, file.getName(), requestBody);
            mapBody.put(name, file.getName());
            return this;
        }

        /**
         * 等价于 postman 的 form-data
         * @param name     键
         * @param filename 文件名
         * @param content  文件内容
         * @return 请求构造器
         */
        public RequestBuilder addMultipartBodyFormDataPart(String name, String filename, byte[] content) {
            lockBodyType(BodyType.MULTIPART);
            RequestBody requestBody = RequestBody.create(content, MediaType.parse("application/octet-stream"));
            multipartBody.addFormDataPart(name, filename, requestBody);
            mapBody.put(name, filename);
            return this;
        }

        public RequestBuilder setJsonBody(String jsonString) {
            lockBodyType(BodyType.JSON);
            bytesBody = jsonString.getBytes(StandardCharsets.UTF_8);
            return setHeader(HttpUtils.HTTP_HEADER_CONTENT_TYPE, "application/json;charset=utf-8");
        }

        public RequestBuilder setJsonBody(Serializable value) {
            lockBodyType(BodyType.JSON);
            bytesBody = JsonUtils.objectToJson(value).getBytes(StandardCharsets.UTF_8);
            return setHeader(HttpUtils.HTTP_HEADER_CONTENT_TYPE, "application/json;charset=utf-8");
        }

        public RequestBuilder setXmlBody(String value) {
            lockBodyType(BodyType.XML);
            bytesBody = value.getBytes(StandardCharsets.UTF_8);
            return setHeader(HttpUtils.HTTP_HEADER_CONTENT_TYPE, "application/xml;charset=utf-8");
        }

        public RequestBuilder setBody(String value, @Nullable MediaType mediaType) {
            return setBody(value.getBytes(StandardCharsets.UTF_8), mediaType);
        }

        public RequestBuilder setBody(byte[] bytes, @Nullable MediaType mediaType) {
            lockBodyType(BodyType.MEDIA_TYPE);
            bytesBody = bytes;
            this.mediaType = mediaType;
            return this;
        }

        /**
         * 执行同步请求
         * @param maxRetryTime 最大重试次数
         * @return 响应
         */
        public HttpResult exec(int maxRetryTime) {
            // 请求构造
            Request request = requestBuild();
            httpRetryOption.maxRetryTime = maxRetryTime;
            // 执行同步请求
            return exec(request);
        }

        /**
         * 执行同步请求
         * @return 响应
         */
        public HttpResult exec() {
            // 请求构造
            Request request = requestBuild();
            // 执行同步请求
            return exec(request);
        }

        /**
         * 执行同步请求
         * 适用于外部手动构造的 Request 对象的使用, 之前的链式方法调用均会被忽略
         * @param request      请求构造对象
         * @param maxRetryTime 最大重试次数
         * @return 响应
         * @see HttpUtils setHttpRetryOption(HttpRetryOption httpRetryOption)
         */
        public HttpResult exec(Request request, int maxRetryTime) {
            httpRetryOption.maxRetryTime = maxRetryTime;
            return exec(request);
        }

        /**
         * 执行同步请求
         * 适用于外部手动构造的 Request 对象的使用, 之前的链式方法调用均会被忽略
         * @param request 请求构造对象
         * @return 响应
         */
        public HttpResult exec(Request request) {
            try {
                // 请求日志记录
                logProvider.printHttpConsumerSendingRequestLog(clientSendingRequestLogEnable, request, logBodyCache);

                // 执行请求与网络错误重试
                Response response = sync(request);

                // 响应解析与包装
                return analysisResponse(request, response);

            } catch (Throwable e) {
                throw new BusinessException(StatusCode.HTTP_REQUEST_API_ERROR, map -> map.put("request", request.toString()), e);
            }
        }


        /**
         * 执行异步请求
         * @param maxRetryTime      最大重试次数
         * @param callbackInterface 结果回调
         */
        public void exec(int maxRetryTime, CallbackInterface callbackInterface) {
            // 请求构造
            Request request = requestBuild();
            httpRetryOption.maxRetryTime = maxRetryTime;

            // 执行异步请求
            exec(request, callbackInterface);
        }

        /**
         * 执行异步请求
         * @param callbackInterface 结果回调
         */
        public void exec(CallbackInterface callbackInterface) {
            // 请求构造
            Request request = requestBuild();

            // 执行异步请求
            exec(request, callbackInterface);
        }

        /**
         * 执行异步请求
         * 适用于外部手动构造的 Request 对象的使用, 之前的链式方法调用均会被忽略
         * @param request           请求构造对象
         * @param maxRetryTime      最大重试次数
         * @param callbackInterface 结果回调
         */
        public void exec(Request request, int maxRetryTime, CallbackInterface callbackInterface) {
            httpRetryOption.maxRetryTime = maxRetryTime;
            exec(request, callbackInterface);
        }

        /**
         * 执行异步请求
         * 适用于外部手动构造的 Request 对象的使用, 之前的链式方法调用均会被忽略
         * @param request           请求构造对象
         * @param callbackInterface 结果回调
         */
        public void exec(Request request, CallbackInterface callbackInterface) {
            // 请求日志记录
            logProvider.printHttpConsumerSendingRequestLog(clientSendingRequestLogEnable, request, logBodyCache);

            // 执行异步请求与网络错误重试
            async(request, 1, callbackInterface);
        }

        /**
         * 请求构造
         * @return 请求
         */
        protected Request requestBuild() {

            urlBuild();

            RequestBody body = bodyBuild();

            switch (method) {
                case HEAD:
                    builder.head().build();
                    break;
                case GET:
                    builder.get().build();
                    break;
                case POST:
                    builder.post(body).build();
                    break;
                case PUT:
                    builder.put(body).build();
                    break;
                case DELETE:
                    builder.delete(body).build();
                    break;
                case PATCH:
                    builder.patch(body).build();
                    break;
                default:
                    throw new BusinessException(StatusCode.CALL_NOT_SUPPORTED);
            }
            return builder.headers(headerBuild()).build();
        }

        /**
         * 请求url构造
         */
        protected void urlBuild() {
            // 设置上下文到 url 上, 且使用下划线风格
            if (chainTransferWithUrl) {
                for (Map.Entry<String, String> entry : ChainProvider.get(ChainProvider.ChainType.CAN_CROSS_PROCESS).entrySet()) {
                    // 只有不存在时才设置
                    mapQuery.putIfAbsent(entry.getKey().toLowerCase().replace("-", "_"), entry.getValue());
                }
            }
            String questString = StringUtils.mapToQuerySearch(mapQuery);
            builder.url(questString.isEmpty() ? url : (url.contains("?") ? url + "&" + questString : url + "?" + questString));
        }

        /**
         * 请求体构造
         * @return 请求体
         */
        protected RequestBody bodyBuild() {
            RequestBody body;
            switch (bodyType) {
                case FORM:
                    body = formBody.build();
                    logBodyCache = mapBody::toString;
                    break;
                case MULTIPART:
                    multipartBody.setType(MultipartBody.FORM);
                    body = multipartBody.build();
                    logBodyCache = mapBody::toString;
                    break;
                case MEDIA_TYPE:
                    body = RequestBody.create(bytesBody, mediaType);
                    logBodyCache = () -> new String(bytesBody);
                    break;
                case XML:
                case JSON:
                default:
                    body = RequestBody.create(bytesBody);
                    logBodyCache = () -> new String(bytesBody);
            }
            return body;
        }

        /**
         * 请求头构造
         * @return 请求头
         */
        protected Headers headerBuild() {
            if (chainTransferWithHeader) {
                for (Map.Entry<String, String> entry : ChainProvider.get(ChainProvider.ChainType.CAN_CROSS_PROCESS).entrySet()) {
                    // 头已经目标key存在，说明手动设置过了，优先级更高。
                    String value = headers.get(entry.getKey());
                    // 只有头中不存在时，才设置
                    if (value == null) {
                        setHeader(entry.getKey(), entry.getValue());
                    }
                }
            }
            return headers.build();
        }

        /**
         * 执行同步请求
         * @param request 请求
         * @return 响应
         * @throws Throwable 原始异常
         */
        protected Response sync(Request request) throws Throwable {
            for (int currentRetryTime = 1; currentRetryTime <= httpRetryOption.maxRetryTime + 1; currentRetryTime++) {
                try {
                    return httpClient.newCall(request).execute();
                } catch (Throwable e) {
                    retryHoldOn(currentRetryTime, () -> {
                    }, () -> {
                        throw e;
                    });
                }
            }
            throw new BusinessException(StatusCode.HTTP_REQUEST_API_RETRY_TIME_ERROR);
        }

        /**
         * 执行异步请求
         * @param request           请求
         * @param currentRetryTime  当前重试次数
         * @param callbackInterface 结果回调
         */
        protected void async(Request request, int currentRetryTime, CallbackInterface callbackInterface) {

            int nextRetryTime = currentRetryTime + 1;

            Map<String, Object> mdcInfo = new HashMap<>(16);
            // 获取MDC中的信息
            ChainProvider.initDataMap(mdcInfo);

            httpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // 将指定map中的所有有效"键"与"值"，赋值到MDC
                    ChainProvider.initMDC(mdcInfo);

                    try {
                        retryHoldOn(currentRetryTime,
                            () -> async(request, nextRetryTime, callbackInterface),
                            () -> callbackInterface.call(null,
                                new BusinessException(StatusCode.HTTP_REQUEST_API_ERROR, map -> map.put("request", request.toString()), e)));
                    } catch (Throwable throwable) {
                        // 此处绝不应该出现异常
                        throw new BusinessException(e);
                    } finally {
                        // 清除
                        ChainProvider.clear();
                    }
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    // 将指定map中的所有有效"键"与"值"，赋值到MDC
                    ChainProvider.initMDC(mdcInfo);
                    callbackInterface.call(analysisResponse(request, response), null);
                    // 清除
                    ChainProvider.clear();
                }
            });
        }

        /**
         * 分析并组装响应
         * @param request  请求
         * @param response 响应
         * @return 包装后的响应体
         */
        protected HttpResult analysisResponse(Request request, Response response) {
            // 响应对象
            HttpResult httpResult = new HttpResult(request, response, acceptEncodingGzip);
            // 对于单个请求是否开启响应日志
            logProvider.printHttpConsumerReceivedResponseLog(clientReceivedResponseLogEnable, request, response, httpResult::getBodyString);

            return httpResult;
        }

        /**
         * body类型检测
         * @param targetType body类型
         */
        protected synchronized void lockBodyType(BodyType targetType) {
            if (bodyType == BodyType.UNDEFINED) {
                bodyType = targetType;
            } else if (!bodyType.equals(targetType)) {
                throw new BusinessException(StatusCode.HTTP_REQUEST_BODY_TYPE_VALIDATION_FAIL);
            }
        }

        /**
         * 重试控制
         * @param currentRetryTime 当前是第几次重试
         * @param runnable         重试成功后执行
         * @param failRunnable     重试失败后执行
         */
        protected void retryHoldOn(int currentRetryTime, RunnableWithThrowableInterface runnable,
            RunnableWithThrowableInterface failRunnable) throws Throwable {
            // 没有重试次数
            if (currentRetryTime > httpRetryOption.maxRetryTime) {
                // 重试失败日志记录
                logProvider.printHttpConsumerSendingRequestRetryGiveUpLog(clientSendingRequestRetryLogEnable, currentRetryTime - 1);
                failRunnable.run();
                return;
            }

            // 延迟重试
            try {
                Thread.sleep(calculationInterval(currentRetryTime, httpRetryOption.retryAfter, httpRetryOption.incrementMultiple,
                    httpRetryOption.basicInterval, httpRetryOption.maxInterval));
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                // 重试失败日志记录
                logProvider.printHttpConsumerSendingRequestRetryGiveUpLog(clientSendingRequestRetryLogEnable, currentRetryTime - 1);
                failRunnable.run();
                return;
            }

            // 重试日志记录
            logProvider.printHttpConsumerSendingRequestRetryLog(clientSendingRequestRetryLogEnable, currentRetryTime, httpRetryOption.maxRetryTime);

            runnable.run();
        }
    }

    /**
     * 计算间隔时间
     * @param currentRetryTime  当前第几次重试
     * @param retryAfter        首次重试时, 间隔时间 单位ms
     * @param incrementMultiple 每次重试间隔时间 递增倍数
     * @param basicInterval     重试基本间隔
     * @param maxInterval       最大重试间隔 0表示不限制
     * @return 当前的重试间隔
     */
    public static long calculationInterval(int currentRetryTime, long retryAfter, double incrementMultiple, long basicInterval, long maxInterval) {
        long currentInterval = (currentRetryTime == 1 && retryAfter > 0)
            ? retryAfter
            : (long) (basicInterval * Math.pow(incrementMultiple, currentRetryTime - 1));
        return maxInterval <= 0 ? currentInterval : Math.min(currentInterval, maxInterval);
    }

    /**
     * 是否响应体是否被gzip压缩
     * @param responseHeaderMap 响应头
     * @return 是否被gzip压缩
     */
    public static boolean containGzipInHeader(Map<String, List<String>> responseHeaderMap) {
        // org.springframework.http.HttpHeaders.CONTENT_ENCODING
        List<String> list = responseHeaderMap.get("Content-Encoding");
        if (list == null || list.isEmpty()) {
            return false;
        }
        return list.get(0).toLowerCase(Locale.ROOT).contains("gzip");
    }

    /**
     * 将gzip响应体解码
     * @param content 原响应体
     * @return gzip解码后的的响应体
     */
    public static String gzipDecode(byte[] content) {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(content), content.length)) {
            byte[] buffer = new byte[1024];
            int n;
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                while ((n = gzipInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, n);
                }
                return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
            }
        } catch (IOException e) {
            throw new BusinessException("Failed to decode GZIP to string", e);
        }
    }

    /**
     * 将gzip响应体解码
     * @param content           原响应体
     * @param responseHeaderMap 响应头
     * @return gzip解码后的的响应体
     */
    public static String gzipDecode(byte[] content, Map<String, List<String>> responseHeaderMap) {
        if (HttpUtils.containGzipInHeader(responseHeaderMap)) {
            return HttpUtils.gzipDecode(content);
        }
        return new String(content);
    }


    /**
     * 回调
     */
    @FunctionalInterface
    public interface CallbackInterface {

        /**
         * 回调通知
         * httpResult 与 throwable 其中必有一个为 null
         * @param httpResult 响应结果
         * @param throwable  请求异常
         */
        void call(@Nullable HttpResult httpResult, @Nullable BusinessException throwable);
    }

    /**
     * 回调
     */
    @FunctionalInterface
    public interface RunnableWithThrowableInterface {

        /**
         * 执行
         * @throws Throwable 异常
         */
        void run() throws Throwable;
    }

    /**
     * 请求体类型
     */
    enum BodyType {
        /**
         * 未定义
         */
        UNDEFINED,

        /**
         * xml
         */
        XML,

        /**
         * json
         */
        JSON,

        /**
         * form
         */
        FORM,

        /**
         * url encode
         */
        MULTIPART,

        /**
         * media type
         */
        MEDIA_TYPE
    }

    /**
     * http请求, 重试配置
     */
    public static class HttpRetryOption implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 最大重试次数
         */
        private int maxRetryTime = 0;

        /**
         * 首次重试时, 间隔时间 单位ms
         */
        private long retryAfter = 0L;

        /**
         * 基础重试间隔时间 单位ms
         */
        private long basicInterval = 10L;

        /**
         * 每次重试最大间隔时间(小于等于0时, 表示不限制) 单位ms
         */
        private long maxInterval = 0L;

        /**
         * 每次重试间隔时间 递增倍数
         */
        private double incrementMultiple = 1.5;


        public int getMaxRetryTime() {
            return maxRetryTime;
        }

        public void setMaxRetryTime(int maxRetryTime) {
            this.maxRetryTime = maxRetryTime;
        }

        public long getRetryAfter() {
            return retryAfter;
        }

        public void setRetryAfter(long retryAfter) {
            this.retryAfter = retryAfter;
        }

        public long getBasicInterval() {
            return basicInterval;
        }

        public void setBasicInterval(long basicInterval) {
            this.basicInterval = basicInterval;
        }

        public long getMaxInterval() {
            return maxInterval;
        }

        public void setMaxInterval(long maxInterval) {
            this.maxInterval = maxInterval;
        }

        public double getIncrementMultiple() {
            return incrementMultiple;
        }

        public void setIncrementMultiple(double incrementMultiple) {
            this.incrementMultiple = incrementMultiple;
        }
    }

    /**
     * http响应
     */
    public static class HttpResult implements Serializable {

        private static final long serialVersionUID = 1L;

        private final transient Request request;

        private final transient Response response;

        private final transient Headers headers;

        private final boolean acceptEncodingGzip;

        @Nullable
        private String bodyString;

        public HttpResult(Request request, Response response, boolean acceptEncodingGzip) {
            this.request = request;
            this.response = response;
            this.acceptEncodingGzip = acceptEncodingGzip;
            this.headers = request.headers();
        }

        public Request getRequest() {
            return request;
        }

        public Response getResponse() {
            return response;
        }

        public Headers getHeaders() {
            return headers;
        }

        public String getBodyString() {
            if (bodyString == null) {
                bodyString = FinalVariable.EMPTY_STRING;
                try (ResponseBody body = response.body()) {
                    if (body != null) {
                        bodyString = acceptEncodingGzip ? HttpUtils.gzipDecode(body.bytes(), headers.toMultimap()) : body.string();
                    }
                } catch (Throwable e) {
                    throw new BusinessException(StatusCode.HTTP_REQUEST_API_ERROR, map -> map.put("request", request.toString()), e);
                }
            }
            return bodyString;
        }

        @Override
        public String toString() {
            return "HttpResult{" +
                "request=" + request +
                ", response=" + response +
                ", headers=" + headers +
                ", acceptEncodingGzip=" + acceptEncodingGzip +
                ", bodyString='" + getBodyString() + '\'' +
                '}';
        }
    }
}

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

        private String stringBody = "";

        @Nullable
        private MediaType mediaType;

        @Nullable
        private String logBodyCache;

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

        public RequestBuilder(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            logProvider = LogProvider.getInstance();

            setHeader(HTTP_HEADER_ACCEPT_CHARSET, "UTF-8");
            setHeader(HTTP_HEADER_CONNECTION, "keep-alive");
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
            stringBody = jsonString;
            return setHeader(HttpUtils.HTTP_HEADER_CONTENT_TYPE, "application/json;charset=utf-8");
        }

        public RequestBuilder setJsonBody(Serializable value) {
            lockBodyType(BodyType.JSON);
            stringBody = JsonUtils.objectToJson(value);
            return setHeader(HttpUtils.HTTP_HEADER_CONTENT_TYPE, "application/json;charset=utf-8");
        }

        public RequestBuilder setXmlBody(String value) {
            lockBodyType(BodyType.XML);
            stringBody = value;
            return setHeader(HttpUtils.HTTP_HEADER_CONTENT_TYPE, "application/xml;charset=utf-8");
        }

        public RequestBuilder setBody(String value, @Nullable MediaType mediaType) {
            lockBodyType(BodyType.MEDIA_TYPE);
            stringBody = value;
            this.mediaType = mediaType;
            return this;
        }

        /**
         * 执行同步请求
         * @param retryMaxTime 最大重试次数
         * @return 响应
         */
        public HttpResult exec(int retryMaxTime) {
            // 请求构造
            Request request = requestBuild();
            // 执行同步请求
            return exec(request, retryMaxTime);
        }

        /**
         * 执行同步请求
         * 适用于外部手动构造的 Request 对象的使用, 之前的链式方法调用均会被忽略
         * @param request      请求构造对象
         * @param retryMaxTime 最大重试次数
         * @return 响应
         */
        public HttpResult exec(Request request, int retryMaxTime) {
            try {
                // 请求日志记录
                logProvider.printHttpConsumerSendingRequestLog(request, logBodyCache);

                // 执行请求与网络错误重试
                Response response = sync(request, retryMaxTime);

                // 响应解析与包装
                return analysisResponse(request, response);

            } catch (Throwable e) {
                throw new BusinessException(StatusCode.HTTP_REQUEST_API_ERROR, map -> map.put("request", request.toString()), e);
            }
        }

        /**
         * 执行异步请求
         * @param retryMaxTime      最大重试次数
         * @param callbackInterface 结果回调
         */
        public void exec(int retryMaxTime, CallbackInterface callbackInterface) {
            // 请求构造
            Request request = requestBuild();
            // 执行异步请求
            exec(request, retryMaxTime, callbackInterface);
        }

        /**
         * 执行异步请求
         * 适用于外部手动构造的 Request 对象的使用, 之前的链式方法调用均会被忽略
         * @param request           请求构造对象
         * @param retryMaxTime      最大重试次数
         * @param callbackInterface 结果回调
         */
        public void exec(Request request, int retryMaxTime, CallbackInterface callbackInterface) {
            // 请求日志记录
            logProvider.printHttpConsumerSendingRequestLog(request, logBodyCache);
            // 执行异步请求与网络错误重试
            async(request, 0, retryMaxTime, callbackInterface);
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
                    logBodyCache = mapBody.toString();
                    break;
                case MULTIPART:
                    multipartBody.setType(MultipartBody.FORM);
                    body = multipartBody.build();
                    logBodyCache = mapBody.toString();
                    break;
                case MEDIA_TYPE:
                    body = RequestBody.create(stringBody, mediaType);
                    logBodyCache = stringBody;
                    break;
                case XML:
                case JSON:
                default:
                    body = RequestBody.create(stringBody.getBytes(StandardCharsets.UTF_8));
                    logBodyCache = stringBody;
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
         * @param request      请求
         * @param retryMaxTime 最大重试次数
         * @return 响应
         * @throws IOException       异常
         * @throws BusinessException 异常
         */
        protected Response sync(Request request, int retryMaxTime) throws IOException, BusinessException {
            for (int retryTime = 1; retryTime <= retryMaxTime + 1; retryTime++) {
                try {
                    return httpClient.newCall(request).execute();
                } catch (Throwable e) {
                    if (retryTime > retryMaxTime) {
                        // 重试失败日志记录
                        logProvider.printHttpConsumerSendingRequestRetryGiveUpLog(retryTime - 1);

                        throw e;
                    }
                    // 重试日志记录
                    logProvider.printHttpConsumerSendingRequestRetryLog(retryTime, retryMaxTime);
                }
            }
            throw new BusinessException(StatusCode.HTTP_REQUEST_API_RETRY_TIME_ERROR);
        }

        /**
         * 执行异步请求
         * @param request           请求
         * @param retryTime         当前重试次数
         * @param retryMaxTime      最大重试次数
         * @param callbackInterface 结果回调
         */
        protected void async(Request request, int retryTime, int retryMaxTime, CallbackInterface callbackInterface) {

            int nextRetryTime = retryTime + 1;

            Map<String, Object> mdcInfo = new HashMap<>(16);
            // 获取MDC中的信息
            ChainProvider.initDataMap(mdcInfo);

            httpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // 将指定map中的所有有效"键"与"值"，赋值到MDC
                    ChainProvider.initMDC(mdcInfo);

                    if (nextRetryTime <= retryMaxTime) {

                        // 重试日志记录
                        logProvider.printHttpConsumerSendingRequestRetryLog(retryTime, retryMaxTime);

                        // 执行异步请求
                        async(request, nextRetryTime, retryMaxTime, callbackInterface);
                    } else {
                        // 重试失败日志记录
                        logProvider.printHttpConsumerSendingRequestRetryGiveUpLog(retryTime);

                        callbackInterface.call(null,
                            new BusinessException(StatusCode.HTTP_REQUEST_API_ERROR, map -> map.put("request", request.toString()), e));
                    }
                    // 清除
                    ChainProvider.clear();
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
         * @throws IOException 响应解析异常
         */
        protected HttpResult analysisResponse(Request request, Response response) throws IOException {
            Headers responseHeaders = response.headers();

            // 响应体字符串
            String responseBodyString = FinalVariable.NULL;
            try (ResponseBody body = response.body()) {
                if (body != null) {
                    responseBodyString = acceptEncodingGzip ? HttpUtils.gzipDecode(body.bytes(), responseHeaders.toMultimap()) : body.string();
                }
            } finally {
                // 响应日志记录
                logProvider.printHttpConsumerReceivedResponseLog(request, response, responseBodyString);
            }
            return new HttpResult(response, responseHeaders, responseBodyString);
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
        return Arrays.toString(content);
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
     * http响应
     */
    public static class HttpResult implements Serializable {

        private static final long serialVersionUID = 1L;

        private final Response response;

        private final Headers headers;

        private final String bodyString;

        public HttpResult(Response response, Headers headers, String bodyString) {
            this.response = response;
            this.headers = headers;
            this.bodyString = bodyString;
        }

        public Response getResponse() {
            return response;
        }

        public Headers getHeaders() {
            return headers;
        }

        public String getBodyString() {
            return bodyString;
        }

        @Override
        public String toString() {
            return "HttpResult{" + "response=" + response + ", headers=" + headers + ", bodyString='" + bodyString + '\'' + '}';
        }
    }
}

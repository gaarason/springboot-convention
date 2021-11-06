package gaarason.convention.starter.webmvc.util;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.provider.ChainProvider;
import gaarason.convention.common.provider.LogProvider;
import gaarason.convention.common.util.HttpUtils;
import gaarason.convention.common.util.SpringUtils;
import gaarason.convention.common.web.contract.ChainContextHandlerContract;
import gaarason.convention.common.web.pojo.GeneralRequest;
import org.apache.catalina.connector.Request;
import org.apache.catalina.util.ParameterMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.*;

/**
 * @author xt
 */
public final class WebMvcRequestUtils {

    private static final Logger LOGGER = LogManager.getLogger(WebMvcRequestUtils.class);

    /**
     * 当tomcat出错时, 用于记录下当前的uri&query
     */
    private static final String WRITE_THE_REQUEST_REAL_URI_AND_QUERY_KEY = "writeTheRequestRealUrl";

    private WebMvcRequestUtils() {

    }

    /**
     * 在请求头中获取信息, 依次获取有值则提前返回
     * @param defaultValue   默认值
     * @param servletRequest request
     * @param keys           头中的keys
     * @return 头中的key的value
     */
    public static String getOneInHeader(String defaultValue, ServletRequest servletRequest, String... keys) {
        String header = getOneInHeader(servletRequest, keys);
        return header != null ? header : defaultValue;
    }

    /**
     * 在请求头中获取信息, 依次获取有值则提前返回
     * @param servletRequest request
     * @param keys           头中的keys
     * @return 头中的key的value
     */
    @Nullable
    public static String getOneInHeader(ServletRequest servletRequest, String... keys) {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            for (String key : keys) {
                String header = httpServletRequest.getHeader(key);
                if (header != null) {
                    return header;
                }
            }
        } catch (Throwable e) {
            WebMvcRequestUtils.LOGGER.error("getOneInHeader 处理{}出错", keys, e);
        }
        return null;
    }

    /**
     * 当tomcat出错时, 记录下当前的uri&query
     * @param uriAndQuery eg: /test/request/mapping?id=123&timestamp=4434&validTime=30&sign=get_query|
     */
    public static void writeTheRequestRealUri(String uriAndQuery) {
        MDC.put(WebMvcRequestUtils.WRITE_THE_REQUEST_REAL_URI_AND_QUERY_KEY, uriAndQuery);
    }

    /**
     * 读取当tomcat出错时, 记录下的当前的uri
     * @return url
     */
    @Nullable
    public static String readTheRequestRealUri() {
        return MDC.get(WebMvcRequestUtils.WRITE_THE_REQUEST_REAL_URI_AND_QUERY_KEY);
    }

    /**
     * 获取 RequestRealUrl
     * @param servletRequest 请求
     * @return RequestRealUrl
     */
    public static String dealRequestRealUrl(ServletRequest servletRequest) {
        String requestRealUrl = "";
        try {
            String uriAndQuery = WebMvcRequestUtils.readTheRequestRealUri();
            if (uriAndQuery != null) {
                String scheme = servletRequest.getScheme();
                String serverName = servletRequest.getServerName();
                int serverPort = servletRequest.getServerPort();
                requestRealUrl = scheme + "://" + serverName + ":" + serverPort + uriAndQuery;
            } else {
                HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
                String url = httpServletRequest.getRequestURL().toString();
                String queryString = httpServletRequest.getQueryString();
                requestRealUrl = url + (queryString == null ? "" : "?" + queryString);
            }
        } catch (Throwable e) {
            WebMvcRequestUtils.LOGGER.error("处理REQUEST_REAL_URL出错", e);
        }
        return requestRealUrl;
    }

    /**
     * 获取 traceId
     * 优先skyWalking, 其次http header, 最后自动生成
     * @param servletRequest 请求
     * @return traceId
     */
    public static String dealTraceId(ServletRequest servletRequest) {
        return ChainProvider.computeIfAbsentTraceId(
            () -> getOneInHeader(servletRequest, ChainProvider.CanCrossProcessKey.TRACE_ID.getHttpHeaderKey()));
    }

    /**
     * 获取 RequestUrl
     * @param servletRequest 请求
     * @return RequestUrl
     */
    public static String dealRequestUrl(ServletRequest servletRequest) {
        return WebMvcRequestUtils.getOneInHeader("", servletRequest, FinalVariable.X_FORWARD_REQUEST_URL);
    }

    /**
     * 在请求入口, 打印入口日志
     * @param request HttpRequest
     */
    public static void printHttpProviderReceivedRequestLog(HttpServletRequest request) {
        // 设置traceId
        ChainProvider.put(ChainProvider.CanCrossProcessKey.TRACE_ID, dealTraceId(request));

        // 设置请求方法
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_METHOD, request.getMethod());

        // 设置请求头
        Map<String, List<String>> headersMap = WebMvcRequestUtils.formattingHeader(request);
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_HEADER_STRING, headersMap.toString());

        // 设置请求时间
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_DATETIME, FinalVariable.NOW_DATETIME.get());

        // 设置请求后端URL
        String requestRealUrl = WebMvcRequestUtils.dealRequestRealUrl(request);
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_REAL_URL, requestRealUrl);

        // 设置请求URL
        String requestUrl = WebMvcRequestUtils.dealRequestUrl(request);
        ChainProvider.put(ChainProvider.CanNotCrossProcessKey.REQUEST_URL, requestUrl.isEmpty() ? requestRealUrl : requestUrl);

        // 设置自定义上下文
        SpringUtils.getBean(ChainContextHandlerContract.class).conversion(generateRequest(request));

        // 请求入口日志
        LogProvider.getInstance().printHttpProviderReceivedRequestLog();

        // 记录请求体日志 (非 form-data && application/x-www-form-urlencoded)
        WebMvcRequestUtils.printHttpProviderReceivedRequestBodyLog(request);
    }

    /**
     * 记录请求体 (form-data && application/x-www-form-urlencoded)
     * @param request      request
     * @param parameterMap 请求中的格式化参数(不会包含file)
     */
    public static void printHttpProviderReceivedRequestBodyLog(Request request, ParameterMap<String, String[]> parameterMap) {
        if (WebMvcRequestUtils.canLogBodyByMap(request)) {
            LogProvider.getInstance().printHttpProviderReceivedRequestBodyLog(() -> {
                Map<String, Object> result = new HashMap<>(16);
                for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                    List<String> strings = Arrays.asList(entry.getValue());
                    result.put(entry.getKey(), strings.size() == 1 ? strings.get(0) : strings);
                }
                return result.toString();
            });
        }
    }

    /**
     * 记录请求体 (非 form-data && application/x-www-form-urlencoded)
     * @param request request
     */
    private static void printHttpProviderReceivedRequestBodyLog(HttpServletRequest request) {
        if (WebMvcRequestUtils.canLogBodyByByte(request)) {
            String characterEncoding = request.getCharacterEncoding();
            characterEncoding = characterEncoding == null ? org.apache.coyote.Constants.DEFAULT_BODY_CHARSET.name() : characterEncoding;

            StringBuilder sb = new StringBuilder(1024);
            try {
                BufferedReader reader = request.getReader();
                char[] chars = new char[1024];
                int numRead;
                while ((numRead = reader.read(chars)) > -1) {
                    String s = new String(chars).substring(0, numRead);
                    String line = new String(s.getBytes(characterEncoding));
                    sb.append(line);
                }
            } catch (Throwable e) {
                throw new BusinessException("printHttpProviderReceivedRequestBodyLog error", e);
            }
            LogProvider.getInstance().printHttpProviderReceivedRequestBodyLog(sb::toString);
        }
    }

    /**
     * 是否需要记录请求体(非 form-data && application/x-www-form-urlencoded)
     * @param request 请求
     * @param body    请求体byte
     * @return boolean
     */
    public static boolean canLogBodyByByte(Request request, @Nullable byte[] body) {
        return body != null || WebMvcRequestUtils.canLogBodyByByte(request);
    }

    /**
     * 是否需要记录请求体(非 form-data && application/x-www-form-urlencoded)
     * @param request 请求
     * @return boolean
     */
    public static boolean canLogBodyByByte(HttpServletRequest request) {
        return LogProvider.getInstance().isLogHttpProviderReceivedRequestBody()
            && HttpUtils.isMethodCanWithBody(FinalVariable.Http.Method.valueOf(request.getMethod().toUpperCase()))
            && !WebMvcRequestUtils.canLogBodyByMap(request);
    }

    /**
     * 是否需要记录请求体(form-data && application/x-www-form-urlencoded)
     * @param request 请求
     * @return boolean
     */
    public static boolean canLogBodyByMap(HttpServletRequest request) {
        if (!HttpUtils.isMethodCanWithBody(FinalVariable.Http.Method.valueOf(request.getMethod().toUpperCase()))) {
            return false;
        }

        String contentTypeString = request.getContentType();
        MediaType contentType = contentTypeString != null ? MediaType.parseMediaType(contentTypeString) : MediaType.APPLICATION_OCTET_STREAM;
        // 非 form-data && x-www-form-urlencoded
        return MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType) || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType);
    }

    /**
     * 获取请求头, 且格式化
     * @param request 请求
     * @return 请求头
     */
    private static Map<String, List<String>> formattingHeader(HttpServletRequest request) {
        Map<String, List<String>> headerMap = new HashMap<>(16);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerKey = headerNames.nextElement();
            Enumeration<String> headers = request.getHeaders(headerKey);
            List<String> headerValues = headerMap.computeIfAbsent(headerKey, key -> new ArrayList<>());
            while (headers.hasMoreElements()) {
                headerValues.add(headers.nextElement());
            }
        }
        return headerMap;
    }

    /**
     * 生成简易request 对象
     * @param theRequest HttpServletRequest
     * @return 简易request
     */
    public static GeneralRequest<HttpServletRequest> generateRequest(HttpServletRequest theRequest) {

        Map<String, List<String>> header = new HashMap<>(16);
        Enumeration<String> headerNames = theRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            Enumeration<String> values = theRequest.getHeaders(key);
            ArrayList<String> theValues = new ArrayList<>();
            while (values.hasMoreElements()) {
                theValues.add(values.nextElement());
            }
            header.put(key, theValues);
        }
        return new GeneralRequest<>(theRequest.getQueryString(), header, theRequest);
    }
}

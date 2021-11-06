package gaarason.convention.common.web.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 通用请求对象
 * @param <T> 请求类型
 * @author xt
 * @since 2021/9/1 6:52 下午
 */
public class GeneralRequest<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String query;

    private final Map<String, List<String>> header;

    /**
     * 在tomcat下正常为: javax.servlet.http.HttpServletRequest
     * <p>
     * 在netty下正常为: org.springframework.http.server.reactive.ServerHttpRequest
     * 在netty下异常为: io.netty.handler.codec.http.HttpRequest
     */
    private final transient T originalRequest;

    public GeneralRequest(String queryString, Map<String, List<String>> headerMap, T originalRequestObj) {
        query = queryString;
        header = headerMap;
        originalRequest = originalRequestObj;
    }

    public String getQuery() {
        return query;
    }

    public Map<String, List<String>> getHeader() {
        return header;
    }

    public T getOriginalRequest() {
        return originalRequest;
    }

    @Override
    public String toString() {
        return "Request{" + "query='" + query + "'" + ", header=" + header + ", originalRequest=" + originalRequest + '}';
    }
}

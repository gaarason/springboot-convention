package gaarason.convention.starter.webmvc.filter;

import gaarason.convention.starter.webmvc.util.WebMvcRequestUtils;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 这个filter仅用来记录请求体
 * @author xt
 */
public class WebMvcLogFilter extends OncePerRequestFilter implements OrderedFilter {

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        // http 入口日志, 设置上下文信信息
        WebMvcRequestUtils.printHttpProviderReceivedRequestLog(request);

        filterChain.doFilter(request, response);
    }
}

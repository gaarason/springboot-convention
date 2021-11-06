package gaarason.convention.starter.webmvc.request.binder;

import gaarason.convention.common.util.StringUtils;
import gaarason.convention.common.web.util.LocalDateTimeUtils;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author xt
 */
public class WebMvcServletRequestDataBinder extends ServletRequestDataBinder {

    public WebMvcServletRequestDataBinder(final Object target, final String objectName) {
        super(target, objectName);
    }

    /**
     * Return the underlying PropertyAccessor of this binder's BindingResult.
     */
    @Override
    protected ConfigurablePropertyAccessor getPropertyAccessor() {
        final ConfigurablePropertyAccessor registry = getInternalBindingResult().getPropertyAccessor();
        // 时间解析器
        LocalDateTimeUtils.registerCustomEditorForLocalDateTime(registry);

        return registry;
    }

    /**
     * 获取参数
     * @param request 请求
     * @param prefix  前缀
     * @return 参数
     */
    protected static Map<String, Object> getParametersStartingWith(final ServletRequest request, @Nullable String prefix) {
        Assert.notNull(request, "Request must not be null");
        final Enumeration<String> paramNames = request.getParameterNames();
        final Map<String, Object> params = new TreeMap<>();
        if (prefix == null) {
            prefix = "";
        }
        while (paramNames != null && paramNames.hasMoreElements()) {
            final String paramName = paramNames.nextElement();
            if (prefix.isEmpty() || paramName.startsWith(prefix)) {
                final String unprefixed = paramName.substring(prefix.length());
                final String[] values = request.getParameterValues(paramName);
                final String lineToHumpParamName = StringUtils.lineToHump(unprefixed);
                if (values.length > 1) {
                    params.put(lineToHumpParamName, values);
                } else {
                    params.put(lineToHumpParamName, values[0]);
                }
            }
        }
        return params;
    }

    @Override
    public void bind(final ServletRequest request) {
        final MutablePropertyValues mpvs = new WebMvcServletRequestParameterPropertyValues(request);
        final MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
        final String prefix = "multipart/";
        if (multipartRequest != null) {
            bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        } else if (org.springframework.util.StringUtils.startsWithIgnoreCase(request.getContentType(), prefix)) {
            final HttpServletRequest httpServletRequest = WebUtils.getNativeRequest(request, HttpServletRequest.class);
            if (httpServletRequest != null) {
                StandardServletPartUtils.bindParts(httpServletRequest, mpvs, isBindEmptyMultipartFiles());
            }
        }
        addBindValues(mpvs, request);
        doBind(mpvs);
    }

    /**
     * @see ServletRequestParameterPropertyValues
     */
    static class WebMvcServletRequestParameterPropertyValues extends MutablePropertyValues {

        /**
         * Default prefix separator.
         */
        public static final String DEFAULT_PREFIX_SEPARATOR = "_";

        private static final long serialVersionUID = 3641374137779787619L;

        /**
         * Create new ServletRequestPropertyValues using no prefix
         * (and hence, no prefix separator).
         * @param request the HTTP request
         */
        WebMvcServletRequestParameterPropertyValues(final ServletRequest request) {
            this(request, null, null);
        }

        /**
         * Create new ServletRequestPropertyValues using the given prefix and
         * the default prefix separator (the underscore character "_").
         * @param request the HTTP request
         * @param prefix  the prefix for parameters (the full prefix will
         *                consist of this plus the separator)
         * @see #DEFAULT_PREFIX_SEPARATOR
         */
        WebMvcServletRequestParameterPropertyValues(final ServletRequest request, @Nullable final String prefix) {
            this(request, prefix, WebMvcServletRequestParameterPropertyValues.DEFAULT_PREFIX_SEPARATOR);
        }

        /**
         * Create new ServletRequestPropertyValues supplying both prefix and
         * prefix separator.
         * @param request         the HTTP request
         * @param prefix          the prefix for parameters (the full prefix will
         *                        consist of this plus the separator)
         * @param prefixSeparator separator delimiting prefix (e.g. "spring")
         *                        and the rest of the parameter name ("param1", "param2")
         */
        WebMvcServletRequestParameterPropertyValues(final ServletRequest request, @Nullable final String prefix,
            @Nullable final String prefixSeparator) {

            super(WebMvcServletRequestDataBinder.getParametersStartingWith(request, (prefix != null ? prefix + prefixSeparator : null)));
        }
    }
}

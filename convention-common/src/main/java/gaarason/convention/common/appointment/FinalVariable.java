package gaarason.convention.common.appointment;

import org.springframework.core.io.Resource;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

/**
 * 全局可用的常量
 * @author xt
 */
public class FinalVariable {

    /**
     * application.yml 中可以代表应用名称的的字段
     */
    public static final String[] APPLICATION_NAME_KEY = {"spring.application.name", "dubbo.application.name"};

    /**
     * 当出现异常时, 以下的环境中, 在响应中体现异常堆栈
     */
    public static final String[] SHOW_STACK_TRACE_ENV = {"default", "dev", "test"};

    /**
     * 实体中普通属性支持的包装类型
     */
    public static final List<Class<?>> ALLOW_FIELD_TYPES = Arrays.asList(Boolean.class, Byte.class, Character.class, Short.class, Integer.class,
        Long.class,
        Float.class, Double.class, BigInteger.class, Date.class, String.class);

    /**
     * null
     */
    public static final String NULL = "null";

    /**
     * 空字符串
     */
    public static final String EMPTY_STRING = "";

    /**
     * 已经发送结果
     */
    public static final String SENT_HTTP_RESPONSE = "sent response";

    /**
     * 当前日期时间
     */
    public static final Supplier<String> NOW_DATETIME = () -> LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    /**
     * 生成 TRACE_ID
     */
    public static final Supplier<String> GENERATE_TRACE_ID = () -> CommonVariable.APPLICATION_NAME + "_" + UUID.randomUUID().toString().replace("-",
        "");

    /**
     * 生成 TRACE_ID, 不应该为空的场景, 因为各种情况的补救生成
     */
    public static final Supplier<String> AUTO_GENERATE_TRACE_ID =
        () -> CommonVariable.APPLICATION_NAME + "_AUTO" + UUID.randomUUID().toString().replace("-", "");

    /**
     * 开放平台网关设置的 X-Forwarded-Request-Url 在 http host 中的键值
     */
    public static final String X_FORWARD_REQUEST_URL = "X-Forwarded-Request-Url";

    /**
     * 在http头中获取/设置租户代码, 租户代码对应的key, 优先级依次降低
     */
    public static final String[] TENANT_CODE_IN_HTTP_HEADER_AND_QUERY = {"x-jwt-tenant", "tenant_code", "o"};

    /**
     * hash map 默认大小
     */
    private static final int HASH_MAP_INT_SIZE = 16;

    /**
     * 不可解析的对象
     */
    public static final Set<Class<?>> NON_BEAN_CLASSES = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList(Object.class, Class.class, Resource.class)));

    private FinalVariable() {
    }

    /**
     * 时间相关
     */
    public static class Timestamp {

        /**
         * 默认日期时间格式
         */
        public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

        /**
         * 默认日期格式
         */
        public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

        /**
         * 默认时间格式
         */
        public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss.SSS";

        private Timestamp() {
        }
    }

    /**
     * Http 相关
     */
    public static class Http {

        public enum Method {
            POST, GET, PUT, PATCH, DELETE, HEAD, OPTIONS
        }

        /**
         * 使用统一响应封装的, 在响应头中标记一下, 方便客户端解码时判断在, http响应头中的键值
         */
        public static final String CONVENTION_MARK_UNIFIED_RESPONSE = "Convention-Mark-Unified-Response";

        /**
         * http 响应头中增加字符集指定
         */
        public static final String CHARACTER_ENCODING = "Character-Encoding";

        /**
         * 包含请求体的http方法
         */
        public static final List<Method> METHOD_WITH_BODY = Arrays.asList(Method.POST, Method.PUT, Method.PATCH);
    }

    /**
     * 常用正则表达式(以下不保证准确)
     */
    public static class RegularExpressions {

        /**
         * 电子邮箱
         */
        public static final String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

        /**
         * 移动电话号码(国内)
         */
        public static final String MOBILE_NUMBER_CH = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";

        /**
         * 固定电话号码(国内)
         * eg : 0511-4405222、021-87888822
         */
        public static final String PHONE_NUMBER_CH = "\\d{3}-\\d{8}|\\d{4}-\\d{7}";

        /**
         * 身份证号码(国内)
         */
        public static final String ID_CARD_CH = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";

        /**
         * QQ
         */
        public static final String QQ = "[1-9][0-9]{4,}";

        /**
         * 邮政编码(国内)
         */
        public static final String POSTCODE_CH = "[1-9]\\d{5}(?!\\d)";

        /**
         * IP
         */
        public static final String IP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        private RegularExpressions() {
        }
    }

}

package gaarason.convention.common.provider;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.util.StringUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 上下文记录
 * @author xt
 */
public final class ChainProvider {

    /**
     * skywalking 的 trace id 的空值
     */
    private static final String NO_FOUND_SKY_WALKING_TRACE_ID = "N/A";

    private ChainProvider() {
    }

    /**
     * 将"键"与"值"设置到指定map与MDC
     * @param dataMap 指定map
     * @param type    上下文类型
     * @param key     键
     * @param value   值
     */
    public static void put(Map<String, Object> dataMap, ChainType type, String key, String value) {
        // map 写入
        getRealMap(dataMap, type).put(key, value);
        // MDC写入
        put(type, key, value);
    }

    /**
     * 将"键"与"值"设置到指定map与MDC
     * @param dataMap 指定map
     * @param key     键
     * @param value   值
     */
    public static void put(Map<String, Object> dataMap, CanCrossProcessKey key, String value) {
        put(dataMap, ChainType.CAN_CROSS_PROCESS, key.name(), value);
    }

    /**
     * 将"键"与"值"设置到指定map与MDC
     * @param dataMap 指定map
     * @param key     键
     * @param value   值
     */
    public static void put(Map<String, Object> dataMap, CanNotCrossProcessKey key, String value) {
        put(dataMap, ChainType.CAN_NOT_CROSS_PROCESS, key.name(), value);
    }

    /**
     * 将"键"与"值"设置到MDC
     * @param type  上下文类型
     * @param key   键
     * @param value 值
     */
    public static void put(ChainType type, String key, String value) {
        MDC.put(generateKey(type, key), value);
    }

    /**
     * 将"键"与"值"设置到MDC
     * @param key   键
     * @param value 值
     */
    public static void put(CanCrossProcessKey key, String value) {
        put(ChainType.CAN_CROSS_PROCESS, key.name(), value);
    }

    /**
     * 将"键"与"值"设置到MDC
     * @param key   键
     * @param value 值
     */
    public static void put(CanNotCrossProcessKey key, String value) {
        put(ChainType.CAN_NOT_CROSS_PROCESS, key.name(), value);
    }

    /**
     * 获取指定map中的所有的"键"与"值"
     * @param dataMap 指定map
     * @param type    上下文类型
     * @return MAP<Key, Value>
     */
    public static Map<String, String> get(Map<String, Object> dataMap, ChainType type) {
        return getRealMap(dataMap, type);
    }

    /**
     * 获取指定map中的指定"键"的"值"
     * @param dataMap 指定map
     * @param type    上下文类型
     * @param key     键
     * @return 值
     */
    @Nullable
    public static String get(Map<String, Object> dataMap, ChainType type, String key) {
        final Map<String, String> localMap = getRealMap(dataMap, type);
        return localMap.get(key);
    }

    /**
     * 获取指定map中的指定"键"的"值"
     * @param dataMap      指定map
     * @param type         上下文类型
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String get(Map<String, Object> dataMap, ChainType type, String key, String defaultValue) {
        return getOrDefaultValue(get(dataMap, type, key), defaultValue);
    }

    /**
     * 获取指定map中的指定"键"的"值"
     * @param dataMap 指定map
     * @param key     键
     * @return 值
     */
    @Nullable
    public static String get(Map<String, Object> dataMap, CanCrossProcessKey key) {
        return get(dataMap, ChainType.CAN_CROSS_PROCESS, key.name());
    }

    /**
     * 获取指定map中的指定"键"的"值"
     * @param dataMap      指定map
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String get(Map<String, Object> dataMap, CanCrossProcessKey key, String defaultValue) {
        return get(dataMap, ChainType.CAN_CROSS_PROCESS, key.name(), defaultValue);
    }

    /**
     * 获取指定map中的指定"键"的"值"
     * @param dataMap 指定map
     * @param key     键
     * @return 值
     */
    @Nullable
    public static String get(Map<String, Object> dataMap, CanNotCrossProcessKey key) {
        return get(dataMap, ChainType.CAN_NOT_CROSS_PROCESS, key.name());
    }

    /**
     * 获取指定map中的指定"键"的"值"
     * @param dataMap      指定map
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String get(Map<String, Object> dataMap, CanNotCrossProcessKey key, String defaultValue) {
        return get(dataMap, ChainType.CAN_NOT_CROSS_PROCESS, key.name(), defaultValue);
    }

    /**
     * 获取MDC中的所有的"键"与"值"
     * @param type 上下文类型
     * @return MAP<Key, Value>
     */
    public static Map<String, String> get(ChainType type) {
        final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        return copyOfContextMap.entrySet().stream().filter(entry -> entry.getKey().startsWith(type.name()))
            .collect(Collectors.toMap(entry -> reductionKey(type, entry.getKey()), Map.Entry::getValue));
    }

    /**
     * 获取MDC中的指定"键"的"值"
     * @param type 上下文类型
     * @param key  键
     * @return 值
     */
    @Nullable
    public static String get(ChainType type, String key) {
        return MDC.get(generateKey(type, key));
    }

    /**
     * 获取MDC中的指定"键"的"值"
     * @param type         上下文类型
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String get(ChainType type, String key, String defaultValue) {
        return getOrDefaultValue(MDC.get(generateKey(type, key)), defaultValue);
    }

    /**
     * 获取MDC中的指定"键"的"值"
     * @param key 键
     * @return 值
     */
    @Nullable
    public static String get(CanCrossProcessKey key) {
        return get(ChainType.CAN_CROSS_PROCESS, key.name());
    }

    /**
     * 获取MDC中的指定"键"的"值"
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String get(CanCrossProcessKey key, String defaultValue) {
        return get(ChainType.CAN_CROSS_PROCESS, key.name(), defaultValue);
    }

    /**
     * 获取MDC中的指定"键"的"值"
     * @param key 键
     * @return 值
     */
    @Nullable
    public static String get(CanNotCrossProcessKey key) {
        return get(ChainType.CAN_NOT_CROSS_PROCESS, key.name());
    }

    /**
     * 获取MDC中的指定"键"的"值"
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public static String get(CanNotCrossProcessKey key, String defaultValue) {
        return get(ChainType.CAN_NOT_CROSS_PROCESS, key.name(), defaultValue);
    }

    /**
     * 移除指定map中的指定"键"
     * @param dataMap 指定msp
     * @param type    上下文类型
     * @param key     键
     */
    public static void remove(Map<String, Object> dataMap, ChainType type, String key) {
        final Map<String, String> localMap = getRealMap(dataMap, type);
        localMap.remove(key);
    }

    /**
     * 移除指定map中的指定"键"
     * @param dataMap 指定msp
     * @param type    上下文类型
     * @param key     键
     */
    public static void remove(Map<String, Object> dataMap, ChainType type, CanCrossProcessKey key) {
        remove(dataMap, type, key.name());
    }

    /**
     * 移除指定map中的指定"键"
     * @param dataMap 指定msp
     * @param type    上下文类型
     * @param key     键
     */
    public static void remove(Map<String, Object> dataMap, ChainType type, CanNotCrossProcessKey key) {
        remove(dataMap, type, key.name());
    }

    /**
     * 移除MDC中的指定"键"
     * @param type 上下文类型
     * @param key  键
     */
    public static void remove(ChainType type, String key) {
        MDC.remove(generateKey(type, key));
    }

    /**
     * 移除MDC中的指定"键"
     * @param type 上下文类型
     * @param key  键
     */
    public static void remove(ChainType type, CanCrossProcessKey key) {
        remove(type, key.name());
    }

    /**
     * 移除MDC中的指定"键"
     * @param type 上下文类型
     * @param key  键
     */
    public static void remove(ChainType type, CanNotCrossProcessKey key) {
        remove(type, key.name());
    }

    /**
     * 移除指定map与MDC中的所有"键"(仅影响有本类设置的内容)
     * @param dataMap 指定map
     */
    public static void clear(Map<String, Object> dataMap) {
        // map清理
        Arrays.stream(ChainType.values()).forEach(chainType -> dataMap.remove(chainType.name()));

        // MDC清理
        clear();
    }

    /**
     * 移除MDC中的所有"键"(仅影响有本类设置的内容)
     */
    public static void clear() {
        final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
        // 移除所有MDC中 前缀属于 ChainType 的值
        copyOfContextMap.entrySet().stream().filter(
                entry -> Arrays.stream(ChainType.values()).anyMatch(chainType -> entry.getKey().startsWith(chainType.name())))
            .forEach(entry -> MDC.remove(entry.getKey()));
    }

    /**
     * 将MDC中的所有有效"键"与"值"，赋值到指定map
     * @param dataMap 指定map
     */
    public static void initDataMap(Map<String, Object> dataMap) {
        Arrays.stream(ChainType.values()).forEach(chainType -> {
            Map<String, String> theRealMap = getRealMap(dataMap, chainType);
            theRealMap.putAll(get(chainType));
        });
    }

    /**
     * 将指定map中的所有有效"键"与"值"，赋值到MDC
     * @param dataMap 指定map
     */
    public static void initMDC(Map<String, Object> dataMap) {
        Arrays.stream(ChainType.values()).forEach(
            chainType -> getRealMap(dataMap, chainType).forEach((key, value) -> put(chainType, key, value)));
    }

    /**
     * 得到 traceId （不会进行任何设置与记录）
     * 优先skyWalking, 其次func, 最后自动生成
     * @param func 函数
     * @return traceId
     */
    public static String computeIfAbsentTraceId(Supplier<String> func) {
        String traceId = TraceContext.traceId();
        // 如果 skywalking 中的 traceId 不存在，则使用业务上的 traceId
        if (ObjectUtils.isEmpty(traceId) || NO_FOUND_SKY_WALKING_TRACE_ID.equalsIgnoreCase(traceId)) {
            traceId = func.get();
        }
        // 如果业务上的 traceId 不存在，则自动生成
        return ObjectUtils.isEmpty(traceId) ? FinalVariable.GENERATE_TRACE_ID.get() : traceId;
    }


    /**
     * 生成键名
     * @param type 上下文类型
     * @param key  键（不含前缀）
     * @return 键名（含前缀）
     */
    private static String generateKey(ChainType type, String key) {
        return type.name() + "|" + key;
    }

    /**
     * 还原键
     * @param type 上下文类型
     * @param key  键名（含前缀）
     * @return 键（不含前缀）
     */
    private static String reductionKey(ChainType type, String key) {
        return StringUtils.ltrim(key + "|", type.name());
    }

    /**
     * 在指定map中获取真实存储的map
     * @param dataMap 指定map
     * @param type    上下文类型
     * @return 真实存储的map
     */
    private static Map<String, String> getRealMap(Map<String, Object> dataMap, ChainType type) {
        return ObjectUtils.typeCast(dataMap.computeIfAbsent(type.name(), k -> new HashMap<String, String>(16)));
    }

    /**
     * 当值为null时，使用默认值保底
     * @param value        值
     * @param defaultValue 默认值
     */
    private static String getOrDefaultValue(@Nullable String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 上下文类型
     */
    public enum ChainType {
        /**
         * 类型
         */
        CAN_CROSS_PROCESS, CAN_NOT_CROSS_PROCESS
    }

    /**
     * 上下文, 最终记录到MDC, 需要跨服务传递
     */
    public enum CanCrossProcessKey {

        /**
         * 手动设置的 TRACE_ID
         */
        TRACE_ID("Trace-Id"),

        /**
         * 手动设置的 TENANT_CODE
         */
        TENANT_CODE("Tenant-Code"),

        /**
         * 手动设置的 TENANT_ID
         */
        TENANT_ID("Tenant-Id"),

        /**
         * 手动设置的 USER_ACCOUNT
         */
        USER_ACCOUNT("User-Account"),

        /**
         * 手动设置的 USER_ID
         */
        USER_ID("User-Id");

        /**
         * http头中的键
         */
        private final String httpHeaderKey;

        CanCrossProcessKey(String httpHeaderKey) {
            this.httpHeaderKey = httpHeaderKey;
        }

        public String getHttpHeaderKey() {
            return httpHeaderKey;
        }

        private static final Map<String, CanCrossProcessKey> HTTP_HEADER_KEY_MAP = Arrays.stream(CanCrossProcessKey.values()).collect(
            Collectors.toMap(CanCrossProcessKey::getHttpHeaderKey, o -> o));

        private static final Set<String> ALL_HTTP_HEADER_KEY = HTTP_HEADER_KEY_MAP.keySet();
    }

    /**
     * 上下文, 最终记录到MDC, 不需要跨服务传递
     */
    public enum CanNotCrossProcessKey {

        /**
         * 手动设置的请求时间
         */
        REQUEST_DATETIME,

        /**
         * 请求方法
         */
        REQUEST_METHOD,

        /**
         * 请求头
         */
        REQUEST_HEADER_STRING,

        /**
         * 真正处理请求的url
         */
        REQUEST_REAL_URL,

        /**
         * 网关对外提供服务的url
         */
        REQUEST_URL,

        /**
         * 是否已经发送http结果
         */
        SENT_HTTP_RESPONSE,
    }
}

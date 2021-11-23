package gaarason.convention.common.util;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

/**
 * 对象操作类
 *
 * @author xt
 */
public class ObjectUtils {

    private ObjectUtils(){}

    /**
     * 获取指定类中的第index个泛型的类
     *
     * @param clazz 指定类
     * @param index 第几个
     * @param <A>   泛型的类
     * @param <B>   指定类型
     * @return 泛型的类
     */
    @SuppressWarnings("unchecked")
    public static <A, B> Class<A> getGenerics(Class<B> clazz, int index) {
        return (Class<A>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[index];
    }

    /**
     * 获取指定类中的第index个泛型的类
     *
     * @param parameterizedType 指定类
     * @param index             第几个
     * @param <A>               泛型的类
     * @return 泛型的类
     */
    @SuppressWarnings("unchecked")
    public static <A> Class<A> getGenerics(ParameterizedType parameterizedType, int index) {
        return (Class<A>) parameterizedType.getActualTypeArguments()[index];
    }

    /**
     * 是否是集合类型
     *
     * @param clazz 类型
     * @return 是否
     */
    public static boolean isCollection(Class<?> clazz) {
        return Arrays.asList(clazz.getInterfaces()).contains(Collection.class);
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     * 处理父类属性,不处理静态属性,不处理json忽略属性
     *
     * @param obj 对象
     * @return map
     */
    public static Map<String, Object> obj2Map(@Nullable Object obj, Function<String, String> dealKey) {
        try {
            Map<String, Object> resultMap = new LinkedHashMap<>();
            if (obj != null) {
                Class<?> clazz = obj.getClass();
                // 获取本身和父级对象
                for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
                    // 获取所有私有字段
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (Modifier.isStatic(field.getModifiers()) || (null != field.getAnnotation(JsonIgnore.class))) {
                            // 不处理静态属性
                            // 不处理json忽略属性
                            continue;
                        }
                        field.setAccessible(true);
                        resultMap.put(dealKey.apply(field.getName()), field.get(obj));
                    }
                }
            }
            return resultMap;
        } catch (Throwable e) {
            throw new BusinessException("Failed to serialize object to Map", map -> {
                map.put("Object", obj != null ? obj : FinalVariable.NULL);
            }, e);
        }
    }

    /**
     * 通过序列化对普通对象进行递归copy
     *
     * @param original 源对象
     * @param <T>      对象所属的类
     * @return 全新的对象
     * @throws BusinessException 克隆异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T original) {
        try {
            ByteArrayOutputStream bis = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bis);
            oos.writeObject(original);
            oos.flush();
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bis.toByteArray()));
            return (T) input.readObject();
        } catch (Throwable e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    /**
     * 无绑定对象
     *
     * @param resolved 类型
     * @return 是否
     */
    public static boolean isUnbindableBean(Class<?> resolved) {
        if (resolved.isPrimitive() || FinalVariable.NON_BEAN_CLASSES.contains(resolved)) {
            return true;
        }
        return resolved.getName().startsWith("java.");
    }

    /**
     * 判断字段是否静态
     *
     * @param field 字段
     * @return 是否
     */
    public static boolean isStaticField(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    /**
     * 判断字段是否基本类型(含包装类型)
     *
     * @param field 字段
     * @return 是否
     */
    public static boolean isBasicField(Field field) {
        return isBasicField(field.getType());
    }

    /**
     * 判断是否基本类型(含包装类型)
     *
     * @param clazz 类型
     * @return 是否
     */
    public static boolean isBasicField(Class<?> clazz) {
        return clazz.isPrimitive() || FinalVariable.ALLOW_FIELD_TYPES.contains(clazz);
    }

    /**
     * 属性是否在类中存在(多层级)
     * 集合类型的属性,将会使用第一个泛型类型
     *
     * @param detectedClass     待检测的类
     * @param multipleAttribute 检测的属性 eg: teacher.student.id
     * @return 是否存在
     */
    public static boolean checkProperties(Class<?> detectedClass, String multipleAttribute) {
        String[] attrArr = multipleAttribute.split("\\.");
        try {
            Class<?> tempClass = detectedClass;
            for (String attr : attrArr) {
                Field field = tempClass.getDeclaredField(attr);

                tempClass = field.getType();
                boolean contains = new ArrayList<>(Arrays.asList(tempClass.getInterfaces())).contains(Collection.class);
                // 如果是集合类型, 那么使用泛型对象
                if (contains) {
                    Type genericType = field.getGenericType();
                    // 如果是泛型
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) genericType;
                        // 得到泛型里的第一个class类型对象
                        tempClass = (Class<?>) pt.getActualTypeArguments()[0];
                    }
                }
            }
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        } catch (Throwable e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    /**
     * 通过反射更改属性的值
     *
     * @param obj            对象
     * @param attributesName 属性名
     * @param newValue       新的属性值
     */
    public static void changeAttributes(Object obj, String attributesName, Object newValue) {
        try {
            Field field = obj.getClass().getDeclaredField(attributesName);
            field.setAccessible(true);
            field.set(obj, newValue);
        } catch (Throwable e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    /**
     * 对象 转 对象 (强制类型转换)
     *
     * @param original 原始对象
     * @param <T>      原始类型
     * @param <N>      目标类型
     * @return 目标对象
     */
    @SuppressWarnings("unchecked")
    public static <T, N> N typeCast(T original) {
        try {
            return (N) original;
        } catch (Throwable e) {
            throw new BusinessException(e.getMessage(), e);
        }
    }

    /**
     * 对象 转 对象
     *
     * @param originalObj 原对象, 一般是map/list等
     * @param tClass      目标对象, 一般是自定义java对象
     * @param <T>         目标对象类型
     * @return 目标对象
     * @throws BusinessException 序列化异常
     */
    public static <T, K> T typeCast(K originalObj, Class<T> tClass) {
        try {
            ObjectMapper theMapper = JsonUtils.getTheMapper();
            String str = theMapper.writeValueAsString(originalObj);
            return theMapper.readValue(str, tClass);
        } catch (Throwable e) {
            throw new BusinessException("Failed to convert original object to new object", map -> {
                map.put("Original object", originalObj != null ? originalObj : FinalVariable.NULL);
                map.put("New object type", tClass != null ? tClass : FinalVariable.NULL);
            }, e);
        }
    }

    /**
     * 对象 转 对象
     *
     * @param originalObj 原对象, 一般是map/list等
     * @param type        目标对象, 一般是自定义java对象
     * @param <T>         目标对象类型
     * @return 目标对象
     * @throws BusinessException 序列化异常
     */
    public static <T, K> T typeCast(K originalObj, Type type) {
        try {
            ObjectMapper theMapper = JsonUtils.getTheMapper();
            String str = theMapper.writeValueAsString(originalObj);
            return theMapper.readValue(str, new TypeReference<T>() {
                @Override
                public Type getType() {
                    return type;
                }
            });
        } catch (Throwable e) {
            throw new BusinessException("Failed to convert original object to new object", map -> {
                map.put("Original object", originalObj != null ? originalObj : FinalVariable.NULL);
                map.put("New object type", type != null ? type : FinalVariable.NULL);
            }, e);
        }
    }

    /**
     * 对象 转 对象
     *
     * @param originalObj  原对象, 一般是map/list等
     * @param valueTypeRef 对象类型 eg: new TypeReference<ResultVO<List<List<String>>>>() {}
     * @param <T>          目标对象类型
     * @return 目标对象
     * @throws BusinessException 序列化异常
     */
    public static <T, K> T typeCast(K originalObj, TypeReference<T> valueTypeRef) {
        try {
            ObjectMapper theMapper = JsonUtils.getTheMapper();
            String str = theMapper.writeValueAsString(originalObj);
            return theMapper.readValue(str, valueTypeRef);
        } catch (Throwable e) {
            throw new BusinessException("Failed to convert original object to new object", map -> {
                map.put("Original object", originalObj != null ? originalObj : FinalVariable.NULL);
                map.put("New object valueTypeRef", valueTypeRef != null ? valueTypeRef : FinalVariable.NULL);
            }, e);
        }
    }

    /**
     * Determine whether the given array is empty:
     * i.e. {@code null} or of zero length.
     * @param array the array to check
     * @see #isEmpty(Object)
     */
    public static boolean isEmpty(@Nullable Object[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Determine whether the given object is empty.
     * <p>This method supports the following object types.
     * <ul>
     * <li>{@code Optional}: considered empty if {@link Optional#empty()}</li>
     * <li>{@code Array}: considered empty if its length is zero</li>
     * <li>{@link CharSequence}: considered empty if its length is zero</li>
     * <li>{@link Collection}: delegates to {@link Collection#isEmpty()}</li>
     * <li>{@link Map}: delegates to {@link Map#isEmpty()}</li>
     * </ul>
     * <p>If the given object is non-null and not one of the aforementioned
     * supported types, this method returns {@code false}.
     * @param obj the object to check
     * @return {@code true} if the object is {@code null} or <em>empty</em>
     * @since 4.2
     * @see Optional#isPresent()
     * @see org.springframework.util.ObjectUtils#isEmpty(Object[])
     * @see StringUtils#hasLength(CharSequence)
     * @see CollectionUtils#isEmpty(java.util.Collection)
     * @see CollectionUtils#isEmpty(java.util.Map)
     */
    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Optional) {
            return !((Optional<?>) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        // else
        return false;
    }


    /**
     * Determine if the given objects are equal, returning {@code true} if
     * both are {@code null} or {@code false} if only one is {@code null}.
     * <p>Compares arrays with {@code Arrays.equals}, performing an equality
     * check based on the array elements rather than the array reference.
     * @param o1 first Object to compare
     * @param o2 second Object to compare
     * @return whether the given objects are equal
     * @see Object#equals(Object)
     * @see java.util.Arrays#equals
     */
    public static boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    /**
     * Compare the given arrays with {@code Arrays.equals}, performing an equality
     * check based on the array elements rather than the array reference.
     * @param o1 first array to compare
     * @param o2 second array to compare
     * @return whether the given objects are equal
     * @see #nullSafeEquals(Object, Object)
     * @see java.util.Arrays#equals
     */
    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }
}

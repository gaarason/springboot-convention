package gaarason.convention.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.BusinessException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author xt
 */
public class JsonUtils {

    /**
     * jackson 一般对象
     */
    private static final ObjectMapper MAPPER = intMapper();

    /**
     * 获取json对象, 每次均会返回相同配置的全新引用对象(防止同地址对象属性被修改的情况)
     * 应避免重复调用
     * @return ObjectMapper
     */
    public static ObjectMapper getMapper() {
        return intMapper();
    }

    /**
     * 初始化
     * @return ObjectMapper
     */
    private static ObjectMapper intMapper() {
        ObjectMapper mapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
            .setSerializationInclusion(JsonInclude.Include.ALWAYS).enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class,
            new LocalTimeSerializer(DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class,
            new LocalTimeDeserializer(DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_TIME_FORMAT)));

        // Date序列化和反序列化
        javaTimeModule.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                SimpleDateFormat formatter = new SimpleDateFormat(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT);
                String formattedDate = formatter.format(date);
                jsonGenerator.writeString(formattedDate);
            }
        });
        javaTimeModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser jsonParser,
                DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                SimpleDateFormat format = new SimpleDateFormat(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT);
                String date = jsonParser.getText();
                try {
                    return format.parse(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // 注册新的模块到objectMapper
        mapper.registerModule(javaTimeModule);
        return mapper;
    }

    /**
     * 内部专用, 获取json对象
     * @return ObjectMapper
     */
    static ObjectMapper getTheMapper() {
        return MAPPER;
    }

    /**
     * 对象 转 json字符串
     * @param obj 对象
     * @return json 字符串
     * @throws BusinessException 序列化异常
     */
    public static String objectToJson(Object obj) throws BusinessException {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new BusinessException("Failed to serialize object to JSON", map -> {
                map.put("Object", obj != null ? obj : FinalVariable.NULL);
                map.put("Class", obj != null ? obj.getClass() : FinalVariable.NULL);
            }, e);
        }
    }

    /**
     * json字符串 转 对象
     * @param json         json 字符串
     * @param valueTypeRef 对象类型 eg: new TypeReference<ResultVO<List<List<String>>>>() {}
     * @param <T>          对象类型
     * @return 对象
     * @throws BusinessException 序列化异常
     */
    public static <T> T jsonToObject(String json, TypeReference<T> valueTypeRef) {
        try {
            return MAPPER.readValue(json, valueTypeRef);
        } catch (Throwable e) {
            throw new BusinessException("Failed to deserialize JSON to object", map -> {
                map.put("Json", json != null ? json : FinalVariable.NULL);
                map.put("TypeReference", valueTypeRef != null ? valueTypeRef : FinalVariable.NULL);
            }, e);
        }
    }

    /**
     * json字符串 转 对象
     * @param json         json 字符串
     * @param valueTypeRef 对象类型
     * @param <T>          对象类型
     * @return 对象
     * @throws BusinessException 序列化异常
     */
    public static <T> T jsonToObject(String json, Class<T> valueTypeRef) {
        try {
            return MAPPER.readValue(json, valueTypeRef);
        } catch (Throwable e) {
            throw new BusinessException("Failed to deserialize JSON to object", map -> {
                map.put("Json", json != null ? json : FinalVariable.NULL);
                map.put("Class", valueTypeRef != null ? valueTypeRef : FinalVariable.NULL);
            }, e);
        }
    }

    /**
     * json字符串 转 对象
     * @param json json 字符串
     * @param type 对象类型
     * @param <T>  对象类型
     * @return 对象
     * @throws BusinessException 序列化异常
     */
    public static <T> T jsonToObject(String json, Type type) {
        try {
            return MAPPER.readValue(json, new TypeReference<T>() {
                @Override
                public Type getType() {
                    return type;
                }
            });
        } catch (Throwable e) {
            throw new BusinessException("Failed to deserialize JSON to object", map -> {
                map.put("Json", json != null ? json : FinalVariable.NULL);
                map.put("Class", type != null ? type : FinalVariable.NULL);
            }, e);
        }
    }

}

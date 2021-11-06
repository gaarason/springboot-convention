package gaarason.convention.common.web.util;

import gaarason.convention.common.appointment.FinalVariable;
import org.springframework.beans.ConfigurablePropertyAccessor;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author xt
 */
public class LocalDateTimeUtils {

    /**
     * 注册 时间序列化
     * @param registry 注册器
     */
    public static void registerCustomEditorForLocalDateTime(final ConfigurablePropertyAccessor registry) {
        registry.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(final String text) throws IllegalArgumentException {
                setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_FORMAT)));
            }
        });
        registry.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(final String text) throws IllegalArgumentException {
                setValue(LocalDateTime.parse(text, DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_DATE_TIME_FORMAT)));
            }
        });
        registry.registerCustomEditor(LocalTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(final String text) throws IllegalArgumentException {
                setValue(LocalTime.parse(text, DateTimeFormatter.ofPattern(FinalVariable.Timestamp.DEFAULT_TIME_FORMAT)));
            }
        });
    }
}

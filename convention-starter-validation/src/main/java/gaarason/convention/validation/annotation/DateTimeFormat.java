package gaarason.convention.validation.annotation;

import gaarason.convention.validation.DateTimeFormatValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 验证时间字段的格式
 * @author xt
 */
@Documented
@Constraint(validatedBy = {DateTimeFormatValidator.class})
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface DateTimeFormat {

    /**
     * 模式描述字符
     */
    String dateTimeFormatterPattern() default "y-M-d H:m:s";

    String message() default "字段[${field}]的入参值[${value}]与预期时间格式[${dateTimeFormatterPattern}]不相符";

    /**
     * 是否允许为null, 为null则无须效验
     */
    boolean nullable() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

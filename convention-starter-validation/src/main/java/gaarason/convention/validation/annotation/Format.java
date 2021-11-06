package gaarason.convention.validation.annotation;

import gaarason.convention.validation.FormatValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 验证字段的格式
 * @author xt
 */
@Documented
@Constraint(validatedBy = {FormatValidator.class})
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Repeatable(Format.List.class)
public @interface Format {

    /**
     * 正则模式描述字符
     */
    String formatterPattern() default "";

    String message() default "字段[${field}]的入参值[${value}]与预期格式[${formatterPattern}]不相符";

    /**
     * 是否允许为null, 为null则无须效验
     */
    boolean nullable() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({FIELD, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        Format[] value();
    }
}

package gaarason.convention.validation.annotation;

import gaarason.convention.validation.NowValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 验证是否为当前时间(秒级时间戳)
 * @author xt
 */
@Documented
@Constraint(validatedBy = {NowValidator.class})
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Now {

    /**
     * 允许的误差时间,单位s
     */
    long permissibleError() default 5;

    /**
     * 是否允许为null, 为null则无须效验
     */
    boolean nullable() default false;

    String message() default "字段[${field}]的入参值[${value}]与当前时间戳[${timestamp}]的误差大于${permissibleError}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

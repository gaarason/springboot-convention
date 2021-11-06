package gaarason.convention.validation.annotation;

import gaarason.convention.validation.NumberRangeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 验证是数字是否在描述范围内
 * @author xt
 */
@Documented
@Constraint(validatedBy = {NumberRangeValidator.class})
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface NumberRange {

    long min() default 0;

    long max() default Long.MAX_VALUE;

    String message() default "字段[${field}]的入参值[${value}]应该在[${min}]到[${max}]的范围之间";

    /**
     * 是否允许为null, 为null则无须效验
     */
    boolean nullable() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

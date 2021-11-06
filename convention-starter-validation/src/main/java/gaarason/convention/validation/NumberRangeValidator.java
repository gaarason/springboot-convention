package gaarason.convention.validation;

import gaarason.convention.validation.annotation.NumberRange;
import org.springframework.lang.Nullable;

import javax.validation.ConstraintValidatorContext;

/**
 * @author xt
 */
public class NumberRangeValidator extends BaseValidator<NumberRange, Number> {

    private long min;

    private long max;

    private boolean nullable;

    @Override
    public void initialize(NumberRange constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
        nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(@Nullable Number value, ConstraintValidatorContext context) {

        boolean isValid = (nullable && value == null) || (value != null && value.longValue() >= min && value.longValue() <= max);

        return judge(isValid, value, context, (str) -> str.replace("${min}", String.valueOf(min)).replace("${max}", String.valueOf(max)));

    }
}

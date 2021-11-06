package gaarason.convention.validation;

import gaarason.convention.validation.annotation.Now;
import org.springframework.lang.Nullable;

import javax.validation.ConstraintValidatorContext;

/**
 * @author xt
 */
public class NowValidator extends BaseValidator<Now, Number> {

    /**
     * 允许误差
     */
    private long permissibleError;

    private boolean nullable;

    @Override
    public void initialize(Now constraintAnnotation) {
        permissibleError = constraintAnnotation.permissibleError();
        nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(@Nullable Number value, ConstraintValidatorContext context) {
        // 当前秒级时间戳
        int timeValue = Integer.parseInt(String.valueOf(System.currentTimeMillis() / 1000));
        boolean isValid = (nullable && value == null) || (value != null && Math.abs(value.longValue() - timeValue) < permissibleError);
        return judge(isValid, value, context,
            str -> str.replace("${timestamp}", Integer.toString(timeValue)).replace("${permissibleError}", String.valueOf(permissibleError)));

    }
}

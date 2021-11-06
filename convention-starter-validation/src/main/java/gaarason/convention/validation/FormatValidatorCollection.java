package gaarason.convention.validation;

import gaarason.convention.validation.annotation.Format;
import org.springframework.lang.Nullable;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * @author xt
 */
public class FormatValidatorCollection extends BaseValidator<Format, Collection<Object>> {

    /**
     * 模式描述字符
     */
    @Nullable
    private String formatterPattern;

    @Override
    public void initialize(Format constraintAnnotation) {
        formatterPattern = constraintAnnotation.formatterPattern();
    }

    @Override
    public boolean isValid(Collection<Object> values, ConstraintValidatorContext context) {
        boolean isValid = !values.isEmpty();
        for (Object value : values) {
            isValid = isValid && String.valueOf(value).matches(String.valueOf(formatterPattern));
        }
        return judge(isValid, values, context, (str) -> str.replace("${formatterPattern}", String.valueOf(formatterPattern)));

    }
}

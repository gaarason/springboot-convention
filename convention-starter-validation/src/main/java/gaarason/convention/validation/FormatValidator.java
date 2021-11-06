package gaarason.convention.validation;

import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.validation.annotation.Format;
import org.springframework.lang.Nullable;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Objects;

/**
 * @author xt
 */
public class FormatValidator extends BaseValidator<Format, Object> {

    /**
     * 模式描述字符
     */
    @Nullable
    private String formatterPattern;

    private boolean nullable;

    @Override
    public void initialize(Format constraintAnnotation) {
        formatterPattern = constraintAnnotation.formatterPattern();
        nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(@Nullable Object values, ConstraintValidatorContext context) {
        // 批量
        if (values instanceof Collection) {
            Collection<Object> valueCollection = ObjectUtils.typeCast(values);
            boolean isValid = !valueCollection.isEmpty();
            for (Object value : valueCollection) {
                isValid = isValid && String.valueOf(value).matches(Objects.requireNonNull(formatterPattern));
            }
            return judge(isValid, values, context, (str) -> str.replace("${formatterPattern}", String.valueOf(formatterPattern)));
        }

        // 单个
        boolean isValid = (nullable && values == null) || (values != null && String.valueOf(values).matches(String.valueOf(formatterPattern)));
        return judge(isValid, values, context, (str) -> str.replace("${formatterPattern}", String.valueOf(formatterPattern)));

    }
}

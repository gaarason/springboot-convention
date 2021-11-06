package gaarason.convention.validation;

import gaarason.convention.validation.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author xt
 */
public class DateTimeFormatValidator extends BaseValidator<DateTimeFormat, String> {

    /**
     * 模式对象
     */
    @Nullable
    private DateFormat formatter;

    /**
     * 模式描述字符
     */
    @Nullable
    private String dateTimeFormatterPattern;

    private boolean nullable;

    @Override
    public void initialize(DateTimeFormat constraintAnnotation) {
        nullable = constraintAnnotation.nullable();
        dateTimeFormatterPattern = constraintAnnotation.dateTimeFormatterPattern();
        formatter = new SimpleDateFormat(dateTimeFormatterPattern);
    }

    @Override
    public boolean isValid(@Nullable String value, ConstraintValidatorContext context) {
        boolean isValid;

        if (nullable && (value == null)) {
            isValid = true;
        } else {
            if (value == null) {
                isValid = false;
            } else {
                try {
                    assert formatter != null;
                    Date date = formatter.parse(value);
                    isValid = value.equals(formatter.format(date));
                } catch (Exception e) {
                    isValid = false;
                }
            }
        }
        return judge(isValid, value, context, (str) -> str.replace("${dateTimeFormatterPattern}", String.valueOf(dateTimeFormatterPattern)));

    }
}

package gaarason.convention.validation;

import gaarason.convention.common.util.StringUtils;
import gaarason.convention.validation.contract.JudgeFunctionalInterface;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintViolationCreationContext;
import org.springframework.lang.Nullable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @param <A> 注解
 * @param <T> a
 * @author xt
 */
public abstract class BaseValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    /**
     * 判断是否认证通过
     * @param isValid 是否认证通过
     * @param value   参数值
     * @param context 上下文
     * @param closure message处理
     * @return 是否认证通过
     */
    protected boolean judge(boolean isValid, @Nullable T value, ConstraintValidatorContext context, JudgeFunctionalInterface closure) {
        // 字段名
        String fieldKey = "";
        ConstraintValidatorContextImpl validatorContext = (ConstraintValidatorContextImpl) context;
        List<ConstraintViolationCreationContext> constraintViolationCreationContexts = validatorContext.getConstraintViolationCreationContexts();
        for (ConstraintViolationCreationContext constraintViolationCreationContext : constraintViolationCreationContexts) {
            fieldKey = StringUtils.humpToLine(constraintViolationCreationContext.getPath().asString());
        }

        String message = validatorContext.getDefaultConstraintMessageTemplate().replace("${field}", fieldKey).replace("${value}",
            String.valueOf(value));
        String apply = closure.isValid(message);

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(apply).addConstraintViolation();
        return isValid;
    }

}

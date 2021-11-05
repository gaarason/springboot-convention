package gaarason.convention.common.model.annotation.web;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不使用 参数解析
 *
 * @author xt
 */
@Documented
@Target({TYPE, PARAMETER})
@Retention(RUNTIME)
public @interface ExcludeUnifiedRequest {

}

package gaarason.convention.common.model.annotation.web;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 不使用 统一响应
 *
 * @author xt
 */
@Documented
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface ExcludeUnifiedResponse {

}

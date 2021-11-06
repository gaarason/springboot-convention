package gaarason.convention.common.web.contract;

import org.springframework.lang.Nullable;

/**
 * 用户可用的结果转化
 * @author xt
 */
public interface ResponseHandlerContract {

    /**
     * 统一结果转化
     * 一般不要更改响应的类型, 要改的话请充分测试.
     * @param result 原响应
     * @return 新响应
     */
    @Nullable
    Object conversion(@Nullable Object result);
}
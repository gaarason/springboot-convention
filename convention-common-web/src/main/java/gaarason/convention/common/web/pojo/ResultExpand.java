package gaarason.convention.common.web.pojo;

import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.util.ResultUtils;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * 结果类
 * @param <T> 实体
 * @author xt
 */
public class ResultExpand<T> extends ResultVO<T> {

    private static final long serialVersionUID = 1L;

    /**
     * 正常响应
     * @param data 待包装数据
     * @return 响应
     */
    public ResultVO<T> success(@Nullable T data) {
        return ResultUtils.success(this, data);
    }

    /**
     * 业务异常
     * @param e 异常信息
     * @return 响应
     */
    public ResultVO<T> warn(BusinessException e) {
        return ResultUtils.warn(this, e);
    }

    /**
     * 已识别的错误
     * @param statusCode 错误类型
     * @param message    消息
     * @param e          异常
     * @return 响应
     */
    public ResultVO<T> warn(StatusCode statusCode, @Nullable String message, Throwable e) {
        return ResultUtils.warn(this, statusCode, message != null ? message : statusCode.getMessage(), e);
    }

    /**
     * 已识别的错误
     * @param statusCode 错误类型
     * @param message    消息
     * @return 响应
     */
    public ResultVO<T> warn(StatusCode statusCode, String message) {
        return ResultUtils.warn(this, statusCode, message);
    }

    /**
     * 未识别的错误
     * @param e 异常
     * @return 响应
     */
    public ResultVO<T> error(Throwable e) {
        return ResultUtils.error(this, e);
    }
}

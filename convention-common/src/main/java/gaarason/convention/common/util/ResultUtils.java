package gaarason.convention.common.util;

import gaarason.convention.common.appointment.CommonVariable;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.provider.ChainProvider;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 响应构造
 * @author xt
 */
public final class ResultUtils {

    private ResultUtils() {

    }

    /**
     * 通用属性填充
     * @return 响应对象
     */
    public static ResultVO<Object> wrap(ResultVO<Object> originalResult) {
        originalResult.setTraceId(ChainProvider.get(ChainProvider.CanCrossProcessKey.TRACE_ID, FinalVariable.EMPTY_STRING));
        originalResult.setRequestUrl(ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_URL, FinalVariable.EMPTY_STRING));
        originalResult.setRequestDatetime(ChainProvider.get(ChainProvider.CanNotCrossProcessKey.REQUEST_DATETIME, FinalVariable.EMPTY_STRING));
        originalResult.setResponseDatetime(FinalVariable.NOW_DATETIME.get());
        originalResult.setApplicationName(CommonVariable.APPLICATION_NAME);
        return originalResult;
    }

    /**
     * 正常响应
     * @param data 待包装数据
     * @return 响应
     */
    @SuppressWarnings("unchecked")
    public static <T> ResultVO<T> success(T data) {
        // 如果已经是 ResultVO 类型，则直接返回
        if (data instanceof ResultVO) {
            return (ResultVO<T>) data;
        }
        // 如果不是，则包装成 ResultVO 类型
        return new ResultVO<>(data);
    }

    /**
     * 正常响应
     * @param resultVO 预处理对象
     * @param data     待包装数据
     * @return 响应
     */
    @SuppressWarnings("unchecked")
    public static <T> ResultVO<T> success(ResultVO<T> resultVO, T data) {
        // 如果已经是 ResultVO 类型，则直接返回
        // 将data中的公共属性,设置为resultVO的属性
        if (data instanceof ResultVO) {
            ResultVO<T> dataResultVO = (ResultVO<T>) data;
            dataResultVO.setRequestDatetime(resultVO.getRequestDatetime());
            dataResultVO.setResponseDatetime(resultVO.getResponseDatetime());
            dataResultVO.setRequestUrl(resultVO.getRequestUrl());
            dataResultVO.setTraceId(resultVO.getTraceId());
            return dataResultVO;
        } else {
            // 如果不是，则包装成 ResultVO 类型
            resultVO.setData(data);
            return resultVO;
        }
    }

    /**
     * 业务异常
     * @param e 异常信息
     * @return 响应
     */
    public static <T> ResultVO<T> warn(BusinessException e) {
        return ResultUtils.warn(new ResultVO<>(), e);
    }

    /**
     * 业务异常
     * @param resultVO 预处理对象
     * @param e        异常信息
     * @return 响应
     */
    public static <T> ResultVO<T> warn(ResultVO<T> resultVO, BusinessException e) {
        resultVO.setCode(e.getCode());
        resultVO.setMessage(e.getMessage());
        resultVO.setStackTrace(ResultUtils.exception2stack(ObjectUtils.typeCast(e)));
        resultVO.setException(ObjectUtils.typeCast(e));
        return resultVO;
    }

    /**
     * 已识别的错误
     * @param statusCode 错误类型
     * @param message    消息
     * @param e          异常
     * @return 响应
     */
    public static <T> ResultVO<T> warn(StatusCode statusCode, String message, @Nullable Throwable e) {
        return ResultUtils.warn(new ResultVO<>(), statusCode, message, e);
    }

    /**
     * 已识别的错误
     * @param resultVO   预处理对象
     * @param statusCode 错误类型
     * @param message    消息
     * @param e          异常
     * @return 响应
     */
    public static <T> ResultVO<T> warn(ResultVO<T> resultVO, StatusCode statusCode, String message, @Nullable Throwable e) {
        resultVO.setCode(statusCode.getCode());
        resultVO.setMessage(message);
        resultVO.setStackTrace(ResultUtils.exception2stack(e));
        resultVO.setException(e);
        return resultVO;
    }

    /**
     * 已识别的错误
     * @param statusCode 错误类型
     * @param message    消息
     * @return 响应
     */
    public static <T> ResultVO<T> warn(StatusCode statusCode, String message) {
        return ResultUtils.warn(new ResultVO<>(), statusCode, message);
    }

    /**
     * 已识别的错误
     * @param resultVO   预处理对象
     * @param statusCode 错误类型
     * @param message    消息
     * @return 响应
     */
    public static <T> ResultVO<T> warn(ResultVO<T> resultVO, StatusCode statusCode, String message) {
        resultVO.setCode(statusCode.getCode());
        resultVO.setMessage(message);
        return resultVO;
    }

    /**
     * 已识别的错误
     * @param resultVO 预处理对象
     * @param code     错误类型
     * @param message  消息
     * @param e        异常
     * @return 响应
     */
    public static <T> ResultVO<T> warn(ResultVO<T> resultVO, int code, String message, @Nullable Throwable e) {
        resultVO.setCode(code);
        resultVO.setMessage(message);
        resultVO.setStackTrace(ResultUtils.exception2stack(e));
        resultVO.setException(e);
        return resultVO;
    }

    /**
     * 未识别的错误
     * @param e 异常
     * @return 响应
     */
    public static ResultVO<?> error(Throwable e) {
        return ResultUtils.error(new ResultVO<>(), e);
    }

    /**
     * 未识别的错误
     * @param resultVO 预处理对象
     * @param e        异常
     * @return 响应
     */
    public static <T> ResultVO<T> error(ResultVO<T> resultVO, Throwable e) {
        resultVO.setCode(StatusCode.INTERNAL_ERROR.getCode());
        resultVO.setMessage(StatusCode.INTERNAL_ERROR.getMessage());
        resultVO.setStackTrace(ResultUtils.exception2stack(e));
        resultVO.setException(e);
        return resultVO;
    }

    /**
     * 获取异常堆栈信息
     * @param e 异常
     * @return 异常堆栈信息字符串
     */
    public static String exception2stack(@Nullable Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();
        if (e != null && CommonVariable.SHOULD_SHOW_STACK_TRACE) {
            StackTraceElement[] stackTraceArr = e.getStackTrace();
            stringBuilder.append(e.getClass().toString().replace("class ", "")).append(" : ").append(e.getMessage()).append(", ");
            stringBuilder.append(
                Arrays.stream(stackTraceArr).map(
                        s -> s.getClassName() + "." + s.getMethodName() + "(" + s.getFileName() + ":" + s.getLineNumber() + ")")
                    .reduce((one, two) -> one + ", " + two).orElse(null));
            Throwable cause = e.getCause();
            if (cause != null) {
                stringBuilder.append("; Caused by: ").append(ResultUtils.exception2stack(cause));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 将响应对象转化为异常
     * @param resultVO 响应对象
     * @return 异常
     */
    public static BusinessException toException(ResultVO<? extends Serializable> resultVO) {
        throw new BusinessException(resultVO.getCode(), resultVO.getMessage());
    }

}

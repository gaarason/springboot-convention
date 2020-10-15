package gaarason.springboot.convention.common.util;

import gaarason.springboot.convention.common.appointment.FinalVariable;
import gaarason.springboot.convention.common.config.SpringBootConfiguration;
import gaarason.springboot.convention.common.exception.BusinessException;
import gaarason.springboot.convention.common.pojo.ResultVO;
import gaarason.springboot.convention.common.pojo.StatusCode;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * 响应构造
 */
@Slf4j
public class ResultUtils {

    /**
     * 正常响应
     * @param data 待包装数据
     * @return 响应
     */
    public static ResultVO<?> success(Object data) {
        // 如果已经是 ResultVO 类型，则直接返回
        if (data instanceof ResultVO) {
            return (ResultVO<?>) data;
        }
        // 如果不是，则包装成 ResultVO 类型
        return new ResultVO<>(data);
    }

    /**
     * 业务异常
     * @param e 异常信息
     * @return 响应
     */
    public static ResultVO<?> warn(BusinessException e) {
        int code = e.getCode();
        String message = e.getMessage();
        String otherInfo = e.getDebug().toString();
        log.warn("【" + code + "】" + message + " | DEBUG_MAP ->【" + otherInfo + "】", e);
        return response(code, message, e, otherInfo);
    }

    /**
     * 已识别的错误
     * @param statusCode 错误类型
     * @param message    消息
     * @param e          异常
     * @return 响应
     */
    public static ResultVO<Object> warn(StatusCode statusCode, String message, Throwable e) {
        int code = statusCode.getCode();
        log.warn("【" + code + "】" + message, e);
        return response(code, message, e, "{}");
    }

    /**
     * 未识别的错误
     * @param e 异常
     * @return 响应
     */
    public static ResultVO<Object> error(Throwable e) {
        int code = StatusCode.INTERNAL_ERROR.getCode();
        String message = StatusCode.INTERNAL_ERROR.getMessage();
        log.error("【" + code + "】" + e.getMessage(), e);
        return response(code, message, e, "{}");
    }


    /**
     * 响应
     * @param code    状态码
     * @param message 信息
     * @param e       异常
     * @param other   异常
     * @return 响应对象
     */
    private static ResultVO<Object> response(int code, String message, Throwable e, String other) {
        boolean shouldPrint = Arrays.asList(FinalVariable.SHOW_STACK_TRACE_ENV).contains(SpringBootConfiguration.getActiveProfile());
        String information = null;
        if (shouldPrint) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw, true));
            information = sw.getBuffer().toString() + " | " + other;
        }
        return new ResultVO<>(code, message, information);
    }

}

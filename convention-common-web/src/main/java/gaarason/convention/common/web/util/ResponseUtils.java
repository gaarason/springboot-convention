package gaarason.convention.common.web.util;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.StatusCode;
import gaarason.convention.common.model.pojo.ResultVO;
import gaarason.convention.common.provider.LogProvider;
import gaarason.convention.common.util.ObjectUtils;
import gaarason.convention.common.util.SpringUtils;
import gaarason.convention.common.web.contract.ResponseHandlerContract;
import org.springframework.lang.Nullable;

import java.io.Serializable;

/**
 * @author xt
 */
public class ResponseUtils {

    /**
     * 响应转化, 并记录响应日志, 所有http响应的终点
     * @param resultOriginal 原响应
     * @return 转化后的响应
     */
    @Nullable
    public static Object responseAndLog(@Nullable final Object resultOriginal) {
        // 获取自定义对象
        final ResponseHandlerContract responseHandlerContract = SpringUtils.getBean("responseHandlerContract");
        // 执行转化
        final Object result = responseHandlerContract.conversion(resultOriginal);
        final LogProvider logProvider = LogProvider.getInstance();
        if (result instanceof ResultVO) {
            final ResultVO<Serializable> resultVO = ObjectUtils.typeCast(result);
            final int code = resultVO.getCode();
            // 正常日志
            if (code == StatusCode.SUCCESS.getCode()) {
                logProvider.printHttpProviderSendingResponseLog(resultVO::toString);
            } else if (code == StatusCode.INTERNAL_ERROR.getCode()) {
                // error 日志
                LogProvider.printHttpProviderSendingResponseErrorLog(resultVO, resultVO.getException());
            } else {
                // Warning 日志
                LogProvider.printHttpProviderSendingResponseWarningLog(resultVO, resultVO.getException());
            }
        } else {
            // 正常日志
            logProvider.printHttpProviderSendingResponseLog(() -> result == null ? FinalVariable.NULL : result.toString());
        }
        return result;
    }
}

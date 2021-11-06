package gaarason.convention.common.web.contract;

/**
 * 用户可用的异常转化
 * @author xt
 */
public interface ExceptionHandlerContract {

    /**
     * 转化异常
     * @param throwable 原异常
     * @return 新异常
     */
    Throwable conversion(Throwable throwable);

}
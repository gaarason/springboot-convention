package gaarason.convention.common.web.contract;

import gaarason.convention.common.web.pojo.GeneralRequest;

import java.util.Map;

/**
 * 用户可用的链路上下文配置
 * @author xt
 */
public interface ChainContextHandlerContract {

    /**
     * 用户可用的链路上下文配置
     * @param generalRequest 简易的request对象
     */
    void conversion(GeneralRequest<?> generalRequest);
}
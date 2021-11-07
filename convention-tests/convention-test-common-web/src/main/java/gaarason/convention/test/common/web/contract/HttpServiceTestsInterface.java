package gaarason.convention.test.common.web.contract;

import gaarason.convention.test.common.web.contract.trait.*;

/**
 * 服务测试接口
 * @author xt
 */
public interface HttpServiceTestsInterface extends
        // 统一参数解析
    HttpServiceTestsUnifiedRequestTrait,
        // 统一响应
        HttpServiceTestsUnifiedResponseTrait,
        // 统一异常处理
        HttpServiceTestsUnifiedResponseExceptionTrait,
        // 非统一参数解析
        HttpServiceTestsExcludeUnifiedRequestTrait,
        // 非统一响应
        HttpServiceTestsExcludeUnifiedResponseTrait,
        // 非统一响应
        HttpServiceTestsExcludeUnifiedResponseResponseEntityTrait {

    /**
     * 空白响应
     */
    void faviconIco();

    /**
     * httpUtils
     */
    void httpUtils();
}

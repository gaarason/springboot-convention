package gaarason.convention.test.common.web.contract;

import gaarason.convention.common.model.annotation.web.UnifiedResponse;
import gaarason.convention.test.common.web.contract.trait.*;

/**
 * @author xt
 */
@UnifiedResponse
public interface TestControllerInterface extends
    // 保留的原测试api
    TestControllerOldApiTrait,
    // 统一响应
    TestControllerUnifiedResponseTrait,
    // 统一异常响应
    TestControllerUnifiedResponseExceptionTrait,
    // 非统一响应(spring 原逻辑)
    TestControllerExcludeUnifiedResponseTrait,
    // 非统一响应(spring 原逻辑 - ResponseEntity响应)
    TestControllerExcludeUnifiedResponseResponseEntityTrait,
    // 统一参数解析
    TestControllerUnifiedRequestTrait,
    // 统一参数解析
    TestControllerExcludeUnifiedRequestTrait {

}

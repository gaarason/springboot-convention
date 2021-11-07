package gaarason.convention.test.common.web.contract.trait;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author xt
 */
public interface TestControllerUnifiedResponseExceptionTrait {

    /**
     * 统一响应, 业务异常
     */
    @GetMapping("unifiedResponseThrowBusinessException")
    void unifiedResponseThrowBusinessException();

    /**
     * 统一响应, 受检异常
     */
    @GetMapping("unifiedResponseThrowException")
    void unifiedResponseThrowException() throws Exception;
}

package gaarason.convention.test.common.web.contract.trait;

/**
 * @author xt
 */
public interface HttpServiceTestsUnifiedResponseExceptionTrait {

    /**
     * 统一响应, 业务异常
     */
    void unifiedResponseThrowBusinessException();

    /**
     * 统一响应, 受检异常
     */
    void unifiedResponseThrowException();
}

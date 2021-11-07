package gaarason.convention.test.common.web.service;


import gaarason.convention.common.model.exception.BusinessException;

/**
 * @author xt
 */
public interface TestService {

    /**
     * 获取名称
     * @param something 名称
     * @return 名称
     * @throws Exception         异常
     * @throws BusinessException 业务异常
     */
    String getServiceName(String something) throws Exception, BusinessException;
}

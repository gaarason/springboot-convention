package gaarason.convention.common.model.test;

import gaarason.convention.common.model.exception.BusinessException;
import org.junit.jupiter.api.Test;

/**
 * @author xt
 * @since 2021/12/3 11:10 上午
 */
class BusinessExceptionTests {

    @Test
    void printStackTrace(){
        final BusinessException businessException = new BusinessException("test", map -> {
            map.put("a", "av");
            map.put("b", "bv");
        });
        businessException.printStackTrace();
    }
}

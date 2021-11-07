package gaarason.convention.test.common.web.contract;

import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author xt
 */
public interface AnnotationControllerInterface {

    /**
     * 测试方法
     * @param jwtTokenRequestDto JwtTokenRequestDto
     * @return String
     */
    @GetMapping("test")
    default String test(final JwtTokenRequestDto jwtTokenRequestDto) {
        return String.valueOf(jwtTokenRequestDto.getValidTime());
    }
}

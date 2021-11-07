package gaarason.convention.test.common.web.contract.trait;

import gaarason.convention.common.model.annotation.web.ExcludeUnifiedResponse;
import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * @author xt
 */
public interface TestControllerExcludeUnifiedResponseResponseEntityTrait {

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<String>
     * @return ResponseEntity 结果
     */
    @GetMapping("excludeUnifiedResponseResponseEntityString")
    @ExcludeUnifiedResponse
    ResponseEntity<String> excludeUnifiedResponseResponseEntityString();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<自定义java对象>
     * @return ResponseEntity 结果
     */
    @GetMapping("excludeUnifiedResponseResponseEntityObject")
    @ExcludeUnifiedResponse
    ResponseEntity<JwtTokenRequestDto> excludeUnifiedResponseResponseEntityObject();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<Boolean>
     * @return ResponseEntity s
     */
    @GetMapping("excludeUnifiedResponseResponseEntityBoolean")
    @ExcludeUnifiedResponse
    ResponseEntity<Boolean> excludeUnifiedResponseResponseEntityBoolean();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<Void>
     * @return ResponseEntity
     */
    @GetMapping("excludeUnifiedResponseResponseEntityVoid")
    @ExcludeUnifiedResponse
    ResponseEntity<Void> excludeUnifiedResponseResponseEntityVoid();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<Number>
     * @return ResponseEntity r
     */
    @GetMapping("excludeUnifiedResponseResponseEntityNumber")
    @ExcludeUnifiedResponse
    ResponseEntity<Number> excludeUnifiedResponseResponseEntityNumber();

    /**
     * 非统一响应, 接口响应定义为ResponseEntity<Map<?,?>>
     * @return ResponseEntity
     */
    @GetMapping("excludeUnifiedResponseResponseEntityMap")
    @ExcludeUnifiedResponse
    ResponseEntity<Map<?, ?>> excludeUnifiedResponseResponseEntityMap();
}

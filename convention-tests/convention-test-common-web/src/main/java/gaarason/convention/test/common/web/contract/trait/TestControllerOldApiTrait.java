package gaarason.convention.test.common.web.contract.trait;

import gaarason.convention.common.model.annotation.web.ExcludeUnifiedRequest;
import gaarason.convention.common.model.annotation.web.ExcludeUnifiedResponse;
import gaarason.convention.test.common.web.pojo.FormatCollectionValidatorRequestDTO;
import gaarason.convention.test.common.web.pojo.FormatValidatorRequestDTO;
import gaarason.convention.test.common.web.pojo.JwtTokenRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author xt
 */
public interface TestControllerOldApiTrait {

    /**
     * businessError
     * @return String s
     */
    @GetMapping("/business/error")
    String businessError();

    /**
     * businessError
     * @return String
     * @throws Exception e异常
     */
    @GetMapping("/checked/error")
    String checkedError() throws Exception;

    /**
     * noError
     * @return String s
     */
    @GetMapping("/no/error")
    String noError();

    /**
     * businessError
     * @return String s
     */
    @GetMapping("/no/error/many")
    List<String> noErrorMany();

    /**
     * noResponse
     */
    @GetMapping("/no/response")
    void noResponse();

    /**
     * html1
     * @return ResponseEntity s
     */
    @GetMapping("/html/1")
    @ExcludeUnifiedResponse
    ResponseEntity<String> html1();

    /**
     * html2
     * @return String s
     */
    @GetMapping("/html/2")
    @ExcludeUnifiedResponse
    String html2();

    /**
     * html3
     * @param jwtTokenRequestDTO qi
     * @return ResponseEntity s
     */
    @PostMapping("/html/3")
    @ExcludeUnifiedResponse
    ResponseEntity<JwtTokenRequestDto> html3(JwtTokenRequestDto jwtTokenRequestDTO);

    /**
     * html4
     * @return ResponseEntity s
     */
    @GetMapping("/html/4")
    @ExcludeUnifiedResponse
    ResponseEntity<Boolean> html4();

    /**
     * parameterAuthenticationError
     * @return String s
     */
    @GetMapping
    String parameterAuthenticationError();

    /**
     * 参数错误
     * @param jwtTokenRequestDTO 请求
     * @return JwtTokenRequestDto j
     */
    @PostMapping("/parameter/error")
    JwtTokenRequestDto parameterError(@Validated JwtTokenRequestDto jwtTokenRequestDTO);

    /**
     * parameterString
     * @param str s
     * @return String
     */
    @GetMapping("/parameter/string")
    String parameterString(String str);

    /**
     * parameterErrorGet
     * @param jwtTokenRequestDTO 请求体
     * @return JwtTokenRequestDto
     */
    @GetMapping("/parameter/error")
    JwtTokenRequestDto parameterErrorGet(@Validated JwtTokenRequestDto jwtTokenRequestDTO);

    /**
     * parameterErrorGet
     * @param jwtTokenRequestDTO 请求体
     * @return JwtTokenRequestDto
     */
    @PostMapping("/parameter/no/error")
    JwtTokenRequestDto parameterNoError(@RequestBody JwtTokenRequestDto jwtTokenRequestDTO);

    /**
     * parameterErrorGet
     * @param jwtTokenRequestDTO 请求体
     * @return JwtTokenRequestDto
     */
    @PostMapping("/parameter/exclude")
    JwtTokenRequestDto parameterExclude(@ExcludeUnifiedRequest JwtTokenRequestDto jwtTokenRequestDTO);

    /**
     * parameterErrorGet
     * @return String s
     */
    @GetMapping("/app/name")
    String appName();

    /**
     * validationSingle
     * @param formatValidatorRequestDTO 请求体
     * @return FormatValidatorRequestDTO
     */
    @PostMapping("/validation/single")
    FormatValidatorRequestDTO validationSingle(@Validated FormatValidatorRequestDTO formatValidatorRequestDTO);

    /**
     * parameterErrorGet
     * @param formatCollectionValidatorRequestDTO 请求体
     * @return FormatCollectionValidatorRequestDTO
     */
    @PostMapping("/validation/collection")
    FormatCollectionValidatorRequestDTO validationCollection(@Validated FormatCollectionValidatorRequestDTO formatCollectionValidatorRequestDTO);

    /**
     * request
     * @throws InterruptedException 中断异常
     */
    @PostMapping("/request")
    void request() throws InterruptedException;
}

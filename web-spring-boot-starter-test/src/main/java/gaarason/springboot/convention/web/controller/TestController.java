package gaarason.springboot.convention.web.controller;


import gaarason.springboot.convention.common.exception.BusinessException;
import gaarason.springboot.convention.common.pojo.StatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/business/error")
    public String businessError() {
        throw new BusinessException(StatusCode.E400001);
    }

    @GetMapping("/checked/error")
    public String checkedError() throws Exception {
        throw new Exception("这是一个受检异常");
    }

    @GetMapping("/no/error")
    public String noError() {
        return "这是一个正常响应结果";
    }

    @GetMapping
    public String parameterAuthenticationError() {
        return "OK";
    }

}

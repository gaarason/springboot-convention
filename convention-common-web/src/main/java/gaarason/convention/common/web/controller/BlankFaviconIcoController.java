package gaarason.convention.common.web.controller;

import gaarason.convention.common.model.annotation.web.ExcludeUnifiedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author xt
 */
public interface BlankFaviconIcoController {

    /**
     * http://xxx/favicon.ico
     * 可以使用 convention.http.generate-blank-favicon-ico = false 关闭
     * @return 空白
     */
    @GetMapping("/favicon.ico")
    @ExcludeUnifiedResponse
    ResponseEntity<String> favicon();
}

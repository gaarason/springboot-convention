package gaarason.convention.common.web.controller.impl;

import gaarason.convention.common.web.controller.BlankFaviconIcoController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * @author xt
 */
@Controller
public class BlankFaviconIcoControllerImpl implements BlankFaviconIcoController {

    @Override
    public ResponseEntity<String> favicon() {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("");
    }
}

package gaarason.convention.test.common.web.controller;

import gaarason.convention.common.model.annotation.web.UnifiedResponse;
import gaarason.convention.test.common.web.contract.AnnotationControllerInterface;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xt
 */

@UnifiedResponse
@RequestMapping("/AnnotationUnifiedResponseController")
@RestController
public class AnnotationUnifiedResponseController implements AnnotationControllerInterface {

}

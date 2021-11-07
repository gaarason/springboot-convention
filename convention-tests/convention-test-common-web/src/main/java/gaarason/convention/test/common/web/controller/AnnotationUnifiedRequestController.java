package gaarason.convention.test.common.web.controller;

import gaarason.convention.common.model.annotation.web.UnifiedRequest;
import gaarason.convention.test.common.web.contract.AnnotationControllerInterface;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xt
 */

@UnifiedRequest
@RequestMapping("/AnnotationUnifiedRequestController")
@RestController
public class AnnotationUnifiedRequestController implements AnnotationControllerInterface {

}

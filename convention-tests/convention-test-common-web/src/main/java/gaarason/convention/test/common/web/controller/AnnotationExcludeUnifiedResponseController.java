package gaarason.convention.test.common.web.controller;

import gaarason.convention.common.model.annotation.web.ExcludeUnifiedResponse;
import gaarason.convention.test.common.web.contract.AnnotationControllerInterface;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xt
 */
@ExcludeUnifiedResponse
@RequestMapping("/AnnotationExcludeUnifiedResponseController")
@RestController
public class AnnotationExcludeUnifiedResponseController implements AnnotationControllerInterface {

}

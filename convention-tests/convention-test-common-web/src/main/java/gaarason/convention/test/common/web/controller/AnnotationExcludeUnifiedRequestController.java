package gaarason.convention.test.common.web.controller;

import gaarason.convention.common.model.annotation.web.ExcludeUnifiedRequest;
import gaarason.convention.test.common.web.contract.AnnotationControllerInterface;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xt
 */
@ExcludeUnifiedRequest
@RequestMapping("/AnnotationExcludeUnifiedRequestController")
@RestController
public class AnnotationExcludeUnifiedRequestController implements AnnotationControllerInterface {

}

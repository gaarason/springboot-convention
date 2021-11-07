package gaarason.convention.test.common.web.service;

/**
 * @author xt
 */
public interface TestHttpProviderService {

    /**
     * 正常响应
     */
    void normal();

    /**
     * 正常响应数组
     */
    void normalMany();

    /**
     * 业务异常
     */
    void businessError();

    /**
     * 受检异常
     */
    void exceptionError();

    /**
     * 路由不存在
     */
    void uriNotFound();

    /**
     * http method不存在
     */
    void methodNotFound();

    /**
     * 不使用自定义的参数解析
     */
    void exclude();

    /**
     * 参数类型异常
     */
    void requestTypeError();

    /**
     * 不使用统一响应, 直接响应原文
     */
    void noUnifiedResponse1();

    /**
     * 不使用统一响应, 直接响应原文
     */
    void noUnifiedResponse2();

    /**
     * 不使用统一响应, 直接响应原文
     */
    void noUnifiedResponse3();

    /**
     * 不使用统一响应, 直接响应原文
     */
    void noUnifiedResponse4();

    /**
     * void接口返回
     */
    void noResponse();

    /**
     * 服务连接出错 Illegal character
     */
    void connectionErrorIllegalCharacter();

    /**
     * 服务连接出错 InvalidURI
     */
    void connectionErrorInvalidUri();

    /**
     * get url参数赋值
     */
    void paramGetQuery();

    /**
     * get url参数赋值, 哦控制器参数声明为 String
     */
    void paramGetString();

    /**
     * 提供默认的 /favicon.ico 路由返回空白
     */
    void faviconIco();

    /**
     * get form参数赋值
     */
    void paramGetForm();

    /**
     * post json参数赋值
     */
    void paramPostJson();

    /**
     * get url参数认证
     */
    void paramGetQueryValidated();

    /**
     * get form参数认证
     */
    void paramGetFormValidated();

    /**
     * post json参数认证
     */
    void paramPostJsonValidated();

    /**
     * get url参数认证异常
     */
    void paramGetQueryValidatedError();

    /**
     * get form参数认证异常
     */
    void paramGetFormValidatedError();

    /**
     * post json参数认证异常
     */
    void paramPostJsonValidatedError();

}

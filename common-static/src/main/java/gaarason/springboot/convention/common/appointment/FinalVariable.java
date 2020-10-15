package gaarason.springboot.convention.common.appointment;

/**
 * 全局可用的常量
 */
public class FinalVariable {
    /**
     * 当出现异常时, 以下的环境中, 在响应中体现异常堆栈
     */
    public static final String[] SHOW_STACK_TRACE_ENV = {"default", "dev", "test"};
}

package gaarason.convention.common.appointment;

import gaarason.convention.common.util.SpringUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 全局可用的常量(需要初始化, 依赖外部执行顺序)
 * @author xt
 */
public class CommonVariable {

    /**
     * 当前进程是否应该在响应中打印堆栈
     */
    public static final boolean SHOULD_SHOW_STACK_TRACE = Arrays.asList(FinalVariable.SHOW_STACK_TRACE_ENV).contains(SpringUtils.getActiveProfile());

    /**
     * 当前应用的名称( spring.application.name/dubbo.application.name )
     */
    public static final String APPLICATION_NAME =
        Arrays.stream(FinalVariable.APPLICATION_NAME_KEY).map(key -> SpringUtils.getContext().getEnvironment().getProperty(key)).filter(
            Objects::nonNull)
            .limit(1).reduce((s, d) -> s).orElse("No application name found");

    private CommonVariable() {
        
    }
}

package gaarason.convention.common.autoconfigure;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.util.SpringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.Objects;

/**
 * Logging 初始化
 * 使 log4j2-spring.xml 可以读到配置信息
 * @author xt
 */
public class LoggingEnvironmentPreparedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    private static final Logger LOGGER = LogManager.getLogger(LoggingEnvironmentPreparedListener.class);

    private static final String LOGGING_LISTENER_NAME = "loggingListenerName";

    /**
     * 仅需要其中的默认值
     */
    private static final ConventionProperties DEFAULT_PROPERTIES = new ConventionProperties();

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        // 使 log4j2-spring.xml 可以读到配置信息
        turnProperties(event);
    }

    /**
     * 将spring的配置, 赋值到system中, 让xml可以读到
     * @param event 事件
     */
    private void turnProperties(ApplicationEnvironmentPreparedEvent event) {
        final ConfigurableEnvironment environment = event.getEnvironment();
        final ConventionProperties.LogSpring logSpring = DEFAULT_PROPERTIES.getLogSpring();
        // springApplicationName
        final String springApplicationName =
            Arrays.stream(FinalVariable.APPLICATION_NAME_KEY).map(key -> event.getEnvironment().getProperty(key)).filter(Objects::nonNull)
                .limit(1).reduce((s, d) -> s).orElse("No application name found");
        System.setProperty("spring.application.name", springApplicationName);

        SpringUtils.turnSpringPropertyToSystem(environment, "spring.profiles.active", "default");

        SpringUtils.turnSpringPropertyToSystem(environment, "ykj.log-spring.file-dir", logSpring.getFileDir());

        SpringUtils.turnSpringPropertyToSystem(environment, "ykj.log-spring.console-pattern", logSpring.getConsolePattern());

        SpringUtils.turnSpringPropertyToSystem(environment, "ykj.log-spring.console-level", logSpring.getConsoleLevel());

        SpringUtils.turnSpringPropertyToSystem(environment, "ykj.log-spring.json-pattern", logSpring.getJsonPattern());

        SpringUtils.turnSpringPropertyToSystem(environment, "ykj.log-spring.json-level", logSpring.getJsonLevel());

        SpringUtils.turnSpringPropertyToSystem(environment, "ykj.log-spring.skywalking-pattern", logSpring.getSkywalkingPattern());

        SpringUtils.turnSpringPropertyToSystem(environment, "ykj.log-spring.skywalking-level", logSpring.getSkywalkingLevel());
    }

    @Override
    public int getOrder() {
        // 非常关键
        return Ordered.HIGHEST_PRECEDENCE + 19;
    }
}

package gaarason.springboot.convention.common.contract;

import java.util.Map;

@FunctionalInterface
public interface ErrorLogFunctionalInterface {

    void run(Map<Object, Object> map);
}

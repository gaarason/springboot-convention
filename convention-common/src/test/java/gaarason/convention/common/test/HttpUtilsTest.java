package gaarason.convention.common.test;

import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.util.HttpUtils;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author xt
 * @since 2021/11/29 5:07 下午
 */
class HttpUtilsTest {

    private static final Logger LOGGER = LogManager.getLogger();


    @Test
    void calculationInterval() {
        Assertions.assertEquals(1, HttpUtils.calculationInterval(1, 0, 2, 1, 0));

        Assertions.assertEquals(2, HttpUtils.calculationInterval(2, 0, 2, 1, 0));

        Assertions.assertEquals(4, HttpUtils.calculationInterval(1, 4, 2, 1, 0));

        Assertions.assertEquals(2, HttpUtils.calculationInterval(1, 4, 2, 1, 2));

        Assertions.assertEquals(2, HttpUtils.calculationInterval(1, 4, 2, 1, 2));

        Assertions.assertEquals(2, HttpUtils.calculationInterval(2, 4, 2, 1, 9));

        Assertions.assertEquals(4, HttpUtils.calculationInterval(3, 4, 2, 1, 9));

        Assertions.assertEquals(8, HttpUtils.calculationInterval(4, 4, 2, 1, 9));

        Assertions.assertEquals(9, HttpUtils.calculationInterval(5, 4, 2, 1, 9));
    }

    @Test
    void request() {
        HttpUtils.HttpRetryOption option = new HttpUtils.HttpRetryOption();
        option.setRetryAfter(2000);
        option.setBasicInterval(100L);
        option.setIncrementMultiple(2);
        option.setMaxRetryTime(5);
        option.setMaxInterval(1L);


        Map<String, Object> map = new HashMap<>(16);
        map.put("ssss", null);
        map.put(null, null);
        HttpUtils.HttpResult exec = HttpUtils.request().url("https://baidu.com", map).exec(3);

        boolean successful = exec.getResponse().isSuccessful();
        LOGGER.info(successful);

        ResponseBody body = exec.getResponse().body();
        LOGGER.info(body);

        Headers headers = exec.getResponse().headers();
        LOGGER.info(headers);

        HttpUtils.HttpResult exec3 =
            HttpUtils.request().setMethod(FinalVariable.Http.Method.POST).url("https://custom-web.test.myspacex.cn/mpopen/contract/new-detail?t=3&o=njdtzy")
                .setJsonBody("{\"id\":\"39fc7693-2a57-e6be-6ff6-a51bb826a074\"}").setHeader("x-jwt-tenant", "njdtzy").exec(3);

        HttpUtils.HttpResult exec4 =
            HttpUtils.request().setMethod(FinalVariable.Http.Method.POST).url("https://custom-web.test.myspacex.cn/mpopen/contract/new-detail?t=4&o=njdtzy")
                .setJsonBody("{\"id\":\"39fc7693-2a57-e6be-6ff6-a51bb826a074\"}").exec(3);
        LOGGER.info(exec3);
        LOGGER.info(exec4);

        // 同步请求 3次重试失败
        Assertions.assertThrows(BusinessException.class, () -> {
            HttpUtils.request().setHttpRetryOption(option).setMethod(FinalVariable.Http.Method.POST).url("https://custom-web.test.myspacexPPPPP.cn/mpopen/contract/new-detail?t=3&o=njdtzy")
                .setJsonBody("{\"id\":\"39fc7693-2a57-e6be-6ff6-a51bb826a074\"}").setHeader("x-jwt-tenant", "njdtzy").exec(3);
        });

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        // 异步请求 3次重试失败]
        HttpUtils.request().setHttpRetryOption(option).setMethod(FinalVariable.Http.Method.POST).url("https://custom-web.test.myspacexPPPPP.cn/mpopen/contract/new-detail?t=3&o=njdtzy")
            .setJsonBody("{\"id\":\"39fc7693-2a57-e6be-6ff6-a51bb826a074\"}").setHeader("x-jwt-tenant", "njdtzy").exec((httpResult, throwable) ->{
            System.out.println("over");
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

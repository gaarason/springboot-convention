package gaarason.convention.test.common.web.run;

import gaarason.convention.common.util.EncryptionRc4Utils;
import gaarason.convention.common.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author xt
 */
public class UtilsTest {

    private static final Logger LOGGER = LogManager.getLogger();

    @Test
    public void stringUtilsObjectToJson() {

    }

    @Test
    public void stringUtilsJsonToObject1() {

    }

    @Test
    public void stringUtilsJsonToObject2() {

    }

    @Test
    public void stringUtilsObjectToObject1() {

    }

    @Test
    public void stringUtilsObjectToObject2() {

    }

    @Test
    public void stringUtilsMapToQuerySearch() {
        final HashMap<String, Object> objectObjectHashMap = new HashMap<>(16);
        objectObjectHashMap.put("a", "aV");
        objectObjectHashMap.put("b", "bV");
        objectObjectHashMap.put("c", "cV");
        objectObjectHashMap.put("d", Arrays.asList("z", "x", "x"));
        objectObjectHashMap.put("e", Arrays.asList("z", "x", Arrays.asList("z", "x", "x")));
        final String urlParamsByMap = StringUtils.mapToQuerySearch(objectObjectHashMap);
        UtilsTest.LOGGER.info(urlParamsByMap);

    }

    @Test
    public void stringUtilsLineToHump() {
        // 默认小驼峰
        Assertions.assertEquals("stuTea", StringUtils.lineToHump("stu_tea"));
        Assertions.assertEquals("stuTeaOok", StringUtils.lineToHump("stu_tea_ook"));
        Assertions.assertEquals("stuTeaOok", StringUtils.lineToHump("stu_tea_ook__"));
        Assertions.assertEquals("stuTea", StringUtils.lineToHump("_stu_tea"));
        Assertions.assertEquals("stuTea", StringUtils.lineToHump("__stu_tea"));

        // 指定小驼峰
        Assertions.assertEquals("stuTea", StringUtils.lineToHump("stu_tea", false));
        Assertions.assertEquals("stuTeaOok", StringUtils.lineToHump("stu_tea_ook", false));
        Assertions.assertEquals("stuTeaOok", StringUtils.lineToHump("stu_tea_ook__", false));
        Assertions.assertEquals("stuTea", StringUtils.lineToHump("_stu_tea", false));
        Assertions.assertEquals("stuTea", StringUtils.lineToHump("__stu_tea", false));

        // 指定大驼峰
        Assertions.assertEquals("StuTea", StringUtils.lineToHump("stu_tea", true));
        Assertions.assertEquals("StuTeaOok", StringUtils.lineToHump("stu_tea_ook", true));
        Assertions.assertEquals("StuTeaOok", StringUtils.lineToHump("stu_tea_ook__", true));
        Assertions.assertEquals("StuTea", StringUtils.lineToHump("_stu_tea", true));
        Assertions.assertEquals("StuTea", StringUtils.lineToHump("__stu_tea", true));

        // 其他情况
        Assertions.assertEquals("stuTea", StringUtils.lineToHump("stuTea"));
        Assertions.assertEquals("stuTeaOok", StringUtils.lineToHump("stu_teaOok"));

    }

    @Test
    public void stringUtilsHumpToLine() {

    }

    /**
     * 下划线转中划线并保持驼峰测试
     */
    @Test
    public void stringUtilsToCenterLine() {

    }

    @Test
    public void stringUtilsLtrim() {

    }

    @Test
    public void stringUtilsRtrim() {

    }

    @Test
    public void stringUtilsIsJavaIdentifier() {

    }

    @Test
    public void stringUtilsIsJavaKeyword() {

    }

    @Test
    public void stringUtilsMd5() {

    }

    @Test
    public void stringUtilsReadBytes1() {

    }

    @Test
    public void stringUtilsReadBytes2() {

    }

    @Test
    public void rc4() {
        final String s1 = (EncryptionRc4Utils.encrypt("request_time:1620897722", "1a8ce7910f7bb946d06dd3154f2f6991"));
        final String s2 = (EncryptionRc4Utils.encrypt("request_time:1620897722", "1a8ce7910f7bb946d06dd3154f2f6991"));

        UtilsTest.LOGGER.info(s1);
        Assertions.assertEquals(s1, "fTJ1lmPXo9Hlo3uhJoxpj6iFjALbfKs=");
        UtilsTest.LOGGER.info(s2);
        Assertions.assertEquals(s2, "fTJ1lmPXo9Hlo3uhJoxpj6iFjALbfKs=");
    }
}

package gaarason.convention.common.test;

import gaarason.convention.common.util.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xt
 * @since 2021/11/25 5:07 下午
 */
class StringUtilsTest {

    @Test
    void mapToQuerySearch() {
        Assertions.assertEquals("", StringUtils.mapToQuerySearch(null));
        Assertions.assertEquals("", StringUtils.mapToQuerySearch(new HashMap<>()));

        final Map<String, Object> map1 = new LinkedHashMap<>();
        map1.put("name", "xiaozhan");
        map1.put("age", 1);
        map1.put("sex", 0L);
        Assertions.assertEquals("name=xiaozhan&age=1&sex=0", StringUtils.mapToQuerySearch(map1));


        final Map<String, Object> map2 = new LinkedHashMap<>();
        map2.put("info", map1);
        map2.put("class", "1");
        map2.put("group", "A group");
        // urlencode(info[name]=xiaozhan&info[age]=1&info[sex]=0&class=1&group=A group)
        Assertions.assertEquals("info%5Bname%5D=xiaozhan&info%5Bage%5D=1&info%5Bsex%5D=0&class=1&group=A+group", StringUtils.mapToQuerySearch(map2));

        final Map<String, Object> map3 = new LinkedHashMap<>();
        map3.put("info", map1);
        map3.put("class", "1");
        map3.put("group", "A group");
        // urlencode(class=1&group=A group&info[age]=1&info[name]=xiaozhan&info[sex]=0)
        Assertions.assertEquals("class=1&group=A+group&info%5Bage%5D=1&info%5Bname%5D=xiaozhan&info%5Bsex%5D=0", StringUtils.mapToQuerySearch(map3, true));
    }

    @Test
    void lowerFirstChar() {
        Assertions.assertEquals("aBC", StringUtils.lowerFirstChar("ABC"));
        Assertions.assertEquals(".ABC", StringUtils.lowerFirstChar(".ABC"));
        Assertions.assertEquals("123", StringUtils.lowerFirstChar("123"));
        Assertions.assertEquals("_A123", StringUtils.lowerFirstChar("_A123"));
        Assertions.assertEquals("", StringUtils.lowerFirstChar(""));
        Assertions.assertEquals("", StringUtils.lowerFirstChar(null));
    }

    @Test
    void lineToHump(){
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
        Assertions.assertEquals("", StringUtils.lineToHump(""));
        Assertions.assertEquals("", StringUtils.lineToHump(null));
    }

    @Test
    void humpToLine(){
        Assertions.assertEquals("stu_tea", StringUtils.humpToLine("stuTea"));
        Assertions.assertEquals("stu_tea_ook", StringUtils.humpToLine("stuTeaOok"));
    }

//    /**
//     * 移除字符串左侧的所有character
//     * @param str       原字符串
//     * @param character 将要移除的字符
//     * @return 处理后的字符
//     */
//    public static String ltrim(String str, String character) {
//        int length = character.length();
//        if ("".equals(str) || str.length() < length) {
//            return str;
//        }
//        return str.substring(0, length).equals(character) ? StringUtils.ltrim(str.substring(length), character) : str;
//    }
//
//    /**
//     * 移除字符串右侧的所有character
//     * @param str       原字符串
//     * @param character 将要移除的字符
//     * @return 处理后的字符
//     */
//    public static String rtrim(String str, String character) {
//        int length = character.length();
//        if ("".equals(str) || str.length() < length) {
//            return str;
//        }
//        return str.substring(str.length() - length).equals(character) ? StringUtils.rtrim(str.substring(0, str.length() - length), character) : str;
//    }
//
//    /**
//     * 是否是合法的标识符
//     * @param input 输入字符串
//     * @return 是否
//     */
//    public static boolean isJavaIdentifier(@Nullable String input) {
//        if (input != null && input.length() > 0) {
//            int pos = 0;
//            if (Character.isJavaIdentifierStart(input.charAt(pos))) {
//                while (++pos < input.length()) {
//                    if (!Character.isJavaIdentifierPart(input.charAt(pos))) {
//                        return false;
//                    }
//                }
//                return !StringUtils.isJavaKeyword(input);
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 是否为java关键字
//     * @param input 输入字符串
//     * @return 是否
//     */
//    public static boolean isJavaKeyword(String input) {
//        List<String> keyList = Arrays.asList(StringUtils.JAVA_KEYWORDS);
//        return keyList.contains(input);
//    }
//
//    /**
//     * md5
//     * @param input 输入
//     * @return 输出
//     */
//    public static String md5(String input) {
//        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
//        try {
//            byte[] btInput = input.getBytes(StandardCharsets.UTF_8);
//            // 获得MD5摘要算法的 MessageDigest 对象
//            MessageDigest mdInst = MessageDigest.getInstance("MD5");
//            // 使用指定的字节更新摘要
//            mdInst.update(btInput);
//            // 获得密文
//            byte[] md = mdInst.digest();
//            // 把密文转换成十六进制的字符串形式
//            int j = md.length;
//            char[] str = new char[j * 2];
//            int k = 0;
//            for (byte byte0 : md) {
//                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
//                str[k++] = hexDigits[byte0 & 0xf];
//            }
//            return new String(str);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 通过BufferedReader和字符编码集转换成byte数组
//     * @param br                 BufferedReader
//     * @param originallyEncoding 原输入字符集
//     * @return byte数组
//     */
//    public static byte[] readBytes(BufferedReader br, String originallyEncoding) {
//        try {
//            StringBuilder retStr = new StringBuilder();
//            int read;
//            while ((read = br.read()) != -1) {
//                retStr.append((char) read);
//            }
//            if (retStr.length() > 0) {
//                return retStr.toString().getBytes(Charset.forName(originallyEncoding));
//            }
//            return new byte[0];
//        } catch (Throwable e) {
//            throw new BusinessException(e);
//        }
//    }
//
//    /**
//     * InputStream 2 ByteArrayOutputStream
//     * @param is InputStream
//     * @return ByteArrayOutputStream
//     */
//    public static ByteArrayOutputStream readBytes(InputStream is) {
//        try {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            int i;
//            while ((i = is.read()) != -1) {
//                byteArrayOutputStream.write(i);
//            }
//            return byteArrayOutputStream;
//        } catch (Throwable e) {
//            throw new BusinessException(e);
//        }
//    }
//
//    /**
//     * 是否为空字符
//     * @param str 字符串
//     * @return bool
//     */
//    public static boolean hasText(@Nullable String str) {
//        return (str != null && !str.isEmpty() && containsText(str));
//    }
//
//    /**
//     * 是否为空字符
//     * @param str 字符串
//     * @return bool
//     */
//    private static boolean containsText(CharSequence str) {
//        int strLen = str.length();
//        for (int i = 0; i < strLen; i++) {
//            if (!Character.isWhitespace(str.charAt(i))) {
//                return true;
//            }
//        }
//        return false;
//    }
}

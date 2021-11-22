package gaarason.convention.common.util;

import gaarason.convention.common.model.exception.BusinessException;
import gaarason.convention.common.model.exception.StatusCode;
import org.springframework.lang.Nullable;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串 工具
 * @author xt
 */
public class StringUtils {

    /**
     * java 关键字
     */
    private static final String[] JAVA_KEYWORDS = {"abstract", "case", "continue", "enum", "for", "instanceof", "new", "return", "switch", "transient",
        "assert", "catch", "default", "extends", "goto", "int", "package", "short", "synchronized", "try", "boolean", "char", "do", "final", "if", "interface",
        "private", "static", "this", "void", "break", "class", "double", "finally", "implements", "long", "protected", "strictfp", "throw", "volatile", "byte",
        "const", "else", "float", "import", "native", "public", "super", "throws", "while", "byValue", "cast", "false", "future", "generic", "inner", "null",
        "operator", "outer", "rest", "true", "var"};

    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");

    /**
     * 格式化参数到 query 形式
     * @param paramsMap 参数map
     * @return 字符串 eg: name=zhang&age=1
     * @throws BusinessException HTTP参数构造异常
     */
    public static String mapToQuerySearch(Map<String, Object> paramsMap) {
        return StringUtils.mapToQuerySearch(paramsMap, false);
    }

    /**
     * 格式化参数到 query 形式( 经过 url encode)
     * @param paramsMap 参数map
     * @param sort      是否key排序
     * @return 字符串 eg: name=zhang&age=1
     * @throws BusinessException HTTP参数构造异常
     */
    public static String mapToQuerySearch(Map<String, Object> paramsMap, boolean sort) {
        try {
            String reString;
            // 遍历数组形成akey=avalue&bkey=bvalue&ckey=cvalue形式的的字符串
            reString = StringUtils.realQueryBuild(paramsMap, "", true, sort);
            reString = StringUtils.rtrim(reString, "&");

            // 将得到的字符串进行处理得到目标格式的字符串：utf8处理中文出错
            reString = java.net.URLEncoder.encode(reString, "utf-8");
            reString = reString.replace("%3D", "=").replace("%26", "&");
            return reString;
        } catch (Throwable e) {
            throw new BusinessException(StatusCode.URL_ENCODE_ERROR, map -> map.put("paramsMap", paramsMap), e);
        }
    }

    /**
     * 递归解析map到query
     * @param object    对象
     * @param parentStr 里层分隔符
     * @param first     是否最外层
     * @param sort      是否排序
     * @return 字符串 eg: name=zhang&age=1
     */
    private static String realQueryBuild(Object object, String parentStr, boolean first, boolean sort) {
        StringBuilder r = new StringBuilder();
        if (object instanceof Map) {
            List<Map.Entry<String, Object>> list = new ArrayList<>(((Map<String, Object>) object).entrySet());
            // 按照map的key排序
            if (sort) {
                // 升序排序
                list.sort(Map.Entry.comparingByKey());
            }
            for (Map.Entry<String, Object> mapping : list) {
                String key = mapping.getKey();
                Object value = mapping.getValue();

                if (first) {
                    r.append(StringUtils.realQueryBuild(value, key, false, sort));
                } else {
                    r.append(StringUtils.realQueryBuild(value, parentStr + "[" + key + "]", false, sort));
                }
            }
        } else if (object instanceof List) {
            for (int i = 0; i < ((List) object).size(); i++) {
                r.append(StringUtils.realQueryBuild(((List) object).get(i), parentStr + "[" + i + "]", false, sort));
            }
            // 叶节点是String或者Number
        } else if (object instanceof String) {
            r.append(parentStr).append("=").append(object).append("&");
        } else if (object instanceof Number) {
            r.append(parentStr).append("=").append(object).append("&");
        }
        return r.toString();
    }

    /**
     * 将首字符转化为小写
     * @param str 原字符串
     * @return 字符串
     */
    public static String lowerFirstChar(String str) {
        char[] chars = str.toCharArray();
        final int a = 65;
        final int b = 90;
        if (chars[0] >= a && chars[0] <= b) {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    /**
     * 下划线转驼峰
     * @param str              原字符串
     * @param firstIsUpperCase 大驼峰 默认false
     * @return 处理后的字符
     */
    public static String lineToHump(String str, boolean... firstIsUpperCase) {
        str = StringUtils.ltrim(StringUtils.rtrim(str, "_"), "_");
        if (firstIsUpperCase.length != 0 && firstIsUpperCase[0]) {
            str = "_" + str;
        }
        Matcher matcher = StringUtils.LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 小驼峰转下划线
     * @param str 原字符串
     * @return 处理后的字符
     */
    public static String humpToLine(String str) {
        return StringUtils.humpToLine(str, "_");
    }

    /**
     * 小驼峰转下划线
     * @param str    原字符串
     * @param symbol 符号
     * @return 处理后的字符
     */
    public static String humpToLine(String str, String symbol) {
        Matcher matcher = StringUtils.HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, symbol + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return StringUtils.ltrim(sb.toString(), symbol);
    }

    /**
     * 移除字符串左侧的所有character
     * @param str       原字符串
     * @param character 将要移除的字符
     * @return 处理后的字符
     */
    public static String ltrim(String str, String character) {
        int length = character.length();
        if ("".equals(str) || str.length() < length) {
            return str;
        }
        return str.substring(0, length).equals(character) ? StringUtils.ltrim(str.substring(length), character) : str;
    }

    /**
     * 移除字符串右侧的所有character
     * @param str       原字符串
     * @param character 将要移除的字符
     * @return 处理后的字符
     */
    public static String rtrim(String str, String character) {
        int length = character.length();
        if ("".equals(str) || str.length() < length) {
            return str;
        }
        return str.substring(str.length() - length).equals(character) ? StringUtils.rtrim(str.substring(0, str.length() - length), character) : str;
    }

    /**
     * 是否是合法的标识符
     * @param input 输入字符串
     * @return 是否
     */
    public static boolean isJavaIdentifier(@Nullable String input) {
        if (input != null && input.length() > 0) {
            int pos = 0;
            if (Character.isJavaIdentifierStart(input.charAt(pos))) {
                while (++pos < input.length()) {
                    if (!Character.isJavaIdentifierPart(input.charAt(pos))) {
                        return false;
                    }
                }
                return !StringUtils.isJavaKeyword(input);
            }
        }
        return false;
    }

    /**
     * 是否为java关键字
     * @param input 输入字符串
     * @return 是否
     */
    public static boolean isJavaKeyword(String input) {
        List<String> keyList = Arrays.asList(StringUtils.JAVA_KEYWORDS);
        return keyList.contains(input);
    }

    /**
     * md5
     * @param input 输入
     * @return 输出
     */
    public static String md5(String input) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte[] btInput = input.getBytes(StandardCharsets.UTF_8);
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过BufferedReader和字符编码集转换成byte数组
     * @param br                 BufferedReader
     * @param originallyEncoding 原输入字符集
     * @return byte数组
     */
    public static byte[] readBytes(BufferedReader br, String originallyEncoding) {
        try {
            StringBuilder retStr = new StringBuilder();
            int read;
            while ((read = br.read()) != -1) {
                retStr.append((char) read);
            }
            if (retStr.length() > 0) {
                return retStr.toString().getBytes(Charset.forName(originallyEncoding));
            }
            return new byte[0];
        } catch (Throwable e) {
            throw new BusinessException(e);
        }
    }

    /**
     * InputStream 2 ByteArrayOutputStream
     * @param is InputStream
     * @return ByteArrayOutputStream
     */
    public static ByteArrayOutputStream readBytes(InputStream is) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i;
            while ((i = is.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            return byteArrayOutputStream;
        } catch (Throwable e) {
            throw new BusinessException(e);
        }
    }

    /**
     * 是否为空字符
     * @param str 字符串
     * @return bool
     */
    public static boolean hasText(@Nullable String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    /**
     * 是否为空字符
     * @param str 字符串
     * @return bool
     */
    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}

package gaarason.convention.common.util;

import gaarason.convention.common.models.exception.InternalException;
import gaarason.convention.common.models.pojo.TokenDTO;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import static gaarason.convention.common.models.exception.StatusCode.ARGUMENT_IS_NULL;
import static gaarason.convention.common.models.exception.StatusCode.NOT_ALLOW_INSTANCE;

/**
 * 通用服务标准token实现
 */
public final class TokenUtils {

    private TokenUtils() {
        throw new InternalException(NOT_ALLOW_INSTANCE);
    }

    /**
     * 签名
     *
     * @param appId
     * @param secret
     * @param timestamp
     * @param bizId      如果之后要用于插入输入的id，则为id。否则当作一次性的随机nonce即可
     * @param attachment 附属信息，没有可以不传
     * @return
     */
    public static String sign(String appId, String secret, long timestamp, long bizId, @Nullable Map<String, String> attachment) {
        if (!StringUtils.hasText(appId) || !StringUtils.hasText(secret) || timestamp == 0L || bizId == 0L) {
            throw new InternalException(ARGUMENT_IS_NULL, "appId,secret不允许为空；timestamp,bizId不允许为0");
        }
        //按字符排序
        TreeMap<String, String> paramsMap = getParamMap(appId, secret, timestamp, bizId, attachment);

        //合并成字符串
        String params = paramsMap.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((str1, str2) -> str1 + "&" + str2)
                .get();


        return DigestUtils.md5DigestAsHex(params.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 校验
     */
    public static boolean verify(String sign, String appId, String secret, long timestamp, long bizId, Map<String, String> attachment) {
        return sign.equals(sign(appId, secret, timestamp, bizId, attachment));
    }

    public static boolean verify(TokenDTO tokenDTO, String secret) {
        return tokenDTO.getSign().equals(sign(tokenDTO.getAppId(), secret, tokenDTO.getTimestamp(), tokenDTO.getBizId(), tokenDTO.getAttachment()));
    }

    /**
     * 获取token
     */
    public static String getToken(String password, String appId, String secret, long timestamp, long bizId, Map<String, String> attachment) {
        String sign = sign(appId, secret, timestamp, bizId, attachment);
        if (!StringUtils.hasText(password)) {
            throw new InternalException(ARGUMENT_IS_NULL, "password不允许为空");
        }
        TokenDTO tokenDTO = new TokenDTO(appId, timestamp, bizId, sign, attachment);

        //按字符排序
        String json = JsonUtils.objectToJson(tokenDTO);
        return Base64.getEncoder().encodeToString(encryptAES(json, password));
    }

    /**
     * 解密token
     */
    public static TokenDTO decryptToken(String token, String password) {
        if (!StringUtils.hasText(token)) {
            throw new InternalException(ARGUMENT_IS_NULL, "token不允许为空");
        }
        if (!StringUtils.hasText(password)) {
            throw new InternalException(ARGUMENT_IS_NULL, "password不允许为空");
        }
        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String json = new String(decryptAES(decodedBytes, password));
        return JsonUtils.jsonToObject(json, TokenDTO.class);
    }

    private static byte[] decryptAES(byte[] content, String password) {
        try {
            SecretKeySpec key = new SecretKeySpec(pwdHandler(password), "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encryptAES(String content, String password) {
        try {
            SecretKeySpec key = new SecretKeySpec(pwdHandler(password), "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 密码处理方法
     * 如果加解密出问题，
     * 请先查看本方法，排除密码长度不足补"\0",导致密码不一致
     *
     * @param password 待处理的密码
     * @return
     * @throws UnsupportedEncodingException
     */
    private static byte[] pwdHandler(String password) throws UnsupportedEncodingException {
        /*
         * 设置加密密码处理长度。
         * 不足此长度补\0；
         */
        final int PWD_SIZE = 16;

        byte[] data = null;
        if (password == null) {
            password = "";
        }
        StringBuilder sb = new StringBuilder(PWD_SIZE);
        sb.append(password);
        while (sb.length() < PWD_SIZE) {
            sb.append("\0");
        }
        if (sb.length() > PWD_SIZE) {
            sb.setLength(PWD_SIZE);
        }

        data = sb.toString().getBytes("UTF-8");

        return data;
    }

    private static TreeMap<String, String> getParamMap(String appId, @Nullable String secret, long timestamp, long bizId, @Nullable Map<String, String> attachment) {
        TreeMap<String, String> paramsMap = new TreeMap<>();
        paramsMap.put("appId", appId);
        if (secret != null) {
            paramsMap.put("secret", secret);
        }
        paramsMap.put("timestamp", timestamp + "");
        paramsMap.put("bizId", bizId + "");
        if (!CollectionUtils.isEmpty(attachment)) {
            paramsMap.putAll(attachment);
        }
        return paramsMap;
    }

}

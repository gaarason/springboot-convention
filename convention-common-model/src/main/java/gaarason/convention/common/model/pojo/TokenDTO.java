package gaarason.convention.common.model.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TokenDTO {

    private String appId;

    private long timestamp;

    private long bizId;

    private String sign;

    private Map<String, String> attachment;

    public TokenDTO() {
    }

    public TokenDTO(String appId, long timestamp, long bizId, String sign, Map<String, String> attachment) {
        this.appId = appId;
        this.timestamp = timestamp;
        this.bizId = bizId;
        this.sign = sign;
        this.attachment = attachment;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getBizId() {
        return bizId;
    }

    public void setBizId(long bizId) {
        this.bizId = bizId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }
}

package gaarason.convention.test.common.web.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.validation.annotation.Now;
import gaarason.convention.validation.annotation.NumberRange;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author xt
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JwtTokenRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 平台的用户 Id
     * @ApiModelProperty (value = " 开放平台的用户Id ", example = " 39e0f74f - 93fd - 224c - 0078 - 988542600fd3 ", required = true)
     */
    @Length(min = 36, max = 36, message = "用户Id[id]长度必须是36")
    private String id;

    /**
     * 请求的当前秒级时间戳
     */
    @Now(permissibleError = 3000, nullable = true)
    private Integer timestamp;

    /**
     * token的有效期
     */
    @NumberRange(min = 1, max = 7200)
    @JsonProperty("valid_time")
    private int validTime;

    /**
     * 参数的签名
     */
    private String sign;

    /**
     * 枚举类型
     */
    private FinalVariable.Http.Method method;

    /**
     * 数组
     */
    private List<String> names;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public int getValidTime() {
        return validTime;
    }

    public void setValidTime(int validTime) {
        this.validTime = validTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public FinalVariable.Http.Method getMethod() {
        return method;
    }

    public void setMethod(FinalVariable.Http.Method method) {
        this.method = method;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return "JwtTokenRequestDto{" + "id='" + id + '\'' + ", timestamp=" + timestamp + ", validTime=" + validTime + ", sign='" + sign + '\'' + ", method=" + method
            + ", names=" + names + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JwtTokenRequestDto that = (JwtTokenRequestDto) o;
        return validTime == that.validTime && Objects.equals(id, that.id) && Objects.equals(timestamp, that.timestamp) && Objects.equals(sign,
            that.sign)
            && method == that.method && Objects.equals(names, that.names);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, validTime, sign, method, names);
    }
}

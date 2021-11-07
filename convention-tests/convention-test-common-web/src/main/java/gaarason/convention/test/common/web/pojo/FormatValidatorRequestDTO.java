package gaarason.convention.test.common.web.pojo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.validation.annotation.DateTimeFormat;
import gaarason.convention.validation.annotation.Format;

import java.io.Serializable;

/**
 * @author xt
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FormatValidatorRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;

    @Format(formatterPattern = FinalVariable.RegularExpressions.EMAIL)
    private String email;

    @Format(formatterPattern = FinalVariable.RegularExpressions.MOBILE_NUMBER_CH)
    private String mobileNumberCh;

    @Format(formatterPattern = FinalVariable.RegularExpressions.PHONE_NUMBER_CH)
    private String phoneNumberCh;

    @Format(formatterPattern = FinalVariable.RegularExpressions.ID_CARD_CH)
    private String idCardCn;

    @Format(formatterPattern = FinalVariable.RegularExpressions.QQ)
    private String qq;

    @Format(formatterPattern = FinalVariable.RegularExpressions.POSTCODE_CH)
    private String postcodeCh;

    @Format(formatterPattern = FinalVariable.RegularExpressions.IP)
    private String ip;

    /**
     * 判断时间格式 格式必须为“YYYY-MM-dd” 2004-2-30 是无效的 2003-2-29 是无效的
     * 2007-01-02 才是合法的
     */
    @DateTimeFormat(dateTimeFormatterPattern = "yyyy-MM-dd")
    private String datetime;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumberCh() {
        return mobileNumberCh;
    }

    public void setMobileNumberCh(String mobileNumberCh) {
        this.mobileNumberCh = mobileNumberCh;
    }

    public String getPhoneNumberCh() {
        return phoneNumberCh;
    }

    public void setPhoneNumberCh(String phoneNumberCh) {
        this.phoneNumberCh = phoneNumberCh;
    }

    public String getIdCardCn() {
        return idCardCn;
    }

    public void setIdCardCn(String idCardCn) {
        this.idCardCn = idCardCn;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPostcodeCh() {
        return postcodeCh;
    }

    public void setPostcodeCh(String postcodeCh) {
        this.postcodeCh = postcodeCh;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "FormatValidatorRequestDTO{" + "requestId='" + requestId + '\'' + ", email='" + email + '\'' + ", mobileNumberCh='" + mobileNumberCh + '\''
            + ", phoneNumberCh='" + phoneNumberCh + '\'' + ", idCardCn='" + idCardCn + '\'' + ", qq='" + qq + '\'' + ", postcodeCh='" + postcodeCh + '\''
            + ", ip='" + ip + '\'' + ", datetime='" + datetime + '\'' + '}';
    }
}

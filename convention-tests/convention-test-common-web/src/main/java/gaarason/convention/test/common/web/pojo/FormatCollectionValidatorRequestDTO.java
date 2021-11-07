package gaarason.convention.test.common.web.pojo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gaarason.convention.common.appointment.FinalVariable;
import gaarason.convention.validation.annotation.Format;

import java.io.Serializable;
import java.util.List;

/**
 * @author xt
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FormatCollectionValidatorRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;

    @Format(formatterPattern = FinalVariable.RegularExpressions.EMAIL)
    private List<String> emails;

    @Format(formatterPattern = FinalVariable.RegularExpressions.MOBILE_NUMBER_CH)
    private List<String> mobileNumberChs;

    @Format(formatterPattern = FinalVariable.RegularExpressions.PHONE_NUMBER_CH)
    private List<String> phoneNumberChs;

    @Format(formatterPattern = FinalVariable.RegularExpressions.ID_CARD_CH)
    private List<String> idCardCns;

    @Format(formatterPattern = FinalVariable.RegularExpressions.QQ)
    private List<String> qqs;

    @Format(formatterPattern = FinalVariable.RegularExpressions.POSTCODE_CH)
    private List<String> postcodeChs;

    @Format(formatterPattern = FinalVariable.RegularExpressions.IP)
    private List<String> ips;

    @Format.List({@Format(formatterPattern = FinalVariable.RegularExpressions.IP), @Format(formatterPattern = FinalVariable.RegularExpressions.IP)})
    private List<String> ip2s;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getMobileNumberChs() {
        return mobileNumberChs;
    }

    public void setMobileNumberChs(List<String> mobileNumberChs) {
        this.mobileNumberChs = mobileNumberChs;
    }

    public List<String> getPhoneNumberChs() {
        return phoneNumberChs;
    }

    public void setPhoneNumberChs(List<String> phoneNumberChs) {
        this.phoneNumberChs = phoneNumberChs;
    }

    public List<String> getIdCardCns() {
        return idCardCns;
    }

    public void setIdCardCns(List<String> idCardCns) {
        this.idCardCns = idCardCns;
    }

    public List<String> getQqs() {
        return qqs;
    }

    public void setQqs(List<String> qqs) {
        this.qqs = qqs;
    }

    public List<String> getPostcodeChs() {
        return postcodeChs;
    }

    public void setPostcodeChs(List<String> postcodeChs) {
        this.postcodeChs = postcodeChs;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }

    public List<String> getIp2s() {
        return ip2s;
    }

    public void setIp2s(List<String> ip2s) {
        this.ip2s = ip2s;
    }

    @Override
    public String toString() {
        return "FormatCollectionValidatorRequestDTO{" + "requestId='" + requestId + '\'' + ", emails=" + emails + ", mobileNumberChs=" + mobileNumberChs
            + ", phoneNumberChs=" + phoneNumberChs + ", idCardCns=" + idCardCns + ", qqs=" + qqs + ", postcodeChs=" + postcodeChs + ", ips=" + ips + ", ip2s="
            + ip2s + '}';
    }
}

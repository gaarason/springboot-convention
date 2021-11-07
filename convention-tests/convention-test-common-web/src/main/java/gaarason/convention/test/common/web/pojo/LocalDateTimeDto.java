package gaarason.convention.test.common.web.pojo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * @author xt
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LocalDateTimeDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDate theLocalDate;

    private LocalTime theLocalTime;

    private LocalDateTime theLocalDateTime;

    public LocalDate getTheLocalDate() {
        return theLocalDate;
    }

    public void setTheLocalDate(LocalDate theLocalDate) {
        this.theLocalDate = theLocalDate;
    }

    public LocalTime getTheLocalTime() {
        return theLocalTime;
    }

    public void setTheLocalTime(LocalTime theLocalTime) {
        this.theLocalTime = theLocalTime;
    }

    public LocalDateTime getTheLocalDateTime() {
        return theLocalDateTime;
    }

    public void setTheLocalDateTime(LocalDateTime theLocalDateTime) {
        this.theLocalDateTime = theLocalDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocalDateTimeDto that = (LocalDateTimeDto) o;
        return Objects.equals(theLocalDate, that.theLocalDate) && Objects.equals(theLocalTime, that.theLocalTime)
                && Objects.equals(theLocalDateTime, that.theLocalDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theLocalDate, theLocalTime, theLocalDateTime);
    }
}

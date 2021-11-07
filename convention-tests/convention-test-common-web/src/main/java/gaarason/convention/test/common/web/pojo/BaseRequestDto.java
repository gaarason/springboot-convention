package gaarason.convention.test.common.web.pojo;

import java.io.Serializable;

/**
 * @param <T> 实体
 * @author xt
 */
public class BaseRequestDto<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient T data;

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }
}

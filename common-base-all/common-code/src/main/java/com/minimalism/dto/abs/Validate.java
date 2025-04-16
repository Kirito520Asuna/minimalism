package com.minimalism.dto.abs;

public interface Validate<T> {
    /**
     * 校验是否通过
     *
     * @param t
     * @return
     */
    boolean validateOk(T t);

    default boolean validateOk() {
        return validateOk((T) this);
    }
}

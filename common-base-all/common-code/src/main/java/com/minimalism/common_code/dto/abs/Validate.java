package com.minimalism.common_code.dto.abs;

import com.minimalism.base.exception.ValidateException;


public interface Validate<T> {
    /**
     * 校验是否通过
     *
     * @param t
     * @return
     */
    boolean validateOk(T t);

    default boolean validateOk() {
        boolean validateOk = validateOk((T) this);
        if (!validateOk) {
            throw new ValidateException();
        }
        return validateOk;
    }
}

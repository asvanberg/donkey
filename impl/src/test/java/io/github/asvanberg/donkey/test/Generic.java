package io.github.asvanberg.donkey.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class Generic<T> {
    Type type() {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}

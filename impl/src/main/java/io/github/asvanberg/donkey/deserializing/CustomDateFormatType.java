package io.github.asvanberg.donkey.deserializing;

import java.lang.reflect.Type;

record CustomDateFormatType(String pattern, String locale, Class<?> temporalQuery) implements Type {
}

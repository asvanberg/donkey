package io.github.asvanberg.donkey.internal;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Util
{
    public static Optional<ParameterizedType> getParameterizedType(
            final Object object,
            final Class<?> interfaceClass)
    {
        return Stream.<Class<?>>iterate(object.getClass(), Predicate.not(Object.class::equals), Class::getSuperclass)
                     .filter(Objects::nonNull)
                     .map(Class::getGenericInterfaces)
                     .flatMap(Arrays::stream)
                     .filter(ParameterizedType.class::isInstance)
                     .map(ParameterizedType.class::cast)
                     .filter(pt -> interfaceClass.equals(pt.getRawType()))
                     .findAny();
    }
}

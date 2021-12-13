package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.JsonbDeserializer;

import java.lang.reflect.Type;
import java.util.function.Function;

record ParameterizedDeserializer(
        Class<?> clazz,
        Function<Type[], JsonbDeserializer<?>> factory)
{
}

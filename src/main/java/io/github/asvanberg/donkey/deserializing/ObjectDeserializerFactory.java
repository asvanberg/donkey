package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.JsonbDeserializer;

import java.util.function.Function;

class ObjectDeserializerFactory<T>
        implements Function<ParserHistory, JsonbDeserializer<?>>
{
    private final Creator<T> creator;

    ObjectDeserializerFactory(final Class<T> clazz) {
        this.creator = Creator.forClass(clazz);
    }

    @Override
    public JsonbDeserializer<?> apply(final ParserHistory parserHistory) {
        return new ObjectDeserializer<>(parserHistory, creator);
    }
}

package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.Optional;

class OptionalDeserializer<T> extends NullableDeserializer<Optional<T>> {
    private final Type type;

    OptionalDeserializer(final Type[] type) {
        this.type = type[0];
    }

    @Override
    protected Optional<T> getValue(
            final JsonParser parser,
            final DeserializationContext ctx) {
        return Optional.ofNullable(ctx.deserialize(type, parser));
    }

    @Override
    protected Optional<T> nullValue() {
        return Optional.empty();
    }
}

package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.util.Optional;

public enum OptionalSerializer implements JsonbSerializer<Optional<?>>
{
    INSTANCE;

    @Override
    public void serialize(final Optional<?> obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        if (obj.isPresent()) {
            ctx.serialize(obj.get(), generator);
        }
        else {
            generator.writeNull();
        }
    }
}

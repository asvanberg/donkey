package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.util.Map;
import java.util.Objects;

public enum MapSerializer implements JsonbSerializer<Map<?, ?>>
{
    INSTANCE;

    @Override
    public void serialize(
            final Map<?, ?> obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.writeStartObject();
        obj.forEach((key, value) -> ctx.serialize(Objects.toString(key), value, generator));
        generator.writeEnd();
    }
}

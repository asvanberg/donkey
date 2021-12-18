package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.util.Collection;

public enum CollectionSerializer implements JsonbSerializer<Collection<?>> {
    INSTANCE;

    @Override
    public void serialize(final Collection<?> obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.writeStartArray();
        for (Object o : obj) {
            ctx.serialize(o, generator);
        }
        generator.writeEnd();
    }
}

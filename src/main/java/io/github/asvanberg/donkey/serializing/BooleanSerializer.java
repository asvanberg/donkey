package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public enum BooleanSerializer implements JsonbSerializer<Boolean> {
    INSTANCE;

    @Override
    public void serialize(final Boolean obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }
}

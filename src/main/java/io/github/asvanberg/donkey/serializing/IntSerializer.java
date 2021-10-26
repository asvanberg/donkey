package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public enum IntSerializer implements JsonbSerializer<Integer> {
    INSTANCE;

    @Override
    public void serialize(final Integer obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }
}

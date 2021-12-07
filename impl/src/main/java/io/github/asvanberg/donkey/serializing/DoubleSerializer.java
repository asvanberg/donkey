package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public enum DoubleSerializer implements JsonbSerializer<Double> {
    INSTANCE;

    @Override
    public void serialize(final Double obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }
}

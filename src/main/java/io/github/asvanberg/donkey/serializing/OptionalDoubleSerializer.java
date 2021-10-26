package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.util.OptionalDouble;

public enum OptionalDoubleSerializer implements JsonbSerializer<OptionalDouble> {
    INSTANCE;

    @Override
    public void serialize(final OptionalDouble obj, final JsonGenerator generator, final SerializationContext ctx) {
        if (obj.isPresent()) {
            generator.write(obj.getAsDouble());
        }
        else {
            generator.writeNull();
        }
    }
}

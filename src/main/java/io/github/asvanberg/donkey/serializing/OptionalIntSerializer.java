package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.util.OptionalInt;

public enum OptionalIntSerializer implements JsonbSerializer<OptionalInt> {
    INSTANCE;

    @Override
    public void serialize(final OptionalInt obj, final JsonGenerator generator, final SerializationContext ctx) {
        if (obj.isPresent()) {
            generator.write(obj.getAsInt());
        }
        else {
            generator.writeNull();
        }
    }
}

package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.util.OptionalLong;

public enum OptionalLongSerializer implements JsonbSerializer<OptionalLong> {
    INSTANCE;

    @Override
    public void serialize(final OptionalLong obj, final JsonGenerator generator, final SerializationContext ctx) {
        if (obj.isPresent()) {
            generator.write(obj.getAsLong());
        }
        else {
            generator.writeNull();
        }
    }
}

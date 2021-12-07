package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public enum StringSerializer implements JsonbSerializer<String> {
    INSTANCE;

    @Override
    public void serialize(final String obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }
}

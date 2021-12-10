package io.github.asvanberg.donkey.serializing;

import jakarta.json.JsonValue;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public enum JsonValueSerializer implements JsonbSerializer<JsonValue> {
    INSTANCE;

    @Override
    public void serialize(final JsonValue obj, final JsonGenerator generator, final SerializationContext ctx) {
        generator.write(obj);
    }
}

package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public enum InstantSerializer implements JsonbSerializer<Instant> {
    INSTANCE;

    @Override
    public void serialize(
            final Instant obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(DateTimeFormatter.ISO_INSTANT.format(obj));
    }
}

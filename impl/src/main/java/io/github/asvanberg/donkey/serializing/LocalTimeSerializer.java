package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

enum LocalTimeSerializer implements JsonbSerializer<LocalTime> {
    INSTANCE;

    @Override
    public void serialize(
            final LocalTime obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(DateTimeFormatter.ISO_LOCAL_TIME.format(obj));
    }
}

package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

enum LocalDateTimeSerializer implements JsonbSerializer<LocalDateTime> {
    INSTANCE;

    @Override
    public void serialize(
            final LocalDateTime obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(obj));
    }
}

package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public enum OffsetDateTimeSerializer implements JsonbSerializer<OffsetDateTime> {
    INSTANCE;

    @Override
    public void serialize(
            final OffsetDateTime obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        //generator.write(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").format(obj));
        generator.write(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(obj));
    }
}

package io.github.asvanberg.donkey.serializing;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import java.time.LocalDate;

enum LocalDateSerializer implements JsonbSerializer<LocalDate> {
    INSTANCE;

    @Override
    public void serialize(
            final LocalDate obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(obj.toString());
    }
}

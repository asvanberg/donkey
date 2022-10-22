package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum LocalDateTimeCodec implements JsonbCodec<LocalDateTime> {
    INSTANCE;

    @Override
    public void serialize(
            final LocalDateTime obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(obj));
    }

    @Override
    public LocalDateTime deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_STRING, parser);
        return LocalDateTime.parse(parser.getString());
    }
}

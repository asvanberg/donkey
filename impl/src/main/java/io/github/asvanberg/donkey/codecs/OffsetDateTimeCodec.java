package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public enum OffsetDateTimeCodec implements JsonbCodec<OffsetDateTime> {
    INSTANCE;

    @Override
    public void serialize(
            final OffsetDateTime obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(obj));
    }

    @Override
    public OffsetDateTime deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_STRING, parser);
        return OffsetDateTime.parse(parser.getString());
    }
}

package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public enum InstantCodec implements JsonbCodec<Instant> {
    INSTANCE;

    @Override
    public void serialize(
            final Instant obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(DateTimeFormatter.ISO_INSTANT.format(obj));
    }

    @Override
    public Instant deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_STRING, parser);
        return Instant.parse(parser.getString());
    }
}

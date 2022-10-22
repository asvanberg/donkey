package io.github.asvanberg.donkey.codecs;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.LocalDate;

public enum LocalDateCodec implements JsonbCodec<LocalDate> {
    INSTANCE;

    @Override
    public void serialize(
            final LocalDate obj, final JsonGenerator generator, final SerializationContext ctx)
    {
        generator.write(obj.toString());
    }

    @Override
    public LocalDate deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        Util.assertCurrentParserPosition(JsonParser.Event.VALUE_STRING, parser);
        return LocalDate.parse(parser.getString());
    }
}

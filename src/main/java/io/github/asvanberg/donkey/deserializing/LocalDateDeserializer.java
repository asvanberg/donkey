package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.LocalDate;

class LocalDateDeserializer extends BaseDeserializer<LocalDate> {
    LocalDateDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public LocalDate deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_STRING);
        return LocalDate.parse(parser.getString());
    }
}
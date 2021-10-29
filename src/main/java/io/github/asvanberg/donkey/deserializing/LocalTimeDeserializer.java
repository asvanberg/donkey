package io.github.asvanberg.donkey.deserializing;

import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.time.LocalTime;

class LocalTimeDeserializer extends BaseDeserializer<LocalTime> {
    LocalTimeDeserializer(final ParserHistory parserHistory) {
        super(parserHistory);
    }

    @Override
    public LocalTime deserialize(
            final JsonParser parser, final DeserializationContext ctx, final Type rtType)
    {
        assertCurrentParserPosition(JsonParser.Event.VALUE_STRING);
        return LocalTime.parse(parser.getString());
    }
}
